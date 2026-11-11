package com.ezu.product.repository;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.ezu.product.domain.entity.Product;
import com.ezu.product.domain.req.SpuQueryReq;
import com.ezu.product.repository.mapper.ProductMapper;
import org.springframework.stereotype.Repository;

/**
 * 商品SPU数据访问
 */
@Repository
public class ProductRepository extends ServiceImpl<ProductMapper, Product> {

    /**
     * 统计挂在指定品类下的商品数（品类删除守卫用）
     */
    public long countByCategoryId(Long categoryId) {
        return this.lambdaQuery().eq(Product::getCategoryId, categoryId).count();
    }

    /**
     * 分页查询（pageSize=-1 查全量）
     */
    public IPage<Product> queryPage(SpuQueryReq req) {
        Page<Product> page = new Page<>(req.getPageNum(), req.getPageSize());
        return this.lambdaQuery()
                .eq(req.getCategoryId() != null, Product::getCategoryId, req.getCategoryId())
                .like(StrUtil.isNotBlank(req.getBrand()), Product::getBrand, req.getBrand())
                .like(StrUtil.isNotBlank(req.getName()), Product::getName, req.getName())
                .eq(req.getStatus() != null, Product::getStatus, req.getStatus())
                .orderByDesc(Product::getCreateTime)
                .orderByDesc(Product::getId)
                .page(page);
    }
}
