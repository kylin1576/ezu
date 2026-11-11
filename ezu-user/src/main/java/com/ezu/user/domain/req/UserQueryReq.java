package com.ezu.user.domain.req;

import com.ezu.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * B端用户列表查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserQueryReq extends PageQuery {

    /** 昵称（模糊） */
    private String nickname;

    /** 手机号（模糊） */
    private String phone;

    /** 状态（1=正常，2=禁用，3=黑名单） */
    private Integer status;
}
