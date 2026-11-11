package com.ezu.admin.domain.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * B端登录请求
 */
@Data
public class AdminLoginReq {

    /** 登录账号 */
    @NotBlank(message = "登录账号不能为空")
    private String username;

    /** 密码 */
    @NotBlank(message = "密码不能为空")
    private String password;
}
