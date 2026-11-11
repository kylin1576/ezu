package com.ezu.product.repository;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.ezu.product.domain.entity.ProductParam;
import com.ezu.product.repository.mapper.ProductParamMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品参数数据访问
 */
@Repository
public class ProductParamRepository extends ServiceImpl<ProductParamMapper, ProductParam> {

    /**
     * 查询指定SPU的参数列表（sort、id 升序）
     */
    public List<ProductParam> listByProductId(Long productId) {
        return this.lambdaQuery()
                .eq(ProductParam::getProductId, productId)
                .orderByAsc(ProductParam::getSort)
                .orderByAsc(ProductParam::getId)
                .list();
    }

    /**
     * 逻辑删指定SPU下全部参数（整体保存-全删全插用）
     */
    public void removeByProductId(Long productId) {
        this.lambdaUpdate().eq(ProductParam::getProductId, productId).remove();
    }
}
