package com.ezu.order.api;

import com.ezu.common.page.PageVo;
import com.ezu.common.response.ResultData;
import com.ezu.common.satoken.StpUserUtil;
import com.ezu.order.application.OrderApplicationService;
import com.ezu.order.domain.req.OrderCreateReq;
import com.ezu.order.domain.req.OrderQueryReq;
import com.ezu.order.domain.req.OrderTrialReq;
import com.ezu.order.domain.vo.OrderCreateVo;
import com.ezu.order.domain.vo.OrderDetailVo;
import com.ezu.order.domain.vo.OrderListVo;
import com.ezu.order.domain.vo.OrderTrialVo;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * C端订单
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/app/order")
public class AppOrderController {

    private final OrderApplicationService orderApplicationService;

    /**
     * 下单试算（确认页金额以服务端为准）
     */
    @PostMapping("/trial")
    public ResultData<OrderTrialVo> trial(@Validated @RequestBody OrderTrialReq req) {
        return ResultData.success(orderApplicationService.trial(req));
    }

    /**
     * 创建订单（锁设备→建单→待支付账单）
     */
    @PostMapping("/create")
    public ResultData<OrderCreateVo> create(@Validated @RequestBody OrderCreateReq req) {
        return ResultData.success(orderApplicationService.create(StpUserUtil.getUserId(), req));
    }

    /**
     * 订单详情（本人校验，含明细/租约/账单/押金/物流）
     */
    @GetMapping("/detail/{orderNo}")
    public ResultData<OrderDetailVo> detail(@PathVariable String orderNo) {
        return ResultData.success(orderApplicationService.detail(StpUserUtil.getUserId(), orderNo));
    }

    /**
     * 订单分页列表（只查本人）
     */
    @PostMapping("/list")
    public ResultData<PageVo<OrderListVo>> list(@Validated @RequestBody OrderQueryReq req) {
        return ResultData.success(orderApplicationService.list(StpUserUtil.getUserId(), req));
    }

    /**
     * 取消订单（待支付/待发货→已取消，释放锁定设备）
     */
    @PostMapping("/cancel/{orderNo}")
    public ResultData<Void> cancel(@PathVariable String orderNo) {
        orderApplicationService.cancel(StpUserUtil.getUserId(), orderNo);
        return ResultData.success(null);
    }
}
