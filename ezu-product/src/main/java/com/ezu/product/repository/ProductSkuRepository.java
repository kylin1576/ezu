package com.ezu.product.repository;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.ezu.product.domain.entity.ProductSku;
import com.ezu.product.repository.mapper.ProductSkuMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 商品SKU数据访问
 */
@Repository
public class ProductSkuRepository extends ServiceImpl<ProductSkuMapper, ProductSku> {

    /**
     * 按SPU ID批量查询（id 升序，与全删全插的提交顺序一致）
     */
    public List<ProductSku> listByProductIds(Collection<Long> productIds) {
        if (CollUtil.isEmpty(productIds)) {
            return new ArrayList<>();
        }
        return this.lambdaQuery()
                .in(ProductSku::getProductId, productIds)
                .orderByAsc(ProductSku::getId)
                .list();
    }

    /**
     * 逻辑删指定SPU下全部SKU（整体保存-全删全插用）
     */
    public void removeByProductId(Long productId) {
        this.lambdaUpdate().eq(ProductSku::getProductId, productId).remove();
    }
}
