package com.ezu.order.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ezu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 订单明细（一期单SKU单设备，仍保留明细结构；order_no 关联主表）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_order_item")
public class OrderItem extends BaseEntity {

    /** 订单号 */
    private String orderNo;

    /** SKU ID */
    private Long skuId;

    /** 设备ID（锁定的SN台账行，一行一设备） */
    private Long deviceId;

    /** 商品名称快照 */
    private String productName;

    /** 规格名称快照 */
    private String skuSpecName;

    /** 规格图快照URL */
    private String skuImage;

    /** 单价快照（元/周期） */
    private BigDecimal priceSnapshot;

    /** 押金快照（元） */
    private BigDecimal depositSnapshot;
}
