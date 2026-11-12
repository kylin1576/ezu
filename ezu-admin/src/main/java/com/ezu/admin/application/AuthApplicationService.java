package com.ezu.admin.application;

import cn.dev33.satoken.session.SaSession;
import com.ezu.admin.domain.entity.AdminUser;
import com.ezu.admin.domain.req.AdminLoginReq;
import com.ezu.admin.domain.vo.AdminLoginVo;
import com.ezu.admin.domain.vo.AdminUserInfoVo;
import com.ezu.admin.domainService.AdminUserDomainService;
import com.ezu.admin.domainService.AuthDomainService;
import com.ezu.common.exception.BusinessException;
import com.ezu.common.satoken.StpAdminUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 认证应用服务
 */
@Service("adminAuthApplicationService")
@RequiredArgsConstructor
public class AuthApplicationService {

    /** 会话键：登录账号 */
    public static final String SESSION_KEY_USERNAME = "username";

    /** 会话键：姓名 */
    public static final String SESSION_KEY_REAL_NAME = "realName";

    private final AuthDomainService authDomainService;
    private final AdminUserDomainService adminUserDomainService;

    /**
     * B端登录
     */
    public AdminLoginVo login(AdminLoginReq req) {
        AdminUser adminUser = authDomainService.loginCheck(req.getUsername(), req.getPassword());
        StpAdminUtil.login(adminUser.getId());
        SaSession session = StpAdminUtil.getSession();
        session.set(SESSION_KEY_USERNAME, adminUser.getUsername());
        session.set(SESSION_KEY_REAL_NAME, adminUser.getRealName());
        AdminLoginVo vo = new AdminLoginVo();
        vo.setToken(StpAdminUtil.getTokenValue());
        vo.setUserId(adminUser.getId());
        vo.setUsername(adminUser.getUsername());
        vo.setRealName(adminUser.getRealName());
        return vo;
    }

    /**
     * 退出登录
     */
    public void logout() {
        StpAdminUtil.logout();
    }

    /**
     * 当前登录人信息（角色编码 + 权限编码）
     */
    public AdminUserInfoVo info() {
        Long userId = StpAdminUtil.getAdminUserId();
        AdminUser adminUser = adminUserDomainService.getById(userId);
        if (adminUser == null) {
            throw new BusinessException("账号不存在或已删除");
        }
        AdminUserInfoVo vo = new AdminUserInfoVo();
        vo.setUserId(adminUser.getId());
        vo.setUsername(adminUser.getUsername());
        vo.setRealName(adminUser.getRealName());
        vo.setAvatar(adminUser.getAvatar());
        vo.setRoles(authDomainService.getRoleCodes(adminUser.getId()));
        vo.setPermissions(authDomainService.getPermissionCodes(adminUser.getId()));
        return vo;
    }
}
