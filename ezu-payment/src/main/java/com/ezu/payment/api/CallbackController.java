package com.ezu.payment.api;

import com.ezu.common.response.ResultData;
import com.ezu.payment.application.PaymentApplicationService;
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
     * 渠道回调入口（channel=渠道编码：1模拟 2支付宝 3微信）
     */
    @PostMapping("/{channel}")
    public ResultData<Void> callback(@PathVariable Integer channel, @Validated @RequestBody PaymentCallbackReq req) {
        paymentApplicationService.callback(channel, req);
        return ResultData.success(null);
    }
}
