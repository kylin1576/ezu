package com.ezu.product.domain.enums;

/**
 * SKU状态（同时用作 t_rental_plan.status 的取值：1=启用，2=停用）
 */
public interface SkuStatus {

    /** 启用 */
    int ENABLED = 1;

    /** 停用 */
    int DISABLED = 2;
}
