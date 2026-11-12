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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    private final PaymentRecordRepository paymentRecordRepository;
    private final OrderDomainService orderDomainService;
    private final NoGenerator noGenerator;
    private final Map<Integer, PaymentChannel> channelMap;

    public PaymentApplicationService(PaymentRecordRepository paymentRecordRepository,
                                     OrderDomainService orderDomainService,
                                     NoGenerator noGenerator,
                                     List<PaymentChannel> channels) {
        this.paymentRecordRepository = paymentRecordRepository;
        this.orderDomainService = orderDomainService;
        this.noGenerator = noGenerator;
        this.channelMap = channels.stream()
                .collect(Collectors.toMap(PaymentChannel::channelCode, Function.identity()));
    }

    /**
     * C端发起支付（mock 渠道受理即回调成功，同一事务）：
     * 待支付订单复用未完成支付单否则新建；已待发货且存在成功单幂等返回
     */
    @Transactional(rollbackFor = Exception.class)
    public PayVo pay(Long userId, PayReq req) {
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
        // 受理成功 → 统一走回调链路落库并驱动订单（REQUIRED 加入当前事务）
        PaymentCallbackReq callbackReq = new PaymentCallbackReq();
        callbackReq.setPaymentNo(record.getPaymentNo());
        callback(channel.channelCode(), callbackReq);
        return toVo(paymentRecordRepository.getByPaymentNo(record.getPaymentNo()));
    }

    /**
     * 支付回调（渠道回调 / mock 内部驱动共用）：条件更新防并发重复回调（幂等）；
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
            throw new BusinessException("回调渠道【{}】与支付单渠道不一致", channelCode);
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
            orderDomainService.onPaymentSuccess(record.getOrderNo());
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
