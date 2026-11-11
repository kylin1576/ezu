package com.ezu.admin.domainService;

import com.ezu.admin.domain.entity.AdminUser;

import java.util.List;

/**
 * 认证领域服务
 */
public interface AuthDomainService {

    /**
     * 登录校验：账号密码/状态校验，通过后回写最后登录时间并返回账号实体
     */
    AdminUser loginCheck(String username, String password);

    /**
     * 查询账号权限编码列表（账号角色 → 角色权限 → 权限表，三步批量查询）
     */
    List<String> getPermissionCodes(Long adminUserId);

    /**
     * 查询账号角色编码列表（账号角色 → 角色表，两步批量查询）
     */
    List<String> getRoleCodes(Long adminUserId);
}
