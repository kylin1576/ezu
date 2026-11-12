package com.ezu.order.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 支付上下文（ezu-payment 发起支付时跨模块读取，不依赖详情视图组装）
 */
@Data
public class PaymentContextVo {

    /** 订单号 */
    private String orderNo;

    /** 下单用户ID */
    private Long userId;

    /**
     * 订单状态（支付只允许 10=待支付；20 配合成功支付单幂等返回）
     * <p>
     * {@link com.ezu.order.domain.enums.OrderStatus}
     */
    private Integer status;

    /** 应付金额（元） */
    private BigDecimal payAmount;

    /** 待支付租金账单号（支付记录关联账单；无待支付账单时为空） */
    private String unpaidBillNo;
}
