package com.ezu.user.domain.vo;

import lombok.Data;

/**
 * C端登录结果
 */
@Data
public class AppLoginVo {

    /** 会话令牌（前端后续请求放入 ezu-token 请求头） */
    private String token;

    /** 用户ID */
    private Long userId;

    /** 昵称 */
    private String nickname;

    /** 头像URL */
    private String avatar;

    /** 是否本次登录新注册 */
    private Boolean isNew;
}
