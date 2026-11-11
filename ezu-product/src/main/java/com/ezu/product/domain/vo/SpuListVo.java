package com.ezu.product.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品SPU列表项
 */
@Data
public class SpuListVo {

    private Long id;

    private String productNo;

    private Long categoryId;

    /** 品类名称（回显） */
    private String categoryName;

    private String brand;

    private String name;

    private String subtitle;

    private String mainImage;

    /** 状态（1=上架，2=下架） */
    private Integer status;

    /** SKU数 */
    private Integer skuCount;

    /** 最低租金起（所有启用方案 min(price)，元；无启用方案为 null） */
    private BigDecimal minRentPrice;

    private LocalDateTime createTime;
}
