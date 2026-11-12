package com.ezu.payment.channel;

import com.ezu.payment.domain.entity.PaymentRecord;
import com.ezu.payment.domain.enums.PayChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 模拟支付渠道（一期）：同步受理成功，状态推进统一由回调链路落库
 */
@Slf4j
@Component
public class MockPayChannel implements PaymentChannel {

    @Override
    public Integer channelCode() {
        return PayChannel.MOCK;
    }

    @Override
    public boolean pay(PaymentRecord record) {
        // 模拟「用户确认支付」即时成功；不产生渠道交易号
        log.info("模拟支付受理成功：paymentNo={}, orderNo={}, amount={}",
                record.getPaymentNo(), record.getOrderNo(), record.getAmount());
        return true;
    }
}
