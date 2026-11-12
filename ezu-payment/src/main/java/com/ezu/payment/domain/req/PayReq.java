package com.ezu.payment.domain.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * C端发起支付请求
 */
@Data
public class PayReq {

    /** 订单号 */
    @NotBlank(message = "订单号不能为空")
    private String orderNo;
}
