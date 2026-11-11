package com.ezu.product.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ezu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 设备维修记录
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_device_repair")
public class DeviceRepair extends BaseEntity {

    /** 设备ID */
    private Long deviceId;

    /** 关联订单号（租期内报修填写，自发故障为空） */
    private String orderNo;

    /** 故障描述 */
    private String faultDesc;

    /** 维修费用（元） */
    private BigDecimal repairCost;

    /** 费用承担（1=平台，2=用户）（预留：定损理赔） */
    private Integer costBearer;

    /** 维修开始日期 */
    private LocalDate startDate;

    /** 维修结束日期 */
    private LocalDate endDate;

    /** 维修结果 */
    private String result;
}
