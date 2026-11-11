package com.ezu.product.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ezu.product.domain.entity.ProductSku;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品SKU Mapper
 */
@Mapper
public interface ProductSkuMapper extends BaseMapper<ProductSku> {
}
