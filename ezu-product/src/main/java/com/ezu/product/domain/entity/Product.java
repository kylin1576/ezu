package com.ezu.product.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ezu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品SPU
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_product")
public class Product extends BaseEntity {

    /** 商品编号（G+yyyyMMdd+4位） */
    private String productNo;

    /** 品类ID（取二级品类） */
    private Long categoryId;

    /** 品牌（如 佳能/苹果/惠普） */
    private String brand;

    /** 商品名称（SPU，如 佳能 EOS R6 Mark II） */
    private String name;

    /** 副标题/卖点 */
    private String subtitle;

    /** 主图URL */
    private String mainImage;

    /** 图集（JSON数组：url字符串列表） */
    private String images;

    /** 图文详情（富文本HTML） */
    private String description;

    /**
     * 状态（1=上架，2=下架；新建默认下架）
     * <p>
     * {@link com.ezu.product.domain.enums.ProductStatus}
     */
    private Integer status;

    /** 排序（越小越靠前） */
    private Integer sort;
}
