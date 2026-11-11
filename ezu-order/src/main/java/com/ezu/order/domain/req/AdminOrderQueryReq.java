package com.ezu.order.domain.req;

import com.ezu.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * B端订单列表查询请求（全量视图）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AdminOrderQueryReq extends PageQuery {

    /** 订单号（精确匹配） */
    private String orderNo;

    /** 用户ID */
    private Long userId;

    /**
     * 订单状态（10=待支付，20=待发货，30=已发货，40=租赁中，50=归还中，60=验收中，70=已完成，80=已取消）
     * <p>
     * {@link com.ezu.order.domain.enums.OrderStatus}
     */
    private Integer status;

    /** 下单日期-起始（yyyy-MM-dd，含当日） */
    private LocalDate createTimeStart;

    /** 下单日期-结束（yyyy-MM-dd，含当日） */
    private LocalDate createTimeEnd;
}
