package com.ezu.order.api;

import com.ezu.common.page.PageVo;
import com.ezu.common.response.ResultData;
import com.ezu.order.application.OrderApplicationService;
import com.ezu.order.domain.req.AdminOrderQueryReq;
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
 * B端订单管理（一期只读：写接口发货/验收等在 T8 履约任务补）
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/order")
public class AdminOrderController {

    private final OrderApplicationService orderApplicationService;

    /**
     * 订单分页列表（全量视图：单号/用户/状态/日期范围）
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
}
