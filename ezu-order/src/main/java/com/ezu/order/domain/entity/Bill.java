package com.ezu.order.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ezu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 账单（一期租金一次性支付一条记录；周期付二期）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_bill")
public class Bill extends BaseEntity {

    /** 账单号（B+yyyyMMdd+4位） */
    private String billNo;

    /** 订单号 */
    private String orderNo;

    /** 用户ID */
    private Long userId;

    /**
     * 账单类型（1=租金，2=续租租金，3=买断差额，4=维修费【2/3/4预留】）
     * <p>
     * {@link com.ezu.order.domain.enums.BillType}
     */
    private Integer type;

    /** 金额（元） */
    private BigDecimal amount;

    /**
     * 状态（1=已支付，2=待支付，3=已退款）
     * <p>
     * {@link com.ezu.order.domain.enums.BillStatus}
     */
    private Integer status;

    /** 支付时间 */
    private LocalDateTime payTime;
}
