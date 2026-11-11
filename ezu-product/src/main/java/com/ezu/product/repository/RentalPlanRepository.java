package com.ezu.product.repository;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.ezu.product.domain.entity.RentalPlan;
import com.ezu.product.repository.mapper.RentalPlanMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * SKU租金方案数据访问
 */
@Repository
public class RentalPlanRepository extends ServiceImpl<RentalPlanMapper, RentalPlan> {

    /**
     * 按SKU ID批量查询（id 升序，与全删全插的提交顺序一致）
     */
    public List<RentalPlan> listBySkuIds(Collection<Long> skuIds) {
        if (CollUtil.isEmpty(skuIds)) {
            return new ArrayList<>();
        }
        return this.lambdaQuery()
                .in(RentalPlan::getSkuId, skuIds)
                .orderByAsc(RentalPlan::getId)
                .list();
    }

    /**
     * 逻辑删指定SKU集合下全部方案（整体保存-全删全插用）
     */
    public void removeBySkuIds(Collection<Long> skuIds) {
        if (CollUtil.isEmpty(skuIds)) {
            return;
        }
        this.lambdaUpdate().in(RentalPlan::getSkuId, skuIds).remove();
    }
}
