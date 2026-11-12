package com.ezu.order.application;

import com.ezu.common.page.PageVo;
import com.ezu.order.domain.req.AcceptCheckReq;
import com.ezu.order.domain.req.AdminOrderQueryReq;
import com.ezu.order.domain.req.LogisticsInfoReq;
import com.ezu.order.domain.req.OrderCreateReq;
import com.ezu.order.domain.req.OrderQueryReq;
import com.ezu.order.domain.req.OrderTrialReq;
import com.ezu.order.domain.vo.OrderCreateVo;
import com.ezu.order.domain.vo.OrderDetailVo;
import com.ezu.order.domain.vo.OrderListVo;
import com.ezu.order.domain.vo.OrderTrialVo;
import com.ezu.order.domainService.OrderDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 租赁订单应用服务
 */
@Service
@RequiredArgsConstructor
public class OrderApplicationService {

    private final OrderDomainService orderDomainService;

    /**
     * 下单试算
     */
    public OrderTrialVo trial(OrderTrialReq req) {
        return orderDomainService.trial(req.getSkuId(), req.getRentalPlanId(), req.getRentDays());
    }

    /**
     * 创建订单（校验→CAS锁设备→建单+明细+待支付账单，同一事务）
     */
    @Transactional(rollbackFor = Exception.class)
    public OrderCreateVo create(Long userId, OrderCreateReq req) {
        return orderDomainService.create(userId, req);
    }

    /**
     * 取消订单（待支付/待发货→已取消，同事务内释放锁定设备）
     */
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long userId, String orderNo) {
        orderDomainService.cancel(userId, orderNo);
    }

    /**
     * C端订单列表（只查本人）
     */
    public PageVo<OrderListVo> list(Long userId, OrderQueryReq req) {
        return orderDomainService.queryPage(userId, req);
    }

    /**
     * C端订单详情（本人校验）
     */
    public OrderDetailVo detail(Long userId, String orderNo) {
        return orderDomainService.detail(userId, orderNo);
    }

    /**
     * B端订单列表（全量视图）
     */
    public PageVo<OrderListVo> adminList(AdminOrderQueryReq req) {
        return orderDomainService.queryAdminPage(req);
    }

    /**
     * B端订单详情（全量视图）
     */
    public OrderDetailVo adminDetail(String orderNo) {
        return orderDomainService.adminDetail(orderNo);
    }

    // ==================== 履约流转（T8） ====================

    /**
     * B端发货（20→30，设备2→3，登记寄出物流）
     */
    @Transactional(rollbackFor = Exception.class)
    public void ship(String orderNo, LogisticsInfoReq req) {
        orderDomainService.ship(orderNo, req);
    }

    /**
     * C端确认收货（30→40，生成初始租约并回写主表租期）
     */
    @Transactional(rollbackFor = Exception.class)
    public void confirmReceive(Long userId, String orderNo) {
        orderDomainService.confirmReceive(userId, orderNo);
    }

    /**
     * C端申请归还（40→50，设备3→6）
     */
    @Transactional(rollbackFor = Exception.class)
    public void returnApply(Long userId, String orderNo) {
        orderDomainService.returnApply(userId, orderNo);
    }

    /**
     * B端登记归还寄回物流（订单状态不变）
     */
    public void returnSign(String orderNo, LogisticsInfoReq req) {
        orderDomainService.returnSign(orderNo, req);
    }

    /**
     * B端归还验收（50→60，保存验收报告；pass=true 直接完成订单）
     *
     * @return 是否已完成（true=订单已达已完成70）
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean acceptCheck(String orderNo, AcceptCheckReq req) {
        return orderDomainService.acceptCheck(orderNo, req);
    }

    /**
     * B端验收完成确认（60→70，设备6→1，押金退还流水）
     */
    @Transactional(rollbackFor = Exception.class)
    public void acceptConfirm(String orderNo) {
        orderDomainService.acceptConfirm(orderNo);
    }
}
