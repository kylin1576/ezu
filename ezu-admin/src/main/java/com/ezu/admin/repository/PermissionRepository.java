package com.ezu.admin.repository;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.ezu.admin.domain.entity.Permission;
import com.ezu.admin.repository.mapper.PermissionMapper;
import org.springframework.stereotype.Repository;

/**
 * 后台权限数据访问
 */
@Repository
public class PermissionRepository extends ServiceImpl<PermissionMapper, Permission> {
}
