package com.ezu.product.domain.req;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品SPU整体保存请求（id为空=新增，非空=编辑；SKU/租金方案/参数全量提交、整体覆盖）
 */
@Data
public class SpuSaveReq {

    /** 商品ID（编辑必传） */
    private Long id;

    /** 品类ID（取二级品类） */
    @NotNull(message = "品类不能为空")
    private Long categoryId;

    /** 品牌（如 佳能/苹果/惠普） */
    private String brand;

    /** 商品名称 */
    @NotBlank(message = "商品名称不能为空")
    private String name;

    /** 副标题/卖点 */
    private String subtitle;

    /** 主图URL */
    private String mainImage;

    /** 图集（url列表，后端转JSON存储） */
    private List<String> images;

    /** 图文详情（富文本HTML） */
    private String description;

    /** SKU列表（全量提交，整体覆盖；id 仅编辑回显用，保存时后端重新生成） */
    @Valid
    private List<SkuItem> skus;

    /** 商品参数列表（全量提交，整体覆盖） */
    @Valid
    private List<ParamItem> params;

    /**
     * SKU项
     */
    @Data
    public static class SkuItem {

        /** SKU ID（编辑回显带回，整体覆盖时不使用） */
        private Long id;

        /** 规格名称（如：单机身 / 24-105套机） */
        @NotBlank(message = "规格名称不能为空")
        private String specName;

        /** 规格图URL */
        private String specImage;

        /** 押金（元） */
        @NotNull(message = "押金不能为空")
        private BigDecimal deposit;

        /** 买断价（元）（预留：以租代购） */
        private BigDecimal buyoutPrice;

        /** 租金方案列表 */
        @Valid
        private List<PlanItem> rentalPlans;
    }

    /**
     * 租金方案项
     */
    @Data
    public static class PlanItem {

        /** 计费单位（1=日，2=周，3=月，4=季） */
        @NotNull(message = "计费单位不能为空")
        private Integer unit;

        /** 该周期租金（元/周期） */
        @NotNull(message = "租金不能为空")
        private BigDecimal price;

        /** 起租天数（该方案最少租期） */
        @NotNull(message = "起租天数不能为空")
        private Integer minDays;
    }

    /**
     * 商品参数项
     */
    @Data
    public static class ParamItem {

        /** 参数名（如：传感器尺寸） */
        @NotBlank(message = "参数名不能为空")
        private String name;

        /** 参数值（如：全画幅 2420万像素） */
        @NotBlank(message = "参数值不能为空")
        private String value;

        /** 排序（越小越靠前） */
        private Integer sort = 0;
    }
}
