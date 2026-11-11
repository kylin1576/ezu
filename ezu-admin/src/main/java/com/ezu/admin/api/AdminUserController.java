package com.ezu.admin.api;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ezu.admin.application.AdminUserApplicationService;
import com.ezu.admin.domain.req.AdminUserQueryReq;
import com.ezu.admin.domain.req.AdminUserSaveReq;
import com.ezu.admin.domain.vo.AdminUserListVo;
import com.ezu.admin.log.OperateLog;
import com.ezu.common.page.PageVo;
import com.ezu.common.response.ResultData;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台账号管理
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/user")
public class AdminUserController {

    private final AdminUserApplicationService adminUserApplicationService;

    /**
     * 账号列表
     */
    @PostMapping("/list")
    public ResultData<PageVo<AdminUserListVo>> list(@Validated @RequestBody AdminUserQueryReq req) {
        return ResultData.success(adminUserApplicationService.list(req));
    }

    /**
     * 新增/编辑账号
     */
    @SaCheckPermission(value = "user:save", type = "admin")
    @OperateLog(module = "系统管理", operation = "保存后台账号")
    @PostMapping("/save")
    public ResultData<Long> save(@Validated @RequestBody AdminUserSaveReq req) {
        return ResultData.success(adminUserApplicationService.save(req));
    }
}

