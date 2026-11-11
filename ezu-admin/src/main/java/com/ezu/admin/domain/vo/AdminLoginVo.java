package com.ezu.admin.domain.vo;

import lombok.Data;

/**
 * B端登录结果
 */
@Data
public class AdminLoginVo {

    /** 会话令牌（前端后续请求放入 ezu-token 请求头） */
    private String token;

    /** 账号ID */
    private Long userId;

    /** 登录账号 */
    private String username;

    /** 姓名 */
    private String realName;
}
