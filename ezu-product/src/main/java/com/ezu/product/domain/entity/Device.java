package com.ezu.product.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ezu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 设备SN台账（一台实机一行）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_device")
public class Device extends BaseEntity {

    /** 设备资产编号（Z+yyyyMMdd+4位，平台自编，二维码标签用） */
    private String deviceNo;

    /** 厂商序列号（部分设备无SN可为空） */
    private String sn;

    /** 所属SKU ID */
    private Long skuId;

    /**
     * 设备状态（1=在库，2=锁定，3=在租，4=维修，5=报废，6=归还验收中）
     * <p>
     * {@link com.ezu.product.domain.enums.DeviceStatus}
     */
    private Integer status;

    /** 成色定级（1=全新，2=准新，3=轻微使用痕迹，4=明显使用痕迹） */
    private Integer conditionLevel;

    /** 采购日期 */
    private LocalDate purchaseDate;

    /** 采购价（元） */
    private BigDecimal purchasePrice;

    /** 采购渠道 */
    private String purchaseChannel;

    /** 备注 */
    private String remark;
}
