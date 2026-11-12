package com.ezu.product.domain.req;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 设备质检/验收报告保存请求
 */
@Data
public class DeviceCheckSaveReq {

    /** 设备ID */
    @NotNull(message = "设备不能为空")
    private Long deviceId;

    /** 报告类型（1=入库质检，2=归还验收） */
    @NotNull(message = "报告类型不能为空")
    @Min(value = 1, message = "报告类型只能为1或2")
    @Max(value = 2, message = "报告类型只能为1或2")
    private Integer type;

    /** 关联订单号（type=2 归还验收时填写；入库质检为空） */
    private String orderNo;

    /** 外观结论（1=完好，2=轻微划痕，3=明显损伤） */
    private Integer exteriorResult;

    /** 功能结论（1=正常，2=异常） */
    private Integer functionResult;

    /** 成色定级（1=全新，2=准新，3=轻微使用痕迹，4=明显使用痕迹；入库质检时回写设备成色） */
    private Integer conditionLevel;

    /** 质检照片（url列表，后端转JSON存储） */
    private List<String> attachments;

    /** 质检员 */
    private String checker;

    /** 备注 */
    private String remark;
}
