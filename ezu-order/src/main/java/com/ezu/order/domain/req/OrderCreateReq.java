package com.ezu.order.domain.req;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建订单请求
 */
@Data
public class OrderCreateReq {

    @NotNull(message = "SKU不能为空")
    private Long skuId;

    @NotNull(message = "租金方案不能为空")
    private Long rentalPlanId;

    @NotNull(message = "租期天数不能为空")
    @Min(value = 1, message = "租期天数最小为1天")
    private Integer rentDays;

    @NotNull(message = "收货地址不能为空")
    private Long addressId;

    @Size(max = 500, message = "订单备注最长500字")
    private String remark;
}
