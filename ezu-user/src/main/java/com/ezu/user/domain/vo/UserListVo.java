package com.ezu.user.domain.vo;

import com.ezu.user.domain.enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * B端用户列表项
 */
@Data
public class UserListVo {

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

    /** 注册时间（yyyy-MM-dd HH:mm:ss） */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
