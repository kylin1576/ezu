package com.ezu.order.domain.vo;

import com.ezu.order.domain.enums.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 订单列表项（主表字段 + 首条明细快照）
 */
@Data
public class OrderListVo {

    /** 订单号 */
    private String orderNo;

    /**
     * 订单状态（10=待支付，20=待发货，30=已发货，40=租赁中，50=归还中，60=验收中，70=已完成，80=已取消）
     * <p>
     * {@link OrderStatus}
     */
    private Integer status;

    /** 总租金（元） */
    private BigDecimal totalRent;

    /** 押金金额（元） */
    private BigDecimal depositAmount;

    /** 应付金额（元） */
    private BigDecimal payAmount;

    /** 运费（元） */
    private BigDecimal freight;

    /**
     * 计费单位（1=日，2=周，3=月，4=季）
     */
    private Integer leaseUnit;

    /** 单价快照（元/周期） */
    private BigDecimal unitPrice;

    /** 租期天数 */
    private Integer rentDays;

    /** 起租日期（收货确认后回填） */
    private LocalDate rentStartDate;

    /** 预计归还日期（收货确认后回填） */
    private LocalDate rentEndDate;

    /** 收货人 */
    private String receiver;

    /** 收货电话 */
    private String receiverPhone;

    /** 收货地址 */
    private String receiverAddress;

    /** 取消原因 */
    private String cancelReason;

    /** 订单备注 */
    private String remark;

    /** 下单时间 */
    private LocalDateTime createTime;

    /** 商品明细快照（一期一单一明细） */
    private ItemVo item;

    /**
     * 订单商品明细（快照）
     */
    @Data
    public static class ItemVo {

        /** SKU ID */
        private Long skuId;

        /** 设备ID */
        private Long deviceId;

        /** 商品名称 */
        private String productName;

        /** 规格名称 */
        private String skuSpecName;

        /** 规格图URL */
        private String skuImage;

        /** 单价快照（元/周期） */
        private BigDecimal priceSnapshot;

        /** 押金快照（元） */
        private BigDecimal depositSnapshot;
    }
}
