package com.ezu.admin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ezu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 后台管理账号
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_admin_user")
public class AdminUser extends BaseEntity {

    /** 登录账号 */
    private String username;

    /** 密码（bcrypt 加密存储，任何接口禁止返回） */
    private String password;

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
}
