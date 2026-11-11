package com.ezu.order.domain.vo;

import com.ezu.order.domain.enums.BillStatus;
import com.ezu.order.domain.enums.BillType;
import com.ezu.order.domain.enums.DepositType;
import com.ezu.order.domain.enums.LeaseType;
import com.ezu.order.domain.enums.LogisticsStatus;
import com.ezu.order.domain.enums.LogisticsType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 订单详情（主表字段 + 明细/租约/账单/押金/物流，均可能为空：一期租赁中之前无租约，
 * 下单时无押金流水，发货前无物流）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderDetailVo extends OrderListVo {

    /** 下单用户ID（B端视图需要；C端即本人） */
    private Long userId;

    /** 租约（取最新一段，确认收货后生成） */
    private LeaseVo lease;

    /** 账单（取最新一条） */
    private BillVo bill;

    /** 押金流水（取最新一条，履约过程中写入） */
    private DepositVo deposit;

    /** 物流记录（取最新一段，发货后写入） */
    private LogisticsVo logistics;

    /**
     * 租约项
     */
    @Data
    public static class LeaseVo {

        /**
         * 租约类型（1=初始租期，2=续租【预留】）
         * <p>
         * {@link LeaseType}
         */
        private Integer type;

        /** 本段开始日期 */
        private LocalDate startDate;

        /** 本段结束日期 */
        private LocalDate endDate;

        /** 本段租金（元） */
        private BigDecimal rent;
    }

    /**
     * 账单项
     */
    @Data
    public static class BillVo {

        /** 账单号 */
        private String billNo;

        /**
         * 账单类型（1=租金，2=续租租金，3=买断差额，4=维修费【2/3/4预留】）
         * <p>
         * {@link BillType}
         */
        private Integer type;

        /** 金额（元） */
        private BigDecimal amount;

        /**
         * 状态（1=已支付，2=待支付，3=已退款）
         * <p>
         * {@link BillStatus}
         */
        private Integer status;

        /** 支付时间 */
        private LocalDateTime payTime;
    }

    /**
     * 押金流水项
     */
    @Data
    public static class DepositVo {

        /** 押金流水号 */
        private String recordNo;

        /**
         * 操作类型（1=收取/冻结，2=退还，3=扣除【定损预留】）
         * <p>
         * {@link DepositType}
         */
        private Integer type;

        /** 金额（元） */
        private BigDecimal amount;

        /** 渠道（1=模拟，2=支付宝信用免押【预留】） */
        private Integer channel;

        /** 操作原因 */
        private String reason;

        /** 操作时间 */
        private LocalDateTime createTime;
    }

    /**
     * 物流项
     */
    @Data
    public static class LogisticsVo {

        /**
         * 物流类型（1=发货寄出，2=归还寄回）
         * <p>
         * {@link LogisticsType}
         */
        private Integer type;

        /** 快递公司 */
        private String company;

        /** 快递单号（t_logistics.tracking_no） */
        private String logisticsNo;

        /** 运费（元） */
        private BigDecimal fee;

        /**
         * 状态（1=已发出，2=已签收）
         * <p>
         * {@link LogisticsStatus}
         */
        private Integer status;

        /** 发出时间 */
        private LocalDateTime createTime;
    }
}
