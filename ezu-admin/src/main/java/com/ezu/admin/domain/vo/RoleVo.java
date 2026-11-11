package com.ezu.admin.domain.vo;

import com.ezu.admin.domain.entity.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色信息（继承实体，无敏感字段）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RoleVo extends Role {
}
