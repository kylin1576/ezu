package com.ezu.user.api;

import com.ezu.common.page.PageVo;
import com.ezu.common.response.ResultData;
import com.ezu.user.application.UserApplicationService;
import com.ezu.user.domain.req.UserQueryReq;
import com.ezu.user.domain.vo.UserListVo;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * B端C端用户管理（一期只读）
 * <p>
 * 注意：路径用 /api/admin/appUser 区分于 ezu-admin 的 /api/admin/user（后台账号管理），
 * 同名类 AdminUserController / 同路径 /api/admin/user/list 会与 ezu-admin 模块产生
 * Bean 名冲突与 URL 二义性（启动失败），故本模块刻意避开
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/appUser")
public class AppUserAdminController {

    private final UserApplicationService userApplicationService;

    /**
     * 用户列表
     */
    @PostMapping("/list")
    public ResultData<PageVo<UserListVo>> list(@Validated @RequestBody UserQueryReq req) {
        return ResultData.success(userApplicationService.list(req));
    }
}
