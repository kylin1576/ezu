package com.ezu.payment.channel;

import com.ezu.payment.domain.entity.PaymentRecord;

/**
 * 支付渠道抽象（二期支付宝/微信新增实现类，调用方不改）
 * <p>
 * 「发起支付」与「渠道回调」共用同一套落库逻辑：pay() 返回成功后由调用方
 * 统一走 callback 流程推进状态，保证两条链路一致
 */
public interface PaymentChannel {

    /**
     * 渠道编码，对应 t_payment_record.channel
     * <p>
     * {@link com.ezu.payment.domain.enums.PayChannel}
     */
    Integer channelCode();

    /**
     * 发起支付
     *
     * @return true=渠道侧受理成功（mock 即用户确认支付，可驱动回调）；
     *         false=受理失败（记录置失败，真实渠道此处返回支付参数/跳转串，二期扩展返回值）
     */
    boolean pay(PaymentRecord record);
}
