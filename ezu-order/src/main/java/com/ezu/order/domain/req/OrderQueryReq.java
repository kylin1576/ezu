package com.ezu.order.domain.req;

import com.ezu.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * C端订单列表查询请求（只查本人）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderQueryReq extends PageQuery {

    /**
     * 订单状态（10=待支付，20=待发货，30=已发货，40=租赁中，50=归还中，60=验收中，70=已完成，80=已取消；可选，缺省=全部）
     * <p>
     * {@link com.ezu.order.domain.enums.OrderStatus}
     */
    private Integer status;
}
