package com.ezu.user.api;

import com.ezu.common.response.ResultData;
import com.ezu.user.application.AuthApplicationService;
import com.ezu.user.domain.req.LoginReq;
import com.ezu.user.domain.vo.AppLoginVo;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * C端认证
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/app/auth")
public class AppAuthController {

    private final AuthApplicationService authApplicationService;

    /**
     * 登录（mock）
     */
    @PostMapping("/login")
    public ResultData<AppLoginVo> login(@Validated @RequestBody LoginReq req) {
        return ResultData.success(authApplicationService.login(req));
    }

    /**
     * 登出
     */
    @PostMapping("/logout")
    public ResultData<Void> logout() {
        authApplicationService.logout();
        return ResultData.success(null);
    }
}
