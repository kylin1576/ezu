package com.ezu.payment.api;

import com.ezu.common.exception.BusinessException;
import com.ezu.common.response.ResultData;
import com.ezu.payment.application.PaymentApplicationService;
import com.ezu.payment.domain.enums.PayChannel;
import com.ezu.payment.domain.req.PaymentCallbackReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 支付渠道回调（免登录：/api/callback/** 不在 C/B 端拦截路由内；
 * 防重与幂等由 PaymentApplicationService.callback 条件更新保证）
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/callback/payment")
public class CallbackController {

    private final PaymentApplicationService paymentApplicationService;

    /**
     * 渠道回调入口（channel=渠道编码：2支付宝 3微信）
     * <p>
     * mock 渠道（1）无外部回调：支付单号可枚举且无签名校验，放行即等于任意待支付单可被伪造刷成功，
     * 故 mock 只允许 pay() 内部驱动回调链路；二期真实渠道接入时补签名校验+金额核对
     */
    @PostMapping("/{channel}")
    public ResultData<Void> callback(@PathVariable Integer channel, @Validated @RequestBody PaymentCallbackReq req) {
        if (PayChannel.MOCK == channel) {
            throw new BusinessException("模拟渠道不支持外部回调");
        }
        paymentApplicationService.callback(channel, req);
        return ResultData.success(null);
    }
}
