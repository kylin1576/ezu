package com.ezu.product.domain.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 设备维修记录保存请求
 */
@Data
public class DeviceRepairSaveReq {

    /** 设备ID */
    @NotNull(message = "设备不能为空")
    private Long deviceId;

    /** 关联订单号（租期内报修填写，自发故障为空） */
    private String orderNo;

    /** 故障描述 */
    @NotBlank(message = "故障描述不能为空")
    private String faultDesc;

    /** 维修费用（元） */
    private BigDecimal repairCost;

    /** 费用承担（1=平台，2=用户） */
    private Integer costBearer;

    /** 维修开始日期（yyyy-MM-dd） */
    @NotNull(message = "维修开始日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    /** 维修结束日期（yyyy-MM-dd；非空=维修完成，设备回在库） */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    /** 维修结果 */
    private String result;
}
