package com.ezu.user.domain.req;

import com.ezu.user.domain.enums.AuthChannel;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * C端登录请求
 */
@Data
public class LoginReq {

    /** 登录渠道（1=微信，2=支付宝） */
    @NotNull(message = "channel不能为空")
    @Min(value = AuthChannel.WECHAT, message = "channel仅支持1=微信、2=支付宝")
    @Max(value = AuthChannel.ALIPAY, message = "channel仅支持1=微信、2=支付宝")
    private Integer channel;

    /** 授权码（mock 期任意字符串） */
    @NotBlank(message = "code不能为空")
    private String code;
}
