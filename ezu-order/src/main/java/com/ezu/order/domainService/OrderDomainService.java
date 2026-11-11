package com.ezu.order.domainService;

import com.ezu.common.page.PageVo;
import com.ezu.order.domain.req.AdminOrderQueryReq;
import com.ezu.order.domain.req.OrderCreateReq;
import com.ezu.order.domain.req.OrderQueryReq;
import com.ezu.order.domain.vo.OrderCreateVo;
import com.ezu.order.domain.vo.OrderDetailVo;
import com.ezu.order.domain.vo.OrderListVo;
import com.ezu.order.domain.vo.OrderTrialVo;

/**
 * 租赁订单领域服务（下单闭环：试算/创建(CAS锁设备)/取消/列表/详情）
 */
public interface OrderDomainService {

    /**
     * 下单试算：校验方案启用·属于该SKU·SKU启用·SPU上架·租期下限，计算金额（与下单共用校验链）
     */
    OrderTrialVo trial(Long skuId, Long rentalPlanId, Integer rentDays);

    /**
     * 创建订单：校验（共用试算链+地址归属）→ CAS锁设备 → 建单+明细快照+待支付账单；
     * 事务由 OrderApplicationService 管理
     */
    OrderCreateVo create(Long userId, OrderCreateReq req);

    /**
     * 取消订单：待支付/待发货 → 已取消（条件更新防并发），释放下单时锁定的设备（2→1）
     */
    void cancel(Long userId, String orderNo);

    /**
     * C端订单分页（只查本人，状态可选）
     */
    PageVo<OrderListVo> queryPage(Long userId, OrderQueryReq req);

    /**
     * B端订单分页（全量视图：单号精确/用户/状态/创建日期范围）
     */
    PageVo<OrderListVo> queryAdminPage(AdminOrderQueryReq req);

    /**
     * C端订单详情（本人校验：不存在或不属本人统一抛「订单不存在」）
     */
    OrderDetailVo detail(Long userId, String orderNo);

    /**
     * B端订单详情（全量视图，无本人校验）
     */
    OrderDetailVo adminDetail(String orderNo);
}
