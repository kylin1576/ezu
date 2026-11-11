package com.ezu.order.repository;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.ezu.order.domain.entity.Order;
import com.ezu.order.domain.req.AdminOrderQueryReq;
import com.ezu.order.domain.req.OrderQueryReq;
import com.ezu.order.repository.mapper.OrderMapper;
import org.springframework.stereotype.Repository;

/**
 * 租赁订单数据访问
 */
@Repository
public class OrderRepository extends ServiceImpl<OrderMapper, Order> {

    /**
     * C端分页（强制只查本人，状态可选；pageSize=-1 查全量）
     */
    public IPage<Order> queryPage(Long userId, OrderQueryReq req) {
        Page<Order> page = new Page<>(req.getPageNum(), req.getPageSize());
        return this.lambdaQuery()
                .eq(Order::getUserId, userId)
                .eq(req.getStatus() != null, Order::getStatus, req.getStatus())
                .orderByDesc(Order::getCreateTime)
                .orderByDesc(Order::getId)
                .page(page);
    }

    /**
     * B端分页（全量视图：订单号精确/用户ID/状态/创建日期范围，左闭右开 [start, end+1) 防秒精度丢失）
     */
    public IPage<Order> queryAdminPage(AdminOrderQueryReq req) {
        Page<Order> page = new Page<>(req.getPageNum(), req.getPageSize());
        return this.lambdaQuery()
                .eq(StrUtil.isNotBlank(req.getOrderNo()), Order::getOrderNo, req.getOrderNo())
                .eq(req.getUserId() != null, Order::getUserId, req.getUserId())
                .eq(req.getStatus() != null, Order::getStatus, req.getStatus())
                .ge(req.getCreateTimeStart() != null, Order::getCreateTime, req.getCreateTimeStart().atStartOfDay())
                .lt(req.getCreateTimeEnd() != null, Order::getCreateTime, req.getCreateTimeEnd().plusDays(1).atStartOfDay())
                .orderByDesc(Order::getCreateTime)
                .orderByDesc(Order::getId)
                .page(page);
    }

    /**
     * 按订单号查询（order_no 唯一键）
     */
    public Order getByOrderNo(String orderNo) {
        return this.lambdaQuery()
                .eq(Order::getOrderNo, orderNo)
                .one();
    }
}
