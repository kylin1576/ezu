package com.ezu.admin.domain.req;

import com.ezu.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色列表查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RoleQueryReq extends PageQuery {

    /** 角色名称（模糊） */
    private String name;

    /** 状态（1=启用，2=禁用） */
    private Integer status;
}
