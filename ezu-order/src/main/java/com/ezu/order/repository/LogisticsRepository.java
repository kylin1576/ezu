package com.ezu.order.repository;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.ezu.order.domain.entity.Logistics;
import com.ezu.order.repository.mapper.LogisticsMapper;
import org.springframework.stereotype.Repository;

/**
 * 物流记录数据访问
 */
@Repository
public class LogisticsRepository extends ServiceImpl<LogisticsMapper, Logistics> {

    /**
     * 取该订单最新一段物流（id 最大；一期寄出/寄回各一段）
     */
    public Logistics getLatestByOrderNo(String orderNo) {
        return this.lambdaQuery()
                .eq(Logistics::getOrderNo, orderNo)
                .orderByDesc(Logistics::getId)
                .last("LIMIT 1")
                .one();
    }
}
