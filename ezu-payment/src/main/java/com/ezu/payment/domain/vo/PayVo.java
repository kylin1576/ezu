package com.ezu.payment.domain.vo;

import lombok.Data;

/**
 * 发起支付返回（mock 渠道即时成功，status 已为成功）
 */
@Data
public class PayVo {

    /** 支付单号 */
    private String paymentNo;

    /**
     * 支付单状态（1=待支付，2=成功，3=失败，4=已退款）
     * <p>
     * {@link com.ezu.payment.domain.enums.PayStatus}
     */
    private Integer status;
}
