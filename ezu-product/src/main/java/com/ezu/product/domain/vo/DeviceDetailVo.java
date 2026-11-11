package com.ezu.product.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 设备SN台账详情（主表 + SKU/商品回显 + 质检报告列表 + 维修记录列表）
 */
@Data
public class DeviceDetailVo {

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

    /** 质检/验收报告列表（新在前） */
    private List<CheckReportVo> checkReports = new ArrayList<>();

    /** 维修记录列表（新在前） */
    private List<RepairVo> repairs = new ArrayList<>();

    /**
     * 质检/验收报告项
     */
    @Data
    public static class CheckReportVo {

        private Long id;

        /** 报告类型（1=入库质检，2=归还验收） */
        private Integer type;

        /** 关联订单号（归还验收时填写） */
        private String orderNo;

        /** 外观结论（1=完好，2=轻微划痕，3=明显损伤） */
        private Integer exteriorResult;

        /** 功能结论（1=正常，2=异常） */
        private Integer functionResult;

        /** 成色定级（1=全新，2=准新，3=轻微使用痕迹，4=明显使用痕迹） */
        private Integer conditionLevel;

        /** 质检照片（url列表，由JSON还原） */
        private List<String> attachments = new ArrayList<>();

        /** 质检员 */
        private String checker;

        /** 备注 */
        private String remark;

        private LocalDateTime createTime;
    }

    /**
     * 维修记录项
     */
    @Data
    public static class RepairVo {

        private Long id;

        /** 关联订单号（租期内报修填写，自发故障为空） */
        private String orderNo;

        /** 故障描述 */
        private String faultDesc;

        /** 维修费用（元） */
        private BigDecimal repairCost;

        /** 费用承担（1=平台，2=用户） */
        private Integer costBearer;

        /** 维修开始日期 */
        private LocalDate startDate;

        /** 维修结束日期 */
        private LocalDate endDate;

        /** 维修结果 */
        private String result;

        private LocalDateTime createTime;
    }
}
