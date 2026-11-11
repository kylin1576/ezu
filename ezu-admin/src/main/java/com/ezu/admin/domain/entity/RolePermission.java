package com.ezu.admin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ezu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色权限关联
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_role_permission")
public class RolePermission extends BaseEntity {

    /** 角色ID */
    private Long roleId;

    /** 权限ID */
    private Long permissionId;
}
