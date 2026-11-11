package com.ezu.admin.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ezu.admin.domain.entity.Role;
import org.apache.ibatis.annotations.Mapper;

/**
 * 后台角色 Mapper
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {
}
