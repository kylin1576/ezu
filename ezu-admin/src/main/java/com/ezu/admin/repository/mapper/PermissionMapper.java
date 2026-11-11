package com.ezu.admin.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ezu.admin.domain.entity.Permission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 后台权限 Mapper
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {
}
