package com.ezu.payment.domain.enums;

/**
 * 支付业务类型
 */
public interface BizType {

    /** 租金 */
    int RENT = 1;

    /** 押金冻结（预留） */
    int DEPOSIT = 2;
}
