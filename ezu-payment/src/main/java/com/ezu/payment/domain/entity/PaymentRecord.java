package com.ezu.payment.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ezu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付记录（渠道无关；一期 mock 渠道，二期支付宝/微信只加实现不改表）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_payment_record")
public class PaymentRecord extends BaseEntity {

    /** 支付单号（P+yyyyMMdd+4位） */
    private String paymentNo;

    /** 订单号 */
    private String orderNo;

    /** 关联账单号（biz_type=1 租金时填写） */
    private String billNo;

    /** 用户ID */
    private Long userId;

    /**
     * 业务类型（1=租金，2=押金冻结【2预留】）
     * <p>
     * {@link com.ezu.payment.domain.enums.BizType}
     */
    private Integer bizType;

    /** 金额（元） */
    private BigDecimal amount;

    /**
     * 支付渠道（1=模拟，2=支付宝，3=微信）
     * <p>
     * {@link com.ezu.payment.domain.enums.PayChannel}
     */
    private Integer channel;

    /**
     * 状态（1=待支付，2=成功，3=失败，4=已退款）
     * <p>
     * {@link com.ezu.payment.domain.enums.PayStatus}
     */
    private Integer status;

    /** 渠道交易号（模拟渠道为空） */
    private String channelTradeNo;

    /** 回调时间 */
    private LocalDateTime callbackTime;
}
