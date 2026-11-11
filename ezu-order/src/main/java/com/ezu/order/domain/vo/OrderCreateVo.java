package com.ezu.order.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 创建订单结果
 */
@Data
public class OrderCreateVo {

    /** 订单号 */
    private String orderNo;

    /** 应付金额（元） */
    private BigDecimal payAmount;
}
