package com.ezu.payment.domain.enums;

/**
 * 支付渠道
 */
public interface PayChannel {

    /** 模拟（一期） */
    int MOCK = 1;

    /** 支付宝（预留） */
    int ALIPAY = 2;

    /** 微信（预留） */
    int WECHAT = 3;
}
