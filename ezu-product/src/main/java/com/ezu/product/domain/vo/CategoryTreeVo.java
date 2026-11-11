package com.ezu.product.domain.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 品类两级树节点
 */
@Data
public class CategoryTreeVo {

    private Long id;

    private Long parentId;

    private String name;

    private String icon;

    private Integer sort;

    /** 状态（1=启用，2=禁用） */
    private Integer status;

    /** 子品类（顶级节点下挂二级） */
    private List<CategoryTreeVo> children = new ArrayList<>();
}
