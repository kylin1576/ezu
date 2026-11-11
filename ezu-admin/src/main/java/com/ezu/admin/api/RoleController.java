package com.ezu.admin.api;

import com.ezu.admin.application.RoleApplicationService;
import com.ezu.admin.domain.req.RoleQueryReq;
import com.ezu.admin.domain.req.RoleSaveReq;
import com.ezu.admin.domain.vo.RoleVo;
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
 * 后台角色管理
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/role")
public class RoleController {

    private final RoleApplicationService roleApplicationService;

    /**
     * 角色列表
     */
    @PostMapping("/list")
    public ResultData<PageVo<RoleVo>> list(@Validated @RequestBody RoleQueryReq req) {
        return ResultData.success(roleApplicationService.list(req));
    }

    /**
     * 新增/编辑角色
     */
    @OperateLog(module = "系统管理", operation = "保存角色")
    @PostMapping("/save")
    public ResultData<Long> save(@Validated @RequestBody RoleSaveReq req) {
        return ResultData.success(roleApplicationService.save(req));
    }
}
