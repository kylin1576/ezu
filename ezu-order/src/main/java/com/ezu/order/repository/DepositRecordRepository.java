package com.ezu.order.repository;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.ezu.order.domain.entity.DepositRecord;
import com.ezu.order.repository.mapper.DepositRecordMapper;
import org.springframework.stereotype.Repository;

/**
 * 押金流水数据访问
 */
@Repository
public class DepositRecordRepository extends ServiceImpl<DepositRecordMapper, DepositRecord> {

    /**
     * 取该订单最新一条押金流水（id 最大）
     */
    public DepositRecord getLatestByOrderNo(String orderNo) {
        return this.lambdaQuery()
                .eq(DepositRecord::getOrderNo, orderNo)
                .orderByDesc(DepositRecord::getId)
                .last("LIMIT 1")
                .one();
    }
}
