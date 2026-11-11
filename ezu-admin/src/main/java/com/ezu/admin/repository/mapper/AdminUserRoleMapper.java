package com.ezu.admin.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ezu.admin.domain.entity.AdminUserRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 后台账号角色关联 Mapper
 */
@Mapper
public interface AdminUserRoleMapper extends BaseMapper<AdminUserRole> {
}
