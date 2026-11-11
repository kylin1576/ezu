package com.ezu.order.domain.req;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 下单试算请求（确认页金额以服务端为准）
 */
@Data
public class OrderTrialReq {

    @NotNull(message = "SKU不能为空")
    private Long skuId;

    @NotNull(message = "租金方案不能为空")
    private Long rentalPlanId;

    @NotNull(message = "租期天数不能为空")
    @Min(value = 1, message = "租期天数最小为1天")
    private Integer rentDays;
}
