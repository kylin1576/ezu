package com.ezu.order.domain.enums;

/**
 * 账单状态
 */
public interface BillStatus {

    /** 已支付 */
    int PAID = 1;

    /** 待支付 */
    int UNPAID = 2;

    /** 已退款 */
    int REFUNDED = 3;
}
