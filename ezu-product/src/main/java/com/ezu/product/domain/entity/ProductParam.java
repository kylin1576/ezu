package com.ezu.product.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ezu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品参数（键值对，挂SPU）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_product_param")
public class ProductParam extends BaseEntity {

    /** SPU ID */
    private Long productId;

    /** 参数名（如：传感器尺寸） */
    private String name;

    /** 参数值（如：全画幅 2420万像素） */
    private String value;

    /** 排序（越小越靠前） */
    private Integer sort;
}
