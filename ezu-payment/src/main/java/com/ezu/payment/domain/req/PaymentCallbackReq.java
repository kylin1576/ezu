package com.ezu.payment.domain.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 支付回调请求（渠道回调 / 模拟渠道内部驱动共用）
 */
@Data
public class PaymentCallbackReq {

    /** 支付单号 */
    @NotBlank(message = "支付单号不能为空")
    private String paymentNo;

    /** 渠道交易号（模拟渠道为空） */
    private String channelTradeNo;
}
