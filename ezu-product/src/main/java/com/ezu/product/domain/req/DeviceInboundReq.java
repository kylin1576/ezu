package com.ezu.product.domain.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 设备采购入库请求（按SKU逐台录入，返回设备编号列表）
 */
@Data
public class DeviceInboundReq {

    /** SKU ID（不校验启用，允许给停用SKU补录设备） */
    @NotNull(message = "SKU不能为空")
    private Long skuId;

    /** 入库设备明细（逐台录入） */
    @Valid
    @NotEmpty(message = "入库明细不能为空")
    private List<InboundItem> items;

    /**
     * 入库设备项
     */
    @Data
    public static class InboundItem {

        /** 厂商序列号（部分设备无SN可为空） */
        private String sn;

        /** 采购日期（yyyy-MM-dd） */
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate purchaseDate;

        /** 采购价（元） */
        private BigDecimal purchasePrice;

        /** 采购渠道 */
        private String purchaseChannel;

        /** 成色定级（1=全新，2=准新，3=轻微使用痕迹，4=明显使用痕迹） */
        private Integer conditionLevel;

        /** 备注 */
        private String remark;
    }
}
