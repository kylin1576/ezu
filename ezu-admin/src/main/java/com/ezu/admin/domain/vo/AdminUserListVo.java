package com.ezu.admin.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 后台账号列表项（显式声明字段，杜绝 password 泄漏）
 */
@Data
public class AdminUserListVo {

    /** 账号ID */
    private Long id;

    /** 登录账号 */
    private String username;

    /** 姓名 */
    private String realName;

    /** 手机号 */
    private String phone;

    /** 头像URL */
    private String avatar;

    /**
     * 状态（1=启用，2=禁用）
     * <p>
     * {@link com.ezu.admin.domain.enums.AdminUserStatus}
     */
    private Integer status;

    /** 最后登录时间 */
    private LocalDateTime lastLoginTime;

    /** 创建时间 */
    private LocalDateTime createTime;
}
