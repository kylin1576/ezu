package com.ezu.order.application;

import com.ezu.common.page.PageVo;
import com.ezu.order.domain.req.AdminOrderQueryReq;
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
}
