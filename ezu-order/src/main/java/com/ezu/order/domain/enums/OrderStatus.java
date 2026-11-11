package com.ezu.order.domain.enums;

/**
 * 订单状态机
 * <p>
 * 主流程：待支付(10) → 待发货(20) → 已发货(30) → 租赁中(40) → 归还中(50) → 验收中(60) → 已完成(70)
 * <p>
 * 取消：待支付(10)/待发货(20) → 已取消(80)；验收中(60) 验收不通过走定损流程（二期预留）
 */
public interface OrderStatus {

    /** 待支付 */
    int WAIT_PAY = 10;

    /** 待发货 */
    int WAIT_DELIVER = 20;

    /** 已发货 */
    int DELIVERED = 30;

    /** 租赁中 */
    int RENTING = 40;

    /** 归还中 */
    int RETURNING = 50;

    /** 验收中 */
    int CHECKING = 60;

    /** 已完成 */
    int FINISHED = 70;

    /** 已取消 */
    int CANCELED = 80;
}
