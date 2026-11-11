package com.ezu.order.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ezu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 物流记录（发货/归还双向，一订单可多段；写入在 T8 履约）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_logistics")
public class Logistics extends BaseEntity {

    /** 订单号 */
    private String orderNo;

    /**
     * 物流类型（1=发货寄出，2=归还寄回）
     * <p>
     * {@link com.ezu.order.domain.enums.LogisticsType}
     */
    private Integer type;

    /** 快递公司（如 顺丰速运） */
    private String company;

    /** 快递单号 */
    private String trackingNo;

    /** 运费（元） */
    private BigDecimal fee;

    /**
     * 状态（1=已发出，2=已签收）
     * <p>
     * {@link com.ezu.order.domain.enums.LogisticsStatus}
     */
    private Integer status;

    /** 备注 */
    private String remark;
}
