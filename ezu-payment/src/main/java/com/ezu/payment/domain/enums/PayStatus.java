package com.ezu.payment.domain.enums;

/**
 * 支付单状态
 */
public interface PayStatus {

    /** 待支付 */
    int WAIT_PAY = 1;

    /** 成功 */
    int SUCCESS = 2;

    /** 失败 */
    int FAIL = 3;

    /** 已退款 */
    int REFUNDED = 4;
}
