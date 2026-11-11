package com.ezu.order.domain.enums;

/**
 * 账单类型
 */
public interface BillType {

    /** 租金 */
    int RENT = 1;

    /** 续租租金（预留） */
    int RENEW_RENT = 2;

    /** 买断差额（预留） */
    int BUYOUT_DIFF = 3;

    /** 维修费（预留） */
    int REPAIR_FEE = 4;
}
