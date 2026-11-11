package com.ezu.order.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 下单试算结果
 */
@Data
public class OrderTrialVo {

    /** 单周期租金（元/周期） */
    private BigDecimal unitPrice;

    /** 总租金（元） */
    private BigDecimal totalRent;

    /** 押金（元） */
    private BigDecimal deposit;

    /** 运费（元，一期恒0） */
    private BigDecimal freight;

    /** 应付金额（元，=总租金+运费） */
    private BigDecimal payAmount;
}
