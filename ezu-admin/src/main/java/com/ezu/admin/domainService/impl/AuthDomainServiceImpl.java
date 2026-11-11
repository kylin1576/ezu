package com.ezu.admin.domainService.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.ezu.admin.domain.entity.AdminUser;
import com.ezu.admin.domain.entity.AdminUserRole;
import com.ezu.admin.domain.entity.Permission;
import com.ezu.admin.domain.entity.Role;
import com.ezu.admin.domain.entity.RolePermission;
import com.ezu.admin.domain.enums.AdminUserStatus;
import com.ezu.admin.domainService.AuthDomainService;
import com.ezu.admin.repository.AdminUserRepository;
import com.ezu.admin.repository.AdminUserRoleRepository;
import com.ezu.admin.repository.PermissionRepository;
import com.ezu.admin.repository.RolePermissionRepository;
import com.ezu.admin.repository.RoleRepository;
import com.ezu.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 认证领域服务实现
 */
@Service
@RequiredArgsConstructor
public class AuthDomainServiceImpl implements AuthDomainService {

    /** BCryptPasswordEncoder 无状态线程安全，静态复用 */
    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    private final AdminUserRepository adminUserRepository;
    private final AdminUserRoleRepository adminUserRoleRepository;
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public AdminUser loginCheck(String username, String password) {
        AdminUser adminUser = adminUserRepository.getByUsername(username);
        if (adminUser == null || !PASSWORD_ENCODER.matches(password, adminUser.getPassword())) {
            throw new BusinessException("账号或密码错误");
        }
        if (Objects.equals(adminUser.getStatus(), AdminUserStatus.DISABLED)) {
            throw new BusinessException("账号已禁用");
        }
        adminUserRepository.lambdaUpdate()
                .eq(AdminUser::getId, adminUser.getId())
                .set(AdminUser::getLastLoginTime, LocalDateTime.now())
                .update();
        adminUser.setLastLoginTime(LocalDateTime.now());
        return adminUser;
    }

    @Override
    public List<String> getPermissionCodes(Long adminUserId) {
        // 第一步：账号 → 角色ID集
        List<Long> roleIds = adminUserRoleRepository.listByAdminUserId(adminUserId).stream()
                .map(AdminUserRole::getRoleId)
                .distinct()
                .toList();
        if (CollectionUtil.isEmpty(roleIds)) {
            return new ArrayList<>();
        }
        // 第二步：角色ID集 → 权限ID集（单次 IN 查询）
        List<Long> permissionIds = rolePermissionRepository.listByRoleIds(roleIds).stream()
                .map(RolePermission::getPermissionId)
                .distinct()
                .toList();
        if (CollectionUtil.isEmpty(permissionIds)) {
            return new ArrayList<>();
        }
        // 第三步：权限ID集 → 权限编码（单次 IN 查询）
        return permissionRepository.listByIds(permissionIds).stream()
                .map(Permission::getPermissionCode)
                .filter(StrUtil::isNotBlank)
                .distinct()
                .toList();
    }

    @Override
    public List<String> getRoleCodes(Long adminUserId) {
        // 两步批量：账号 → 角色ID集 → 角色编码
        List<Long> roleIds = adminUserRoleRepository.listByAdminUserId(adminUserId).stream()
                .map(AdminUserRole::getRoleId)
                .distinct()
                .toList();
        if (CollectionUtil.isEmpty(roleIds)) {
            return new ArrayList<>();
        }
        return roleRepository.listByIds(roleIds).stream()
                .map(Role::getCode)
                .filter(StrUtil::isNotBlank)
                .distinct()
                .toList();
    }
}
