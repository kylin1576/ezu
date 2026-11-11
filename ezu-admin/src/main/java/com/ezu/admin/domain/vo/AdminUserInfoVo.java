package com.ezu.admin.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * 当前登录人信息（角色编码 + 权限编码）
 */
@Data
public class AdminUserInfoVo {

    /** 账号ID */
    private Long userId;

    /** 登录账号 */
    private String username;

    /** 姓名 */
    private String realName;

    /** 头像URL */
    private String avatar;

    /** 角色编码列表（如 admin） */
    private List<String> roles;

    /** 权限编码列表（如 user:save） */
    private List<String> permissions;
}
