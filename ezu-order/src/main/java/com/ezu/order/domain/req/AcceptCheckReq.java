package com.ezu.order.domain.req;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * B端归还验收请求（50归还中→60验收中；pass=true 直接推进已完成）
 */
@Data
public class AcceptCheckReq {

    /** 外观结论（1=完好，2=轻微划痕，3=明显损伤） */
    @Min(value = 1, message = "外观结论只能为1~3")
    @Max(value = 3, message = "外观结论只能为1~3")
    private Integer exteriorResult;

    /** 功能结论（1=正常，2=异常） */
    @Min(value = 1, message = "功能结论只能为1或2")
    @Max(value = 2, message = "功能结论只能为1或2")
    private Integer functionResult;

    /** 成色定级（1=全新，2=准新，3=轻微使用痕迹，4=明显使用痕迹；回写设备成色） */
    @Min(value = 1, message = "成色定级只能为1~4")
    @Max(value = 4, message = "成色定级只能为1~4")
    private Integer conditionLevel;

    /** 验收照片（url列表） */
    private List<String> attachments;

    /** 验收员 */
    @NotBlank(message = "验收员不能为空")
    private String checker;

    /** 备注 */
    private String remark;

    /** 是否通过：true=验收通过直接完成订单；false=停在验收中（定损二期处理） */
    @NotNull(message = "验收结论不能为空")
    private Boolean pass;
}
