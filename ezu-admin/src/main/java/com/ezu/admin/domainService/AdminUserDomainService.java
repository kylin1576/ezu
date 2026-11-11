package com.ezu.admin.domainService;

import com.ezu.admin.domain.entity.AdminUser;
import com.ezu.admin.domain.req.AdminUserQueryReq;
import com.ezu.admin.domain.req.AdminUserSaveReq;
import com.ezu.admin.domain.vo.AdminUserListVo;
import com.ezu.common.page.PageVo;

/**
 * 后台账号领域服务
 */
public interface AdminUserDomainService {

    /**
     * 分页查询后台账号
     */
    PageVo<AdminUserListVo> queryPage(AdminUserQueryReq req);

    /**
     * 新增/编辑后台账号（id 空=新增），返回账号ID
     */
    Long save(AdminUserSaveReq req);

    /**
     * 按ID查询账号
     */
    AdminUser getById(Long id);
}
