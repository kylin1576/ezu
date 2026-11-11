package com.ezu.order.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ezu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 租约（初始租期+续租段，一单多段；收货确认时写入，T8 履约）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_lease")
public class Lease extends BaseEntity {

    /** 订单号 */
    private String orderNo;

    /**
     * 租约类型（1=初始租期，2=续租【续租为预留功能】）
     * <p>
     * {@link com.ezu.order.domain.enums.LeaseType}
     */
    private Integer type;

    /** 本段开始日期 */
    private LocalDate startDate;

    /** 本段结束日期 */
    private LocalDate endDate;

    /** 本段租金（元，续租段单独计费） */
    private BigDecimal rent;
}
