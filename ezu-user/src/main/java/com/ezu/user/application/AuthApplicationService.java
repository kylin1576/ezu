package com.ezu.user.application;

import com.ezu.user.domain.req.LoginReq;
import com.ezu.user.domain.vo.AppLoginVo;
import com.ezu.user.domainService.AuthDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * C端认证应用服务
 */
@Service("userAuthApplicationService")
@RequiredArgsConstructor
public class AuthApplicationService {

    private final AuthDomainService authDomainService;

    /**
     * mock 登录（新用户注册落库）
     */
    @Transactional(rollbackFor = Exception.class)
    public AppLoginVo login(LoginReq req) {
        return authDomainService.mockLogin(req);
    }

    /**
     * 登出
     */
    public void logout() {
        authDomainService.logout();
    }
}
