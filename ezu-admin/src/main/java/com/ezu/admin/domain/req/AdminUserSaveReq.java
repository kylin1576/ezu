package com.ezu.admin.domain.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 新增/编辑后台账号请求（id 空=新增）
 */
@Data
public class AdminUserSaveReq {

    /** 账号ID（编辑必传，新增不传） */
    private Long id;

    /** 登录账号 */
    @NotBlank(message = "登录账号不能为空")
    @Size(max = 32, message = "登录账号最大32个字符")
    private String username;

    /** 姓名 */
    @Size(max = 32, message = "姓名最大32个字符")
    private String realName;

    /** 手机号 */
    @Size(max = 20, message = "手机号最大20个字符")
    private String phone;

    /** 密码（新增必填；编辑不传=不修改） */
    @Size(max = 64, message = "密码最大64个字符")
    private String password;

    /** 状态（1=启用，2=禁用） */
    @NotNull(message = "状态不能为空")
    private Integer status;

    /** 角色ID列表（空列表=清空全部角色） */
    @NotNull(message = "角色列表不能为空")
    private List<Long> roleIds;
}
