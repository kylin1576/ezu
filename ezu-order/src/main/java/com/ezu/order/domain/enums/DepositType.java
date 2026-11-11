package com.ezu.order.domain.enums;

/**
 * 押金操作类型
 */
public interface DepositType {

    /** 收取/冻结 */
    int CHARGE = 1;

    /** 退还 */
    int REFUND = 2;

    /** 扣除（定损预留） */
    int DEDUCT = 3;
}
