package com.ezu.product.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ezu.product.domain.entity.Product;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品SPU Mapper
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}
