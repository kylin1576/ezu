package com.ezu.admin.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.ezu.admin.domain.entity.AdminUserRole;
import com.ezu.admin.repository.mapper.AdminUserRoleMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 后台账号角色关联数据访问
 */
@Repository
public class AdminUserRoleRepository extends ServiceImpl<AdminUserRoleMapper, AdminUserRole> {

    /**
     * 按账号ID查询全部角色关联
     */
    public List<AdminUserRole> listByAdminUserId(Long adminUserId) {
        return this.lambdaQuery().eq(AdminUserRole::getAdminUserId, adminUserId).list();
    }

    /**
     * 删除账号的全部角色关联（逻辑删除）
     */
    public void removeByAdminUserId(Long adminUserId) {
        this.remove(new LambdaQueryWrapper<AdminUserRole>().eq(AdminUserRole::getAdminUserId, adminUserId));
    }
}
