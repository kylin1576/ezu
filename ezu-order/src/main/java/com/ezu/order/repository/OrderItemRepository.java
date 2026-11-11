package com.ezu.order.repository;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.ezu.order.domain.entity.OrderItem;
import com.ezu.order.repository.mapper.OrderItemMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 订单明细数据访问
 */
@Repository
public class OrderItemRepository extends ServiceImpl<OrderItemMapper, OrderItem> {

    /**
     * 按订单号批量查询（id 升序，一期一单一明细取首条）
     */
    public List<OrderItem> listByOrderNos(Collection<String> orderNos) {
        if (CollUtil.isEmpty(orderNos)) {
            return new ArrayList<>();
        }
        return this.lambdaQuery()
                .in(OrderItem::getOrderNo, orderNos)
                .orderByAsc(OrderItem::getId)
                .list();
    }

    /**
     * 取该订单首条明细（id 最小；一期一单一设备）
     */
    public OrderItem getFirstByOrderNo(String orderNo) {
        return this.lambdaQuery()
                .eq(OrderItem::getOrderNo, orderNo)
                .orderByAsc(OrderItem::getId)
                .last("LIMIT 1")
                .one();
    }
}
