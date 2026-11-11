package com.ezu.order.repository;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.ezu.order.domain.entity.Bill;
import com.ezu.order.repository.mapper.BillMapper;
import org.springframework.stereotype.Repository;

/**
 * 账单数据访问
 */
@Repository
public class BillRepository extends ServiceImpl<BillMapper, Bill> {

    /**
     * 取该订单最新一条账单（id 最大；一期一单一条租金账单）
     */
    public Bill getLatestByOrderNo(String orderNo) {
        return this.lambdaQuery()
                .eq(Bill::getOrderNo, orderNo)
                .orderByDesc(Bill::getId)
                .last("LIMIT 1")
                .one();
    }
}
