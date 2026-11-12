package com.ezu.order.domainService.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ezu.common.exception.BusinessException;
import com.ezu.common.page.PageVo;
import com.ezu.common.util.NoGenerator;
import com.ezu.order.domain.entity.Bill;
import com.ezu.order.domain.entity.DepositRecord;
import com.ezu.order.domain.entity.Lease;
import com.ezu.order.domain.entity.Logistics;
import com.ezu.order.domain.entity.Order;
import com.ezu.order.domain.entity.OrderItem;
import com.ezu.order.domain.enums.BillStatus;
import com.ezu.order.domain.enums.BillType;
import com.ezu.order.domain.enums.DepositType;
import com.ezu.order.domain.enums.LeaseType;
import com.ezu.order.domain.enums.LogisticsStatus;
import com.ezu.order.domain.enums.LogisticsType;
import com.ezu.order.domain.enums.OrderStatus;
import com.ezu.order.domain.req.AcceptCheckReq;
import com.ezu.order.domain.req.AdminOrderQueryReq;
import com.ezu.order.domain.req.LogisticsInfoReq;
import com.ezu.order.domain.req.OrderCreateReq;
import com.ezu.order.domain.req.OrderQueryReq;
import com.ezu.order.domain.vo.OrderCreateVo;
import com.ezu.order.domain.vo.OrderDetailVo;
import com.ezu.order.domain.vo.OrderListVo;
import com.ezu.order.domain.vo.OrderTrialVo;
import com.ezu.order.domain.vo.PaymentContextVo;
import com.ezu.order.domainService.OrderDomainService;
import com.ezu.order.repository.BillRepository;
import com.ezu.order.repository.DepositRecordRepository;
import com.ezu.order.repository.LeaseRepository;
import com.ezu.order.repository.LogisticsRepository;
import com.ezu.order.repository.OrderItemRepository;
import com.ezu.order.repository.OrderRepository;
import com.ezu.product.domain.entity.Product;
import com.ezu.product.domain.entity.ProductSku;
import com.ezu.product.domain.entity.RentalPlan;
import com.ezu.product.domain.enums.ProductStatus;
import com.ezu.product.domain.enums.SkuStatus;
import com.ezu.product.domain.req.DeviceCheckSaveReq;
import com.ezu.product.domainService.DeviceDomainService;
import com.ezu.product.repository.ProductRepository;
import com.ezu.product.repository.ProductSkuRepository;
import com.ezu.product.repository.RentalPlanRepository;
import com.ezu.product.util.RentalCalculator;
import com.ezu.user.domain.entity.UserAddress;
import com.ezu.user.domainService.UserAddressDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 租赁订单领域服务实现
 */
@Service
@RequiredArgsConstructor
public class OrderDomainServiceImpl implements OrderDomainService {

    /** 订单号前缀 */
    private static final String NO_PREFIX_ORDER = "R";

    /** 账单号前缀 */
    private static final String NO_PREFIX_BILL = "B";

    /** 押金流水号前缀 */
    private static final String NO_PREFIX_DEPOSIT = "D";

    /** 用户取消原因 */
    private static final String CANCEL_REASON_USER = "用户取消";

    /** 押金收取原因（支付成功时模拟冻结） */
    private static final String DEPOSIT_REASON_CHARGE = "下单押金冻结(模拟)";

    /** 押金退还原因（验收通过模拟退款） */
    private static final String DEPOSIT_REASON_REFUND = "归还验收通过退押金(模拟)";

    /** 押金渠道：模拟 */
    private static final int DEPOSIT_CHANNEL_MOCK = 1;

    /** 设备报告类型：归还验收 */
    private static final int DEVICE_CHECK_RETURN = 2;

    /** 一期运费恒0 */
    private static final BigDecimal FREIGHT = BigDecimal.ZERO;

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final LeaseRepository leaseRepository;
    private final DepositRecordRepository depositRecordRepository;
    private final BillRepository billRepository;
    private final LogisticsRepository logisticsRepository;
    private final ProductSkuRepository productSkuRepository;
    private final RentalPlanRepository rentalPlanRepository;
    private final ProductRepository productRepository;
    private final DeviceDomainService deviceDomainService;
    private final UserAddressDomainService userAddressDomainService;
    private final NoGenerator noGenerator;

    @Override
    public OrderTrialVo trial(Long skuId, Long rentalPlanId, Integer rentDays) {
        TrialContext ctx = validateAndCalc(skuId, rentalPlanId, rentDays);
        OrderTrialVo vo = new OrderTrialVo();
        vo.setUnitPrice(ctx.plan.getPrice());
        vo.setTotalRent(ctx.totalRent);
        vo.setDeposit(ctx.sku.getDeposit());
        vo.setFreight(FREIGHT);
        vo.setPayAmount(ctx.totalRent.add(FREIGHT));
        return vo;
    }

    @Override
    public OrderCreateVo create(Long userId, OrderCreateReq req) {
        // 1. 校验：SKU/方案/租期（与试算共用校验链）+ 地址存在且属本人
        TrialContext ctx = validateAndCalc(req.getSkuId(), req.getRentalPlanId(), req.getRentDays());
        UserAddress address = userAddressDomainService.getByIdAndUserId(req.getAddressId(), userId);
        if (address == null) {
            throw new BusinessException("地址【{}】不存在", req.getAddressId());
        }
        // 2. 锁设备（CAS 单遍5候选，失败抛业务异常→整体回滚，无残留锁定）
        Long deviceId = deviceDomainService.lockDevice(req.getSkuId());
        // 3. 建单（快照金额/计费单位/收货三件套；起租日期收货确认时回填）
        Order order = new Order();
        order.setOrderNo(noGenerator.next(NO_PREFIX_ORDER));
        order.setUserId(userId);
        order.setStatus(OrderStatus.WAIT_PAY);
        order.setTotalRent(ctx.totalRent);
        order.setDepositAmount(ctx.sku.getDeposit());
        order.setPayAmount(ctx.totalRent.add(FREIGHT));
        order.setFreight(FREIGHT);
        order.setLeaseUnit(ctx.plan.getUnit());
        order.setUnitPrice(ctx.plan.getPrice());
        order.setRentDays(req.getRentDays());
        order.setReceiver(address.getReceiver());
        order.setReceiverPhone(address.getPhone());
        order.setReceiverAddress(joinAddress(address));
        order.setRemark(req.getRemark());
        orderRepository.save(order);
        // 4. 明细快照一行（specImage 空则回落 SPU 主图）
        OrderItem item = new OrderItem();
        item.setOrderNo(order.getOrderNo());
        item.setSkuId(req.getSkuId());
        item.setDeviceId(deviceId);
        item.setProductName(ctx.product.getName());
        item.setSkuSpecName(ctx.sku.getSpecName());
        item.setSkuImage(StrUtil.blankToDefault(ctx.sku.getSpecImage(), ctx.product.getMainImage()));
        item.setPriceSnapshot(ctx.plan.getPrice());
        item.setDepositSnapshot(ctx.sku.getDeposit());
        orderItemRepository.save(item);
        // 5. 待支付租金账单一条
        Bill bill = new Bill();
        bill.setBillNo(noGenerator.next(NO_PREFIX_BILL));
        bill.setOrderNo(order.getOrderNo());
        bill.setUserId(userId);
        bill.setType(BillType.RENT);
        bill.setAmount(order.getPayAmount());
        bill.setStatus(BillStatus.UNPAID);
        billRepository.save(bill);
        OrderCreateVo vo = new OrderCreateVo();
        vo.setOrderNo(order.getOrderNo());
        vo.setPayAmount(order.getPayAmount());
        return vo;
    }

    @Override
    public void cancel(Long userId, String orderNo) {
        Order order = requireOwnOrder(userId, orderNo);
        checkCancelStatus(order.getStatus());
        // 条件更新 10/20→80：防并发窗口内状态被支付/发货流程推进
        boolean updated = orderRepository.lambdaUpdate()
                .eq(Order::getId, order.getId())
                .in(Order::getStatus, OrderStatus.WAIT_PAY, OrderStatus.WAIT_DELIVER)
                .set(Order::getStatus, OrderStatus.CANCELED)
                .set(Order::getCancelReason, CANCEL_REASON_USER)
                .update();
        if (!updated) {
            throw new BusinessException("订单【{}】状态已变更，请刷新后重试", orderNo);
        }
        // 释放下单时锁定的设备（2→1）：待支付/待发货两态下单即已锁设备，取消均需释放
        OrderItem item = orderItemRepository.getFirstByOrderNo(orderNo);
        if (item != null) {
            deviceDomainService.releaseDevice(item.getDeviceId());
        }
    }

    @Override
    public PageVo<OrderListVo> queryPage(Long userId, OrderQueryReq req) {
        return assembleListPage(orderRepository.queryPage(userId, req), req.getPageSize());
    }

    @Override
    public PageVo<OrderListVo> queryAdminPage(AdminOrderQueryReq req) {
        return assembleListPage(orderRepository.queryAdminPage(req), req.getPageSize());
    }

    @Override
    public OrderDetailVo detail(Long userId, String orderNo) {
        Order order = requireOwnOrder(userId, orderNo);
        return assembleDetail(order);
    }

    @Override
    public OrderDetailVo adminDetail(String orderNo) {
        Order order = orderRepository.getByOrderNo(orderNo);
        if (order == null) {
            throw new BusinessException("订单【{}】不存在", orderNo);
        }
        return assembleDetail(order);
    }

    // ==================== 履约流转（T8） ====================

    @Override
    public PaymentContextVo getPaymentContext(String orderNo) {
        Order order = requireOrder(orderNo);
        PaymentContextVo ctx = new PaymentContextVo();
        ctx.setOrderNo(order.getOrderNo());
        ctx.setUserId(order.getUserId());
        ctx.setStatus(order.getStatus());
        ctx.setPayAmount(order.getPayAmount());
        Bill unpaid = billRepository.lambdaQuery()
                .eq(Bill::getOrderNo, orderNo)
                .eq(Bill::getStatus, BillStatus.UNPAID)
                .orderByDesc(Bill::getId)
                .last("LIMIT 1")
                .one();
        ctx.setUnpaidBillNo(unpaid == null ? null : unpaid.getBillNo());
        return ctx;
    }

    @Override
    public void onPaymentSuccess(String orderNo) {
        Order order = requireOrder(orderNo);
        // 幂等：回调可能重复到达，已推进到待发货直接视为成功
        if (Objects.equals(order.getStatus(), OrderStatus.WAIT_DELIVER)) {
            return;
        }
        if (!Objects.equals(order.getStatus(), OrderStatus.WAIT_PAY)) {
            // 已取消等异常态：抛异常连同支付记录一起回滚
            throw new BusinessException("订单状态【{}】不允许该操作", order.getStatus());
        }
        orderRepository.lambdaUpdate()
                .eq(Order::getId, order.getId())
                .eq(Order::getStatus, OrderStatus.WAIT_PAY)
                .set(Order::getStatus, OrderStatus.WAIT_DELIVER)
                .update();
        // 待支付账单 → 已支付（条件更新，重复回调不重复覆盖支付时间）
        billRepository.lambdaUpdate()
                .eq(Bill::getOrderNo, orderNo)
                .eq(Bill::getStatus, BillStatus.UNPAID)
                .set(Bill::getStatus, BillStatus.PAID)
                .set(Bill::getPayTime, LocalDateTime.now())
                .update();
        // 押金收取（模拟冻结）流水
        saveDepositRecord(order, DepositType.CHARGE, order.getDepositAmount(), DEPOSIT_REASON_CHARGE);
    }

    @Override
    public void ship(String orderNo, LogisticsInfoReq req) {
        Order order = requireOrder(orderNo);
        checkStatus(order, OrderStatus.WAIT_DELIVER, OrderStatus.DELIVERED);
        boolean updated = orderRepository.lambdaUpdate()
                .eq(Order::getId, order.getId())
                .eq(Order::getStatus, OrderStatus.WAIT_DELIVER)
                .set(Order::getStatus, OrderStatus.DELIVERED)
                .update();
        if (!updated) {
            throw new BusinessException("订单【{}】状态已变更，请刷新后重试", orderNo);
        }
        // 设备：锁定2 → 在租3
        OrderItem item = requireOrderItem(orderNo);
        deviceDomainService.rentDevice(item.getDeviceId());
        // 寄出物流
        saveLogistics(orderNo, LogisticsType.OUTBOUND, req);
    }

    @Override
    public void confirmReceive(Long userId, String orderNo) {
        Order order = requireOwnOrder(userId, orderNo);
        checkStatus(order, OrderStatus.DELIVERED, OrderStatus.RENTING);
        boolean updated = orderRepository.lambdaUpdate()
                .eq(Order::getId, order.getId())
                .eq(Order::getStatus, OrderStatus.DELIVERED)
                .set(Order::getStatus, OrderStatus.RENTING)
                .update();
        if (!updated) {
            throw new BusinessException("订单【{}】状态已变更，请刷新后重试", orderNo);
        }
        // 初始租约：今天起租，租 rentDays 天（首尾均计）
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(order.getRentDays() - 1L);
        Lease lease = new Lease();
        lease.setOrderNo(orderNo);
        lease.setType(LeaseType.INITIAL);
        lease.setStartDate(start);
        lease.setEndDate(end);
        lease.setRent(order.getTotalRent());
        leaseRepository.save(lease);
        // 起止回写主表（B端逾期过滤依赖 rent_end_date）
        orderRepository.lambdaUpdate()
                .eq(Order::getId, order.getId())
                .set(Order::getRentStartDate, start)
                .set(Order::getRentEndDate, end)
                .update();
    }

    @Override
    public void returnApply(Long userId, String orderNo) {
        Order order = requireOwnOrder(userId, orderNo);
        checkStatus(order, OrderStatus.RENTING, OrderStatus.RETURNING);
        boolean updated = orderRepository.lambdaUpdate()
                .eq(Order::getId, order.getId())
                .eq(Order::getStatus, OrderStatus.RENTING)
                .set(Order::getStatus, OrderStatus.RETURNING)
                .update();
        if (!updated) {
            throw new BusinessException("订单【{}】状态已变更，请刷新后重试", orderNo);
        }
        // 设备：在租3 → 归还验收中6
        OrderItem item = requireOrderItem(orderNo);
        deviceDomainService.returnDevice(item.getDeviceId());
    }

    @Override
    public void returnSign(String orderNo, LogisticsInfoReq req) {
        requireOrder(orderNo);
        // 仅登记寄回物流，订单状态不变
        saveLogistics(orderNo, LogisticsType.RETURN, req);
    }

    @Override
    public boolean acceptCheck(String orderNo, AcceptCheckReq req) {
        Order order = requireOrder(orderNo);
        // 幂等：验收中60 允许重复登记验收报告（如复检）
        if (Objects.equals(order.getStatus(), OrderStatus.RETURNING)) {
            boolean updated = orderRepository.lambdaUpdate()
                    .eq(Order::getId, order.getId())
                    .eq(Order::getStatus, OrderStatus.RETURNING)
                    .set(Order::getStatus, OrderStatus.CHECKING)
                    .update();
            if (!updated) {
                throw new BusinessException("订单【{}】状态已变更，请刷新后重试", orderNo);
            }
        } else if (!Objects.equals(order.getStatus(), OrderStatus.CHECKING)) {
            throw new BusinessException("订单状态【{}】不允许该操作", order.getStatus());
        }
        // 归还验收报告（type=2，回写设备成色），跨模块调 ezu-product
        OrderItem item = requireOrderItem(orderNo);
        DeviceCheckSaveReq checkReq = new DeviceCheckSaveReq();
        checkReq.setDeviceId(item.getDeviceId());
        checkReq.setType(DEVICE_CHECK_RETURN);
        checkReq.setOrderNo(orderNo);
        checkReq.setExteriorResult(req.getExteriorResult());
        checkReq.setFunctionResult(req.getFunctionResult());
        checkReq.setConditionLevel(req.getConditionLevel());
        checkReq.setAttachments(req.getAttachments());
        checkReq.setChecker(req.getChecker());
        checkReq.setRemark(req.getRemark());
        deviceDomainService.saveCheckReport(checkReq);
        if (Boolean.TRUE.equals(req.getPass())) {
            acceptConfirm(orderNo);
            return true;
        }
        // 不通过停在验收中（定损二期）
        return false;
    }

    @Override
    public void acceptConfirm(String orderNo) {
        Order order = requireOrder(orderNo);
        checkStatus(order, OrderStatus.CHECKING, OrderStatus.FINISHED);
        boolean updated = orderRepository.lambdaUpdate()
                .eq(Order::getId, order.getId())
                .eq(Order::getStatus, OrderStatus.CHECKING)
                .set(Order::getStatus, OrderStatus.FINISHED)
                .update();
        if (!updated) {
            throw new BusinessException("订单【{}】状态已变更，请刷新后重试", orderNo);
        }
        // 设备：归还验收中6 → 在库1（可再租）
        OrderItem item = requireOrderItem(orderNo);
        deviceDomainService.backToStock(item.getDeviceId());
        // 押金退还流水（一期只记流水，不动支付记录）
        saveDepositRecord(order, DepositType.REFUND, order.getDepositAmount(), DEPOSIT_REASON_REFUND);
    }

    /**
     * 试算/下单共用校验链：方案存在·启用·属于该SKU → SKU存在·启用 → SPU上架 → 租期下限；通过后计算总租金
     */
    private TrialContext validateAndCalc(Long skuId, Long rentalPlanId, Integer rentDays) {
        RentalPlan plan = rentalPlanRepository.getById(rentalPlanId);
        if (plan == null) {
            throw new BusinessException("租金方案【{}】不存在", rentalPlanId);
        }
        if (!Objects.equals(plan.getStatus(), SkuStatus.ENABLED)) {
            throw new BusinessException("租金方案【{}】已停用", rentalPlanId);
        }
        if (!Objects.equals(plan.getSkuId(), skuId)) {
            throw new BusinessException("租金方案【{}】不属于该商品规格", rentalPlanId);
        }
        ProductSku sku = productSkuRepository.getById(skuId);
        if (sku == null) {
            throw new BusinessException("商品规格【{}】不存在", skuId);
        }
        if (!Objects.equals(sku.getStatus(), SkuStatus.ENABLED)) {
            throw new BusinessException("商品规格【{}】已停用", skuId);
        }
        Product product = productRepository.getById(sku.getProductId());
        if (product == null || !Objects.equals(product.getStatus(), ProductStatus.ON_SHELF)) {
            throw new BusinessException("商品【{}】不存在或已下架", sku.getProductId());
        }
        if (plan.getMinDays() != null && rentDays < plan.getMinDays()) {
            throw new BusinessException("该方案最少租【{}】天", plan.getMinDays());
        }
        TrialContext ctx = new TrialContext();
        ctx.plan = plan;
        ctx.sku = sku;
        ctx.product = product;
        ctx.totalRent = RentalCalculator.calcTotalRent(plan.getUnit(), plan.getPrice(), rentDays);
        return ctx;
    }

    /**
     * 列表组装：批量按单号查明细快照后配对（一期一单一明细，取首条；避免逐单查库）
     */
    private PageVo<OrderListVo> assembleListPage(IPage<Order> page, Integer pageSize) {
        List<Order> records = page.getRecords();
        if (records.isEmpty()) {
            return new PageVo<>(page.getTotal(), new ArrayList<>());
        }
        List<String> orderNos = records.stream().map(Order::getOrderNo).collect(Collectors.toList());
        Map<String, List<OrderItem>> itemMap = orderItemRepository.listByOrderNos(orderNos).stream()
                .collect(Collectors.groupingBy(OrderItem::getOrderNo));
        List<OrderListVo> list = records.stream().map(order -> {
            OrderListVo vo = new OrderListVo();
            BeanUtil.copyProperties(order, vo);
            List<OrderItem> items = itemMap.get(order.getOrderNo());
            if (items != null && !items.isEmpty()) {
                OrderListVo.ItemVo itemVo = new OrderListVo.ItemVo();
                BeanUtil.copyProperties(items.get(0), itemVo);
                vo.setItem(itemVo);
            }
            return vo;
        }).collect(Collectors.toList());
        // pageSize=-1 全量时 MP 跳过 count 查询，total 以实际条数回填
        long total = pageSize == -1 ? list.size() : page.getTotal();
        return new PageVo<>(total, list);
    }

    /**
     * 详情组装：主表 + 明细/租约/账单/押金/物流（后四者按最新一条，租赁前期为 null）
     */
    private OrderDetailVo assembleDetail(Order order) {
        OrderDetailVo vo = new OrderDetailVo();
        BeanUtil.copyProperties(order, vo);
        String orderNo = order.getOrderNo();
        OrderItem item = orderItemRepository.getFirstByOrderNo(orderNo);
        if (item != null) {
            OrderListVo.ItemVo itemVo = new OrderListVo.ItemVo();
            BeanUtil.copyProperties(item, itemVo);
            vo.setItem(itemVo);
        }
        Lease lease = leaseRepository.getLatestByOrderNo(orderNo);
        if (lease != null) {
            OrderDetailVo.LeaseVo leaseVo = new OrderDetailVo.LeaseVo();
            BeanUtil.copyProperties(lease, leaseVo);
            vo.setLease(leaseVo);
        }
        Bill bill = billRepository.getLatestByOrderNo(orderNo);
        if (bill != null) {
            OrderDetailVo.BillVo billVo = new OrderDetailVo.BillVo();
            BeanUtil.copyProperties(bill, billVo);
            vo.setBill(billVo);
        }
        DepositRecord deposit = depositRecordRepository.getLatestByOrderNo(orderNo);
        if (deposit != null) {
            OrderDetailVo.DepositVo depositVo = new OrderDetailVo.DepositVo();
            BeanUtil.copyProperties(deposit, depositVo);
            vo.setDeposit(depositVo);
        }
        Logistics logistics = logisticsRepository.getLatestByOrderNo(orderNo);
        if (logistics != null) {
            OrderDetailVo.LogisticsVo logisticsVo = new OrderDetailVo.LogisticsVo();
            BeanUtil.copyProperties(logistics, logisticsVo);
            // 前端契约字段名 logisticsNo，落库列 tracking_no
            logisticsVo.setLogisticsNo(logistics.getTrackingNo());
            vo.setLogistics(logisticsVo);
        }
        return vo;
    }

    /**
     * 按单号+用户查订单，不存在或不属本人统一抛「订单不存在」（不泄露他人订单存在性）
     */
    private Order requireOwnOrder(Long userId, String orderNo) {
        Order order = orderRepository.getByOrderNo(orderNo);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单【{}】不存在", orderNo);
        }
        return order;
    }

    /**
     * 仅待支付/待发货可取消
     */
    private void checkCancelStatus(Integer status) {
        if (Objects.equals(status, OrderStatus.WAIT_PAY) || Objects.equals(status, OrderStatus.WAIT_DELIVER)) {
            return;
        }
        throw new BusinessException("订单状态【{}】不允许取消", status);
    }

    /**
     * 省市区+详细拼接为一条收货地址快照
     */
    private String joinAddress(UserAddress address) {
        return StrUtil.nullToEmpty(address.getProvince()) + StrUtil.nullToEmpty(address.getCity())
                + StrUtil.nullToEmpty(address.getDistrict()) + StrUtil.nullToEmpty(address.getDetailAddress());
    }

    /**
     * 按订单号查订单，不存在抛业务异常
     */
    private Order requireOrder(String orderNo) {
        Order order = orderRepository.getByOrderNo(orderNo);
        if (order == null) {
            throw new BusinessException("订单【{}】不存在", orderNo);
        }
        return order;
    }

    /**
     * 订单首条明细（一期一单一明细），缺失视为脏数据抛异常
     */
    private OrderItem requireOrderItem(String orderNo) {
        OrderItem item = orderItemRepository.getFirstByOrderNo(orderNo);
        if (item == null) {
            throw new BusinessException("订单【{}】缺少设备明细", orderNo);
        }
        return item;
    }

    /**
     * 状态前置校验：非期望状态抛「订单状态【{}】不允许该操作」
     */
    private void checkStatus(Order order, int expected, int next) {
        if (!Objects.equals(order.getStatus(), expected)) {
            throw new BusinessException("订单状态【{}】不允许该操作", order.getStatus());
        }
    }

    /**
     * 记一段物流（寄出/寄回），状态置已发出
     */
    private void saveLogistics(String orderNo, int type, LogisticsInfoReq req) {
        Logistics logistics = new Logistics();
        logistics.setOrderNo(orderNo);
        logistics.setType(type);
        logistics.setCompany(req.getCompany());
        logistics.setTrackingNo(req.getLogisticsNo());
        logistics.setStatus(LogisticsStatus.SHIPPED);
        logisticsRepository.save(logistics);
    }

    /**
     * 记押金流水（收取/退还）
     */
    private void saveDepositRecord(Order order, int type, BigDecimal amount, String reason) {
        DepositRecord record = new DepositRecord();
        record.setRecordNo(noGenerator.next(NO_PREFIX_DEPOSIT));
        record.setOrderNo(order.getOrderNo());
        record.setUserId(order.getUserId());
        record.setAmount(amount);
        record.setType(type);
        record.setChannel(DEPOSIT_CHANNEL_MOCK);
        record.setReason(reason);
        depositRecordRepository.save(record);
    }

    /**
     * 试算/下单校验通过后的上下文（方案+SKU+SPU+总租金）
     */
    private static class TrialContext {

        RentalPlan plan;

        ProductSku sku;

        Product product;

        BigDecimal totalRent;
    }
}
