package com.ezu.admin.domain.req;

import com.ezu.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 后台账号列表查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AdminUserQueryReq extends PageQuery {

    /** 登录账号（模糊） */
    private String username;

    /** 姓名（模糊） */
    private String realName;

    /** 状态（1=启用，2=禁用） */
    private Integer status;
}
