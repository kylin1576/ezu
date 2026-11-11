package com.ezu.product.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ezu.product.domain.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品品类 Mapper
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
