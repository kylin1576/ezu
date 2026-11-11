package com.ezu.admin.satoken;

import cn.dev33.satoken.stp.StpInterface;
import com.ezu.admin.domainService.AuthDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Sa-Token 鉴权数据源：admin 账号体系返回 RBAC 角色/权限编码，其他账号体系返回空
 */
@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

    private final AuthDomainService authDomainService;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        if ("admin".equals(loginType)) {
            return authDomainService.getPermissionCodes(Long.valueOf(loginId.toString()));
        }
        return Collections.emptyList();
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        if ("admin".equals(loginType)) {
            return authDomainService.getRoleCodes(Long.valueOf(loginId.toString()));
        }
        return Collections.emptyList();
    }
}
