package com.ezu.order.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ezu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 押金流水（收取/退还/扣除全程可审计；写入在 T8 履约）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_deposit_record")
public class DepositRecord extends BaseEntity {

    /** 押金流水号（D+yyyyMMdd+4位） */
    private String recordNo;

    /** 订单号 */
    private String orderNo;

    /** 用户ID */
    private Long userId;

    /** 金额（元） */
    private BigDecimal amount;

    /**
     * 操作类型（1=收取/冻结，2=退还，3=扣除【3为定损预留】）
     * <p>
     * {@link com.ezu.order.domain.enums.DepositType}
     */
    private Integer type;

    /** 渠道（1=模拟，2=支付宝信用免押【2预留】） */
    private Integer channel;

    /** 操作原因 */
    private String reason;
}
