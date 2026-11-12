package com.ezu.order.domainService;

import com.ezu.common.page.PageVo;
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

/**
 * 租赁订单领域服务（下单闭环 + 履约流转）
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

    /**
     * 支付上下文（ezu-payment 发起支付时读取：归属/状态/应付金额/待支付账单号）
     */
    PaymentContextVo getPaymentContext(String orderNo);

    // ==================== 履约流转（T8） ====================

    /**
     * 支付成功推进（ezu-payment 回调调用，事务由回调方管理）：
     * 待支付10→待发货20（条件更新防与取消竞态，失败抛异常回滚回调；已20幂等返回）；
     * 账单按 billNo 精确置已支付+支付时间；插押金收取（冻结）流水
     */
    void onPaymentSuccess(String orderNo, String billNo);

    /**
     * B端发货：待发货20→已发货30（条件更新防并发），设备锁定2→在租3，登记寄出物流
     */
    void ship(String orderNo, LogisticsInfoReq req);

    /**
     * C端确认收货：已发货30→租赁中40；生成初始租约（今天起租 rentDays 天）；
     * 租期起止回写主表（逾期过滤依赖 rent_end_date）
     */
    void confirmReceive(Long userId, String orderNo);

    /**
     * C端申请归还：租赁中40→归还中50，设备在租3→归还验收中6
     */
    void returnApply(Long userId, String orderNo);

    /**
     * B端登记归还寄回物流（订单状态不变，仅记 t_logistics type=2）
     */
    void returnSign(String orderNo, LogisticsInfoReq req);

    /**
     * B端归还验收：归还中50→验收中60（幂等：已60可重复登记报告）；
     * 调 ezu-product 保存 type=2 验收报告并回写成色；
     * pass=true 直接推进已完成，pass=false 停在验收中（定损二期）
     *
     * @return 是否已完成（true=订单已达已完成70）
     */
    boolean acceptCheck(String orderNo, AcceptCheckReq req);

    /**
     * B端验收完成确认：验收中60→已完成70，设备归还验收中6→在库1，插押金退还流水
     */
    void acceptConfirm(String orderNo);
}
