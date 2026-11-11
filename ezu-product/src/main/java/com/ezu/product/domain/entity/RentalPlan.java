package com.ezu.product.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ezu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * SKU租金方案（一SKU多方案）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_rental_plan")
public class RentalPlan extends BaseEntity {

    /** SKU ID */
    private Long skuId;

    /**
     * 计费单位（1=日，2=周，3=月，4=季）
     * <p>
     * {@link com.ezu.product.domain.enums.RentalUnit}
     */
    private Integer unit;

    /** 该周期租金（元/周期） */
    private BigDecimal price;

    /** 起租天数（该方案最少租期，如日租3天起租） */
    private Integer minDays;

    /** 状态（1=启用，2=停用；取值同 {@link com.ezu.product.domain.enums.SkuStatus}） */
    private Integer status;
}
