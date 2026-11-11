package com.ezu.user.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ezu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户收货地址
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_user_address")
public class UserAddress extends BaseEntity {

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

    /** 默认地址（0=否，1=是；每用户至多一条默认） */
    private Integer isDefault;
}
