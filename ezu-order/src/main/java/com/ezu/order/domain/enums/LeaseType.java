package com.ezu.order.domain.enums;

/**
 * 租约类型
 */
public interface LeaseType {

    /** 初始租期 */
    int INITIAL = 1;

    /** 续租（预留） */
    int RENEW = 2;
}
