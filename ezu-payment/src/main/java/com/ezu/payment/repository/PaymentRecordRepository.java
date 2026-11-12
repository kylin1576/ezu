package com.ezu.payment.repository;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.ezu.payment.domain.entity.PaymentRecord;
import com.ezu.payment.domain.enums.PayStatus;
import com.ezu.payment.repository.mapper.PaymentRecordMapper;
import org.springframework.stereotype.Repository;

/**
 * 支付记录数据访问
 */
@Repository
public class PaymentRecordRepository extends ServiceImpl<PaymentRecordMapper, PaymentRecord> {

    /**
     * 按支付单号查询（payment_no 唯一键）
     */
    public PaymentRecord getByPaymentNo(String paymentNo) {
        return this.lambdaQuery()
                .eq(PaymentRecord::getPaymentNo, paymentNo)
                .one();
    }

    /**
     * 该订单最新一条待支付记录（存在则复用，防重复拉起支付单）
     */
    public PaymentRecord getWaitingByOrderNo(String orderNo) {
        return this.lambdaQuery()
                .eq(PaymentRecord::getOrderNo, orderNo)
                .eq(PaymentRecord::getStatus, PayStatus.WAIT_PAY)
                .orderByDesc(PaymentRecord::getId)
                .last("LIMIT 1")
                .one();
    }

    /**
     * 该订单最新一条成功记录（订单已支付时的幂等返回）
     */
    public PaymentRecord getSuccessByOrderNo(String orderNo) {
        return this.lambdaQuery()
                .eq(PaymentRecord::getOrderNo, orderNo)
                .eq(PaymentRecord::getStatus, PayStatus.SUCCESS)
                .orderByDesc(PaymentRecord::getId)
                .last("LIMIT 1")
                .one();
    }
}
