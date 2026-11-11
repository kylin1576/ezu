package com.ezu.admin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ezu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 后台权限（目录/菜单/按钮树）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_permission")
public class Permission extends BaseEntity {

    /** 父级权限ID（0=顶级） */
    private Long parentId;

    /** 权限名称（如 商品管理/SKU新增） */
    private String name;

    /**
     * 类型（1=目录，2=菜单，3=按钮）
     * <p>
     * {@link com.ezu.admin.domain.enums.PermissionType}
     */
    private Integer type;

    /** 权限编码（如 user:save；按钮/接口鉴权必填，目录菜单可空） */
    private String permissionCode;

    /** 前端路由路径（目录/菜单填） */
    private String path;

    /** 菜单图标 */
    private String icon;

    /** 排序（越小越靠前） */
    private Integer sort;

    /**
     * 状态（1=启用，2=禁用）
     * <p>
     * {@link com.ezu.admin.domain.enums.PermissionStatus}
     */
    private Integer status;
}
