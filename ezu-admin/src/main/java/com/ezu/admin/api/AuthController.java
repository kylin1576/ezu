package com.ezu.admin.api;

import com.ezu.admin.application.AuthApplicationService;
import com.ezu.admin.domain.req.AdminLoginReq;
import com.ezu.admin.domain.vo.AdminLoginVo;
import com.ezu.admin.domain.vo.AdminUserInfoVo;
import com.ezu.common.response.ResultData;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * B端认证
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/auth")
public class AuthController {

    private final AuthApplicationService authApplicationService;

    /**
     * B端登录
     */
    @PostMapping("/login")
    public ResultData<AdminLoginVo> login(@Validated @RequestBody AdminLoginReq req) {
        return ResultData.success(authApplicationService.login(req));
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public ResultData<Void> logout() {
        authApplicationService.logout();
        return ResultData.success(null);
    }

    /**
     * 当前登录人信息
     */
    @GetMapping("/info")
    public ResultData<AdminUserInfoVo> info() {
        return ResultData.success(authApplicationService.info());
    }
}
