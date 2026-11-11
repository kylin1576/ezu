package com.ezu.product.repository;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.ezu.product.domain.entity.Category;
import com.ezu.product.repository.mapper.CategoryMapper;
import org.springframework.stereotype.Repository;

/**
 * 商品品类数据访问
 */
@Repository
public class CategoryRepository extends ServiceImpl<CategoryMapper, Category> {

    /**
     * 统计指定品类的直接子品类数
     */
    public long countByParentId(Long parentId) {
        return this.lambdaQuery().eq(Category::getParentId, parentId).count();
    }
}
