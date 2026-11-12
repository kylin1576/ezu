package com.ezu.order.api;

import com.ezu.common.page.PageVo;
import com.ezu.common.response.ResultData;
import com.ezu.order.application.OrderApplicationService;
import com.ezu.order.domain.req.AcceptCheckReq;
import com.ezu.order.domain.req.AdminOrderQueryReq;
import com.ezu.order.domain.req.LogisticsInfoReq;
import com.ezu.order.domain.vo.OrderDetailVo;
import com.ezu.order.domain.vo.OrderListVo;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * B端订单管理（列表/详情 + 履约流转：发货/归还登记/验收）
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/order")
public class AdminOrderController {

    private final OrderApplicationService orderApplicationService;

    /**
     * 订单分页列表（全量视图：单号/用户/状态/日期范围/逾期）
     */
    @PostMapping("/list")
    public ResultData<PageVo<OrderListVo>> list(@Validated @RequestBody AdminOrderQueryReq req) {
        return ResultData.success(orderApplicationService.adminList(req));
    }

    /**
     * 订单详情（全量视图，含下单用户ID）
     */
    @GetMapping("/detail/{orderNo}")
    public ResultData<OrderDetailVo> detail(@PathVariable String orderNo) {
        return ResultData.success(orderApplicationService.adminDetail(orderNo));
    }

    /**
     * 发货（待发货→已发货，登记寄出物流，设备→在租）
     */
    @PostMapping("/ship/{orderNo}")
    public ResultData<Void> ship(@PathVariable String orderNo, @Validated @RequestBody LogisticsInfoReq req) {
        orderApplicationService.ship(orderNo, req);
        return ResultData.success(null);
    }

    /**
     * 登记归还寄回物流（订单状态不变，仅记录）
     */
    @PostMapping("/returnSign/{orderNo}")
    public ResultData<Void> returnSign(@PathVariable String orderNo, @Validated @RequestBody LogisticsInfoReq req) {
        orderApplicationService.returnSign(orderNo, req);
        return ResultData.success(null);
    }

    /**
     * 归还验收（归还中→验收中，保存验收报告）
     *
     * @return true=验收通过且订单已完成；false=不通过停在验收中（定损二期处理）
     */
    @PostMapping("/acceptCheck/{orderNo}")
    public ResultData<Boolean> acceptCheck(@PathVariable String orderNo, @Validated @RequestBody AcceptCheckReq req) {
        return ResultData.success(orderApplicationService.acceptCheck(orderNo, req));
    }

    /**
     * 验收完成确认（验收中→已完成，设备回在库，押金退还流水）
     */
    @PostMapping("/acceptConfirm/{orderNo}")
    public ResultData<Void> acceptConfirm(@PathVariable String orderNo) {
        orderApplicationService.acceptConfirm(orderNo);
        return ResultData.success(null);
    }
}
