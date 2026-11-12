package com.ezu.payment.api;

import com.ezu.common.response.ResultData;
import com.ezu.common.satoken.StpUserUtil;
import com.ezu.payment.application.PaymentApplicationService;
import com.ezu.payment.domain.req.PayReq;
import com.ezu.payment.domain.vo.PayVo;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * C端支付
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/app/payment")
public class PayController {

    private final PaymentApplicationService paymentApplicationService;

    /**
     * 发起支付（mock 渠道即时成功，返回时订单已推进待发货）
     */
    @PostMapping("/pay")
    public ResultData<PayVo> pay(@Validated @RequestBody PayReq req) {
        return ResultData.success(paymentApplicationService.pay(StpUserUtil.getUserId(), req));
    }
}
