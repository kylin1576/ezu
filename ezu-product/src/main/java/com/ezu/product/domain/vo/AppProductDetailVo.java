package com.ezu.product.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * C端商品详情（上架SPU主表 + 启用SKU（各带启用方案）+ 参数）
 */
@Data
public class AppProductDetailVo {

    private Long id;

    private String productNo;

    private Long categoryId;

    /** 品类名称（回显） */
    private String categoryName;

    private String brand;

    private String name;

    private String subtitle;

    private String mainImage;

    /** 图集（url列表，由 JSON 还原） */
    private List<String> images = new ArrayList<>();

    /** 图文详情（富文本HTML） */
    private String description;

    /** 商品参数列表 */
    private List<ParamVo> params = new ArrayList<>();

    /** SKU列表（仅启用SKU） */
    private List<SkuVo> skus = new ArrayList<>();

    /** 在库可租设备数合计（各SKU stockCount 之和） */
    private Integer stockCount = 0;

    /**
     * 商品参数项
     */
    @Data
    public static class ParamVo {

        private Long id;

        /** 参数名 */
        private String name;

        /** 参数值 */
        private String value;
    }

    /**
     * SKU项
     */
    @Data
    public static class SkuVo {

        private Long id;

        private String skuNo;

        /** 规格名称 */
        private String specName;

        private String specImage;

        /** 押金（元） */
        private BigDecimal deposit;

        /** 买断价（元） */
        private BigDecimal buyoutPrice;

        /** 该SKU在库可租设备数 */
        private Integer stockCount = 0;

        /** 租金方案列表（仅启用方案） */
        private List<PlanVo> rentalPlans = new ArrayList<>();
    }

    /**
     * 租金方案项
     */
    @Data
    public static class PlanVo {

        private Long id;

        /** 计费单位（1=日，2=周，3=月，4=季） */
        private Integer unit;

        /** 该周期租金（元/周期） */
        private BigDecimal price;

        /** 起租天数 */
        private Integer minDays;
    }
}
