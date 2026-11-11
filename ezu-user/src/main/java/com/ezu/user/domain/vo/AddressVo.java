package com.ezu.user.domain.vo;

import lombok.Data;

/**
 * 收货地址
 */
@Data
public class AddressVo {

    /** 地址ID */
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 收货人 */
    private String receiver;

    /** 联系电话 */
    private String phone;

    /** 省 */
    private String province;

    /** 市 */
    private String city;

    /** 区 */
    private String district;

    /** 详细地址 */
    private String detailAddress;

    /** 默认地址（0=否，1=是） */
    private Integer isDefault;
}
