package com.ezu.admin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ezu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 后台账号角色关联
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_admin_user_role")
public class AdminUserRole extends BaseEntity {

    /** 后台账号ID */
    private Long adminUserId;

    /** 角色ID */
    private Long roleId;
}
