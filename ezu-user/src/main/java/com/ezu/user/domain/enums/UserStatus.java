package com.ezu.user.domain.enums;

/**
 * 用户状态
 */
public interface UserStatus {

    /** 正常 */
    int NORMAL = 1;

    /** 禁用 */
    int DISABLED = 2;

    /** 黑名单 */
    int BLACKLIST = 3;
}
