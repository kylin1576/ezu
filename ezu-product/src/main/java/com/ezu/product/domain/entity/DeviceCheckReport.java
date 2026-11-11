package com.ezu.product.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ezu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 设备质检/验收报告
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_device_check_report")
public class DeviceCheckReport extends BaseEntity {

    /** 设备ID */
    private Long deviceId;

    /** 报告类型（1=入库质检，2=归还验收） */
    private Integer type;

    /** 关联订单号（归还验收时填写） */
    private String orderNo;

    /** 外观结论（1=完好，2=轻微划痕，3=明显损伤） */
    private Integer exteriorResult;

    /** 功能结论（1=正常，2=异常） */
    private Integer functionResult;

    /** 验收后成色定级（1=全新，2=准新，3=轻微使用痕迹，4=明显使用痕迹；定级后回写t_device） */
    private Integer conditionLevel;

    /** 质检照片（JSON数组：[{name,url}]） */
    private String attachments;

    /** 质检员 */
    private String checker;

    /** 备注 */
    private String remark;
}
