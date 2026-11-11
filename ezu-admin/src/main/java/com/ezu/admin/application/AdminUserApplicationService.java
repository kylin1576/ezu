package com.ezu.admin.application;

import com.ezu.admin.domain.req.AdminUserQueryReq;
import com.ezu.admin.domain.req.AdminUserSaveReq;
import com.ezu.admin.domain.vo.AdminUserListVo;
import com.ezu.admin.domainService.AdminUserDomainService;
import com.ezu.common.page.PageVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 后台账号应用服务
 */
@Service
@RequiredArgsConstructor
public class AdminUserApplicationService {

    private final AdminUserDomainService adminUserDomainService;

    /**
     * 分页查询后台账号
     */
    public PageVo<AdminUserListVo> list(AdminUserQueryReq req) {
        return adminUserDomainService.queryPage(req);
    }

    /**
     * 新增/编辑后台账号（账号落库 + 角色全删全插，同事务）
     */
    @Transactional(rollbackFor = Exception.class)
    public Long save(AdminUserSaveReq req) {
        return adminUserDomainService.save(req);
    }
}
