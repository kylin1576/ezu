package com.ezu.admin.domain.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新增/编辑角色请求（id 空=新增）
 */
@Data
public class RoleSaveReq {

    /** 角色ID（编辑必传，新增不传） */
    private Long id;

    /** 角色名称 */
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 32, message = "角色名称最大32个字符")
    private String name;

    /** 角色编码 */
    @NotBlank(message = "角色编码不能为空")
    @Size(max = 32, message = "角色编码最大32个字符")
    private String code;

    /** 备注 */
    @Size(max = 200, message = "备注最大200个字符")
    private String remark;

    /** 状态（1=启用，2=禁用） */
    @NotNull(message = "状态不能为空")
    private Integer status;
}
