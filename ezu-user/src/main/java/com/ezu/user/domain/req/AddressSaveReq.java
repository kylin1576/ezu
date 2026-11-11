package com.ezu.user.domain.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 收货地址保存请求（id为空=新增，非空=编辑）
 */
@Data
public class AddressSaveReq {

    /** 地址ID（编辑必传） */
    private Long id;

    /** 收货人 */
    @NotBlank(message = "收货人不能为空")
    private String receiver;

    /** 联系电话 */
    @NotBlank(message = "联系电话不能为空")
    private String phone;

    /** 省 */
    @NotBlank(message = "省不能为空")
    private String province;

    /** 市 */
    @NotBlank(message = "市不能为空")
    private String city;

    /** 区（直辖市等无区时可不传） */
    private String district;

    /** 详细地址 */
    @NotBlank(message = "详细地址不能为空")
    private String detailAddress;

    /** 默认地址（0=否，1=是），不传默认否 */
    private Integer isDefault = 0;
}
