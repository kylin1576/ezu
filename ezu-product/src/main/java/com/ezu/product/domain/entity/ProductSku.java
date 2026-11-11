package com.ezu.product.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ezu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 商品SKU（规格）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_product_sku")
public class ProductSku extends BaseEntity {

    /** SKU编号（S+yyyyMMdd+4位） */
    private String skuNo;

    /** 所属SPU ID */
    private Long productId;

    /** 规格名称（如：单机身 / 24-105套机 / 16G+512G） */
    private String specName;

    /** 规格图URL */
    private String specImage;

    /** 押金（元） */
    private BigDecimal deposit;

    /** 买断价（元）（预留：以租代购） */
    private BigDecimal buyoutPrice;

    /**
     * 状态（1=启用，2=停用）
     * <p>
     * {@link com.ezu.product.domain.enums.SkuStatus}
     */
    private Integer status;
}
