package com.ezu.admin.repository;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.ezu.admin.domain.entity.RolePermission;
import com.ezu.admin.repository.mapper.RolePermissionMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 角色权限关联数据访问
 */
@Repository
public class RolePermissionRepository extends ServiceImpl<RolePermissionMapper, RolePermission> {

    /**
     * 按角色ID集合批量查询
     */
    public List<RolePermission> listByRoleIds(Collection<Long> roleIds) {
        if (CollectionUtil.isEmpty(roleIds)) {
            return new ArrayList<>();
        }
        return this.lambdaQuery().in(RolePermission::getRoleId, roleIds).list();
    }

    /**
     * 按角色ID查询全部权限关联
     */
    public List<RolePermission> listByRoleId(Long roleId) {
        return this.lambdaQuery().eq(RolePermission::getRoleId, roleId).list();
    }
}
