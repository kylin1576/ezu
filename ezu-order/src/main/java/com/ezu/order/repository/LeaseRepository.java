package com.ezu.order.repository;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.ezu.order.domain.entity.Lease;
import com.ezu.order.repository.mapper.LeaseMapper;
import org.springframework.stereotype.Repository;

/**
 * 租约数据访问
 */
@Repository
public class LeaseRepository extends ServiceImpl<LeaseMapper, Lease> {

    /**
     * 取该订单最新一段租约（id 最大；一期仅初始租期一段）
     */
    public Lease getLatestByOrderNo(String orderNo) {
        return this.lambdaQuery()
                .eq(Lease::getOrderNo, orderNo)
                .orderByDesc(Lease::getId)
                .last("LIMIT 1")
                .one();
    }
}
