package com.ezu.payment.application;

import com.ezu.common.exception.BusinessException;
import com.ezu.common.util.NoGenerator;
import com.ezu.order.domain.enums.OrderStatus;
import com.ezu.order.domain.vo.PaymentContextVo;
import com.ezu.order.domainService.OrderDomainService;
import com.ezu.payment.channel.PaymentChannel;
import com.ezu.payment.domain.entity.PaymentRecord;
import com.ezu.payment.domain.enums.BizType;
import com.ezu.payment.domain.enums.PayChannel;
import com.ezu.payment.domain.enums.PayStatus;
import com.ezu.payment.domain.req.PayReq;
import com.ezu.payment.domain.req.PaymentCallbackReq;
import com.ezu.payment.domain.vo.PayVo;
import com.ezu.payment.repository.PaymentRecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 支付应用服务：发起支付（幂等复用）+ 统一回调（驱动订单状态推进）
 * <p>
 * 「发起支付」与「渠道回调」共用 callback 落库链路，二期真实渠道只换 Channel 实现
 */
@Slf4j
@Service
public class PaymentApplicationService {

    /** 支付单号前缀 */
    private static final String NO_PREFIX_PAYMENT = "P";

    /** 同订单支付互斥锁 key 前缀（防并发 pay 重复建单） */
    private static final String PAY_LOCK_PREFIX = "ezu:lock:pay:";

    /** 支付锁超时（须大于单次 pay 事务时长，异常时兜底自动释放） */
    private static final Duration PAY_LOCK_TIMEOUT = Duration.ofSeconds(10);

    private final PaymentRecordRepository paymentRecordRepository;
    private final OrderDomainService orderDomainService;
    private final NoGenerator noGenerator;
    private final StringRedisTemplate stringRedisTemplate;
    private final Map<Integer, PaymentChannel> channelMap;

    public PaymentApplicationService(PaymentRecordRepository paymentRecordRepository,
                                     OrderDomainService orderDomainService,
                                     NoGenerator noGenerator,
                                     StringRedisTemplate stringRedisTemplate,
                                     List<PaymentChannel> channels) {
        this.paymentRecordRepository = paymentRecordRepository;
        this.orderDomainService = orderDomainService;
        this.noGenerator = noGenerator;
        this.stringRedisTemplate = stringRedisTemplate;
        this.channelMap = channels.stream()
                .collect(Collectors.toMap(PaymentChannel::channelCode, Function.identity()));
    }

    /**
     * C端发起支付（mock 渠道受理即回调成功，同一事务）：
     * 待支付订单复用未完成支付单否则新建；已待发货且存在成功单幂等返回。
     * <p>
     * 同订单以 Redis 锁互斥，防并发 pay 各建一条支付单双双回调成功（真实渠道即重复扣款）；
     * 锁在事务提交/回滚后释放（afterCompletion），避免「释放后、提交前」的竞态窗口。
     * 注意：本方法自调用 callback(...) 不经事务代理，依赖 pay 自身的 @Transactional（REQUIRED 语义）；
     * 若二期将 callback 改为 REQUIRES_NEW 或去除 pay 事务，需把内部驱动抽到独立 Bean。
     */
    @Transactional(rollbackFor = Exception.class)
    public PayVo pay(Long userId, PayReq req) {
        String lockValue = lockPay(req.getOrderNo());
        try {
            return doPay(userId, req);
        } finally {
            unlockPayAfterCommit(req.getOrderNo(), lockValue);
        }
    }

    private PayVo doPay(Long userId, PayReq req) {
        PaymentContextVo ctx = orderDomainService.getPaymentContext(req.getOrderNo());
        if (ctx == null || !Objects.equals(ctx.getUserId(), userId)) {
            // 与订单侧一致：不泄露他人订单存在性
            throw new BusinessException("订单【{}】不存在", req.getOrderNo());
        }
        PaymentRecord record;
        if (Objects.equals(ctx.getStatus(), OrderStatus.WAIT_PAY)) {
            record = reuseOrCreate(ctx);
        } else if (Objects.equals(ctx.getStatus(), OrderStatus.WAIT_DELIVER)) {
            // 已支付：幂等返回原成功单
            PaymentRecord success = paymentRecordRepository.getSuccessByOrderNo(ctx.getOrderNo());
            if (success != null) {
                return toVo(success);
            }
            throw new BusinessException("订单状态不允许支付");
        } else {
            throw new BusinessException("订单状态不允许支付");
        }
        PaymentChannel channel = requireChannel(record.getChannel());
        boolean accepted = channel.pay(record);
        if (!accepted) {
            paymentRecordRepository.lambdaUpdate()
                    .eq(PaymentRecord::getId, record.getId())
                    .eq(PaymentRecord::getStatus, PayStatus.WAIT_PAY)
                    .set(PaymentRecord::getStatus, PayStatus.FAIL)
                    .update();
            throw new BusinessException("支付渠道受理失败，请稍后重试");
        }
        // 受理成功 → 统一走回调链路落库并驱动订单（自调用加入当前事务）
        PaymentCallbackReq callbackReq = new PaymentCallbackReq();
        callbackReq.setPaymentNo(record.getPaymentNo());
        callback(channel.channelCode(), callbackReq);
        return toVo(paymentRecordRepository.getByPaymentNo(record.getPaymentNo()));
    }

    /**
     * 支付回调（真实渠道外部回调 / mock 内部驱动共用）：条件更新防并发重复回调（幂等）；
     * biz_type=租金 时同事务调 onPaymentSuccess 推进订单
     */
    @Transactional(rollbackFor = Exception.class)
    public void callback(Integer channelCode, PaymentCallbackReq req) {
        PaymentRecord record = paymentRecordRepository.getByPaymentNo(req.getPaymentNo());
        // 未知单号忽略（真实渠道重试/伪造请求均不应报错）
        if (record == null) {
            log.warn("支付回调忽略：支付单【{}】不存在", req.getPaymentNo());
            return;
        }
        if (!Objects.equals(record.getChannel(), channelCode)) {
            // 与未知单号同策略：不向未认证调用方泄露单号存在性
            log.warn("支付回调忽略：支付单【{}】渠道不匹配（期望{}，回调{}）",
                    req.getPaymentNo(), record.getChannel(), channelCode);
            return;
        }
        // 已处理直接返回（幂等）
        if (!Objects.equals(record.getStatus(), PayStatus.WAIT_PAY)) {
            return;
        }
        boolean updated = paymentRecordRepository.lambdaUpdate()
                .eq(PaymentRecord::getId, record.getId())
                .eq(PaymentRecord::getStatus, PayStatus.WAIT_PAY)
                .set(PaymentRecord::getStatus, PayStatus.SUCCESS)
                .set(PaymentRecord::getCallbackTime, LocalDateTime.now())
                .set(req.getChannelTradeNo() != null, PaymentRecord::getChannelTradeNo, req.getChannelTradeNo())
                .update();
        if (!updated) {
            return;
        }
        if (Objects.equals(record.getBizType(), BizType.RENT)) {
            // billNo 透传：账单按支付单关联账单精确置付
            orderDomainService.onPaymentSuccess(record.getOrderNo(), record.getBillNo());
        }
    }

    /**
     * 复用未完成支付单，否则新建（P单号，金额=应付，租金业务，mock 渠道）
     */
    private PaymentRecord reuseOrCreate(PaymentContextVo ctx) {
        PaymentRecord waiting = paymentRecordRepository.getWaitingByOrderNo(ctx.getOrderNo());
        if (waiting != null) {
            return waiting;
        }
        PaymentRecord record = new PaymentRecord();
        record.setPaymentNo(noGenerator.next(NO_PREFIX_PAYMENT));
        record.setOrderNo(ctx.getOrderNo());
        record.setBillNo(ctx.getUnpaidBillNo());
        record.setUserId(ctx.getUserId());
        record.setBizType(BizType.RENT);
        record.setAmount(ctx.getPayAmount());
        record.setChannel(PayChannel.MOCK);
        record.setStatus(PayStatus.WAIT_PAY);
        paymentRecordRepository.save(record);
        return record;
    }

    /**
     * 同订单支付互斥（一期单机 Redis；二期真实渠道建议换数据库活跃单唯一约束）
     */
    private String lockPay(String orderNo) {
        String lockValue = UUID.randomUUID().toString();
        Boolean locked = stringRedisTemplate.opsForValue()
                .setIfAbsent(PAY_LOCK_PREFIX + orderNo, lockValue, PAY_LOCK_TIMEOUT);
        if (!Boolean.TRUE.equals(locked)) {
            throw new BusinessException("支付处理中，请勿重复操作");
        }
        return lockValue;
    }

    /**
     * 事务完成（提交或回滚）后再释放锁，避免释放后提交前的竞态窗口；
     * 无事务上下文（理论不发生）时立即释放
     */
    private void unlockPayAfterCommit(String orderNo, String lockValue) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                    unlock(orderNo, lockValue);
                }
            });
        } else {
            unlock(orderNo, lockValue);
        }
    }

    private void unlock(String orderNo, String lockValue) {
        String key = PAY_LOCK_PREFIX + orderNo;
        try {
            if (lockValue.equals(stringRedisTemplate.opsForValue().get(key))) {
                stringRedisTemplate.delete(key);
            }
        } catch (Exception e) {
            // 锁有 TTL 兜底，释放失败仅记日志不影响业务
            log.error("支付锁释放失败：orderNo={}", orderNo, e);
        }
    }

    private PaymentChannel requireChannel(Integer channelCode) {
        PaymentChannel channel = channelMap.get(channelCode);
        if (channel == null) {
            throw new BusinessException("支付渠道【{}】未接入", channelCode);
        }
        return channel;
    }

    private PayVo toVo(PaymentRecord record) {
        PayVo vo = new PayVo();
        vo.setPaymentNo(record.getPaymentNo());
        vo.setStatus(record.getStatus());
        return vo;
    }
}
