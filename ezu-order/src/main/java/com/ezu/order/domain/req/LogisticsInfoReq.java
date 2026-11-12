package com.ezu.order.domain.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 物流信息登记请求（B端发货 ship 与归还寄回登记 returnSign 共用）
 */
@Data
public class LogisticsInfoReq {

    /** 快递公司（如 顺丰速运） */
    @NotBlank(message = "快递公司不能为空")
    @Size(max = 64, message = "快递公司不能超过64个字符")
    private String company;

    /** 快递单号 */
    @NotBlank(message = "快递单号不能为空")
    @Size(max = 64, message = "快递单号不能超过64个字符")
    private String logisticsNo;
}
