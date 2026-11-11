package com.ezu.product.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * C端商品列表项（仅上架SPU）
 */
@Data
public class AppProductListVo {

    private Long id;

    private String productNo;

    private String name;

    private String subtitle;

    private String mainImage;

    private String brand;

    /** 品类名称（回显） */
    private String categoryName;

    /** 最低租金起（启用SKU的启用方案 min(price)，元；无启用方案为 null） */
    private BigDecimal minRentPrice;

    /** 最低租金方案的计费单位（1=日，2=周，3=月，4=季） */
    private Integer minRentUnit;

    /** 在库可租设备数合计（启用SKU的在库设备） */
    private Integer stockCount;
}
