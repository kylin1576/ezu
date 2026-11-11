package com.ezu.user.domainService;

import com.ezu.user.domain.req.LoginReq;
import com.ezu.user.domain.vo.AppLoginVo;

/**
 * C端认证领域服务
 */
public interface AuthDomainService {

    /**
     * mock 登录
     */
    AppLoginVo mockLogin(LoginReq req);

    /**
     * 登出
     */
    void logout();
}
