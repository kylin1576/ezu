package com.ezu.product.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 商品SPU详情（主表 + SKU（各带租金方案）+ 参数）
 */
@Data
public class SpuDetailVo {

    private Long id;

    private String productNo;

    private Long categoryId;

    private String brand;

    private String name;

    private String subtitle;

    private String mainImage;

    /** 图集（url列表，由 JSON 还原） */
    private List<String> images = new ArrayList<>();

    private String description;

    /** 状态（1=上架，2=下架） */
    private Integer status;

    private LocalDateTime createTime;

    /** SKU列表 */
    private List<SkuVo> skus = new ArrayList<>();

    /** 商品参数列表 */
    private List<ParamVo> params = new ArrayList<>();

    /**
     * SKU项
     */
    @Data
    public static class SkuVo {

        private Long id;

        private String skuNo;

        private String specName;

        private String specImage;

        private BigDecimal deposit;

        private BigDecimal buyoutPrice;

        /** 状态（1=启用，2=停用） */
        private Integer status;

        /** 租金方案列表 */
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

        /** 状态（1=启用，2=停用） */
        private Integer status;
    }

    /**
     * 商品参数项
     */
    @Data
    public static class ParamVo {

        private Long id;

        private String name;

        private String value;

        private Integer sort;
    }
}
