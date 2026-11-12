package com.ezu.order.domain.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 物流信息登记请求（B端发货 ship 与归还寄回登记 returnSign 共用）
 */
@Data
public class LogisticsInfoReq {

    /** 快递公司（如 顺丰速运） */
    @NotBlank(message = "快递公司不能为空")
    private String company;

    /** 快递单号 */
    @NotBlank(message = "快递单号不能为空")
    private String logisticsNo;
}
