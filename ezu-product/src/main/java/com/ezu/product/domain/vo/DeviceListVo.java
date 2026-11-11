package com.ezu.product.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 设备SN台账列表项
 */
@Data
public class DeviceListVo {

    private Long id;

    private String deviceNo;

    private String sn;

    private Long skuId;

    /** 规格名称（回显；SKU因SPU编辑被逻辑删时为空，属正常） */
    private String specName;

    /** 商品名称（回显；SKU被删时为空） */
    private String productName;

    /**
     * 设备状态（1=在库，2=锁定，3=在租，4=维修，5=报废，6=归还验收中）
     * <p>
     * {@link com.ezu.product.domain.enums.DeviceStatus}
     */
    private Integer status;

    /** 成色定级（1=全新，2=准新，3=轻微使用痕迹，4=明显使用痕迹） */
    private Integer conditionLevel;

    private LocalDate purchaseDate;

    private BigDecimal purchasePrice;

    private String purchaseChannel;

    private String remark;

    private LocalDateTime createTime;
}
