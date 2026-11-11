package com.ezu.admin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ezu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 后台角色
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_role")
public class Role extends BaseEntity {

    /** 角色名称（如 超级管理员/运营） */
    private String name;

    /** 角色编码（如 admin/operator） */
    private String code;

    /** 备注 */
    private String remark;

    /**
     * 状态（1=启用，2=禁用）
     * <p>
     * {@link com.ezu.admin.domain.enums.RoleStatus}
     */
    private Integer status;
}
