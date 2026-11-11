package com.ezu.order.domain.enums;

/**
 * 物流状态（一期由运营手动维护）
 */
public interface LogisticsStatus {

    /** 已发出 */
    int SHIPPED = 1;

    /** 已签收 */
    int SIGNED = 2;
}
