package com.ezu.product.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ezu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品品类（两级树）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_category")
public class Category extends BaseEntity {

    /** 父级品类ID（0=顶级） */
    private Long parentId;

    /** 品类名称 */
    private String name;

    /** 品类图标URL */
    private String icon;

    /** 排序（越小越靠前） */
    private Integer sort;

    /**
     * 状态（1=启用，2=禁用）
     * <p>
     * {@link com.ezu.product.domain.enums.CategoryStatus}
     */
    private Integer status;
}
