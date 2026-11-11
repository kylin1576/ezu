package com.ezu.admin.application;

import com.ezu.admin.domain.req.RoleQueryReq;
import com.ezu.admin.domain.req.RoleSaveReq;
import com.ezu.admin.domain.vo.RoleVo;
import com.ezu.admin.domainService.RoleDomainService;
import com.ezu.common.page.PageVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 后台角色应用服务
 */
@Service
@RequiredArgsConstructor
public class RoleApplicationService {

    private final RoleDomainService roleDomainService;

    /**
     * 分页查询角色
     */
    public PageVo<RoleVo> list(RoleQueryReq req) {
        return roleDomainService.queryPage(req);
    }

    /**
     * 新增/编辑角色
     */
    public Long save(RoleSaveReq req) {
        return roleDomainService.save(req);
    }
}
