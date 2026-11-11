package com.ezu.admin.domainService;

import com.ezu.admin.domain.req.RoleQueryReq;
import com.ezu.admin.domain.req.RoleSaveReq;
import com.ezu.admin.domain.vo.RoleVo;
import com.ezu.common.page.PageVo;

/**
 * 后台角色领域服务
 */
public interface RoleDomainService {

    /**
     * 分页查询角色（pageSize=-1 查全量）
     */
    PageVo<RoleVo> queryPage(RoleQueryReq req);

    /**
     * 新增/编辑角色（id 空=新增），返回角色ID
     */
    Long save(RoleSaveReq req);
}
