package com.ezu.admin.domainService.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.ezu.admin.domain.entity.AdminUser;
import com.ezu.admin.domain.entity.AdminUserRole;
import com.ezu.admin.domain.entity.Permission;
import com.ezu.admin.domain.entity.Role;
import com.ezu.admin.domain.entity.RolePermission;
import com.ezu.admin.domain.enums.AdminUserStatus;
import com.ezu.admin.domain.enums.PermissionStatus;
import com.ezu.admin.domain.enums.RoleStatus;
import com.ezu.admin.domainService.AuthDomainService;
import com.ezu.admin.repository.AdminUserRepository;
import com.ezu.admin.repository.AdminUserRoleRepository;
import com.ezu.admin.repository.PermissionRepository;
import com.ezu.admin.repository.RolePermissionRepository;
import com.ezu.admin.repository.RoleRepository;
import com.ezu.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 认证领域服务实现
 */
@Slf4j
@Service("adminAuthDomainServiceImpl")
@RequiredArgsConstructor
public class AuthDomainServiceImpl implements AuthDomainService {

    /** BCryptPasswordEncoder 无状态线程安全，静态复用 */
    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    /**
     * 时序侧信道防护占位哈希（"dummy" 的合法 BCrypt 串）：账号不存在时也执行一次等耗时的 matches，
     * 使两种失败路径耗时一致，无法通过响应时间探测账号是否存在
     */
    private static final String DUMMY_HASH = "$2a$10$Xi0OR0zN/jqWSebX6bTMf.TvWrU29OD/jS2Elk9h0GctRNtcdQGG6";

    private final AdminUserRepository adminUserRepository;
    private final AdminUserRoleRepository adminUserRoleRepository;
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public AdminUser loginCheck(String username, String password) {
        AdminUser adminUser = adminUserRepository.getByUsername(username);
        if (adminUser == null) {
            PASSWORD_ENCODER.matches(password, DUMMY_HASH);
            throw new BusinessException("账号或密码错误");
        }
        if (!PASSWORD_ENCODER.matches(password, adminUser.getPassword())) {
            throw new BusinessException("账号或密码错误");
        }
        if (Objects.equals(adminUser.getStatus(), AdminUserStatus.DISABLED)) {
            throw new BusinessException("账号已禁用");
        }
        updateLastLoginTime(adminUser.getId());
        return adminUser;
    }

    /**
     * 回写最后登录时间：失败仅告警，不影响登录主流程
     */
    private void updateLastLoginTime(Long adminUserId) {
        try {
            adminUserRepository.lambdaUpdate()
                    .eq(AdminUser::getId, adminUserId)
                    .set(AdminUser::getLastLoginTime, LocalDateTime.now())
                    .update();
        } catch (Exception e) {
            log.warn("回写最后登录时间失败：adminUserId={}", adminUserId, e);
        }
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
        // 第二步：角色ID集 → 只保留启用角色 → 权限ID集（批量 IN 查询）
        List<Long> enabledRoleIds = roleRepository.listByIds(roleIds).stream()
                .filter(role -> Objects.equals(role.getStatus(), RoleStatus.ENABLED))
                .map(Role::getId)
                .toList();
        if (CollectionUtil.isEmpty(enabledRoleIds)) {
            return new ArrayList<>();
        }
        List<Long> permissionIds = rolePermissionRepository.listByRoleIds(enabledRoleIds).stream()
                .map(RolePermission::getPermissionId)
                .distinct()
                .toList();
        if (CollectionUtil.isEmpty(permissionIds)) {
            return new ArrayList<>();
        }
        // 第三步：权限ID集 → 只保留启用权限 → 权限编码（单次 IN 查询）
        return permissionRepository.listByIds(permissionIds).stream()
                .filter(permission -> Objects.equals(permission.getStatus(), PermissionStatus.ENABLED))
                .map(Permission::getPermissionCode)
                .filter(StrUtil::isNotBlank)
                .distinct()
                .toList();
    }

    @Override
    public List<String> getRoleCodes(Long adminUserId) {
        // 两步批量：账号 → 角色ID集 → 启用角色的编码
        List<Long> roleIds = adminUserRoleRepository.listByAdminUserId(adminUserId).stream()
                .map(AdminUserRole::getRoleId)
                .distinct()
                .toList();
        if (CollectionUtil.isEmpty(roleIds)) {
            return new ArrayList<>();
        }
        return roleRepository.listByIds(roleIds).stream()
                .filter(role -> Objects.equals(role.getStatus(), RoleStatus.ENABLED))
                .map(Role::getCode)
                .filter(StrUtil::isNotBlank)
                .distinct()
                .toList();
    }
}
