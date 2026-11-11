package com.ezu.user.domain.vo;

import com.ezu.user.domain.enums.UserStatus;
import lombok.Data;

/**
 * C端用户信息（不含实名/身份证/信用状态等预留字段）
 */
@Data
public class UserVo {

    /** 用户ID */
    private Long id;

    /** 昵称 */
    private String nickname;

    /** 头像URL */
    private String avatar;

    /** 手机号 */
    private String phone;

    /** 微信openid */
    private String wxOpenid;

    /** 支付宝openid */
    private String aliOpenid;

    /**
     * 状态（1=正常，2=禁用，3=黑名单）
     * <p>
     * {@link UserStatus}
     */
    private Integer status;
}
