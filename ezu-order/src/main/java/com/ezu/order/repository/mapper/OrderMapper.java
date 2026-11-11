package com.ezu.order.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ezu.order.domain.entity.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * 租赁订单 Mapper
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
