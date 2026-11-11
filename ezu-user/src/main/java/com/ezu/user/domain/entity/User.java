package com.ezu.user.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ezu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * C端用户
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_user")
public class User extends BaseEntity {

    /** 昵称 */
    private String nickname;

    /** 头像URL */
    private String avatar;

    /** 手机号（绑定后可跨微信/支付宝两端合并账号） */
    private String phone;

    /** 微信openid */
    private String wxOpenid;

    /** 支付宝openid */
    private String aliOpenid;

    /** 实名姓名（预留：信用免押二期） */
    private String realName;

    /** 身份证号（预留：信用免押二期，上线前需加密存储） */
    private String idCardNo;

    /**
     * 状态（1=正常，2=禁用，3=黑名单）
     * <p>
     * {@link com.ezu.user.domain.enums.UserStatus}
     */
    private Integer status;

    /** 信用状态（0=未开通，1=已开通免押）（预留：支付宝芝麻信用） */
    private Integer creditStatus;
}
