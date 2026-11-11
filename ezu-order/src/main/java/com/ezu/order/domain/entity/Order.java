package com.ezu.order.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ezu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 租赁订单主表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_order")
public class Order extends BaseEntity {

    /** 订单号（R+yyyyMMdd+4位） */
    private String orderNo;

    /** 用户ID */
    private Long userId;

    /**
     * 订单状态（10=待支付，20=待发货，30=已发货，40=租赁中，50=归还中，60=验收中，70=已完成，80=已取消）
     * <p>
     * {@link com.ezu.order.domain.enums.OrderStatus}
     */
    private Integer status;

    /** 总租金（元） */
    private BigDecimal totalRent;

    /** 押金金额（元） */
    private BigDecimal depositAmount;

    /** 应付金额（元，一期=总租金+运费） */
    private BigDecimal payAmount;

    /** 运费（元，一期恒0） */
    private BigDecimal freight;

    /**
     * 计费单位（1=日，2=周，3=月，4=季）
     * <p>
     * 取值同 {@link com.ezu.product.domain.enums.RentalUnit}
     */
    private Integer leaseUnit;

    /** 单价快照（元/周期，下单时租金方案价格） */
    private BigDecimal unitPrice;

    /** 租期天数 */
    private Integer rentDays;

    /** 起租日期 */
    private LocalDate rentStartDate;

    /** 预计归还日期（起租+租期；实际以租约为准） */
    private LocalDate rentEndDate;

    /** 收货人（下单时快照） */
    private String receiver;

    /** 收货电话（快照） */
    private String receiverPhone;

    /** 收货地址（快照，省市区+详细） */
    private String receiverAddress;

    /** 取消原因 */
    private String cancelReason;

    /** 订单备注 */
    private String remark;
}
