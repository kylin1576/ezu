package com.ezu.product.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ezu.product.domain.entity.ProductParam;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品参数 Mapper
 */
@Mapper
public interface ProductParamMapper extends BaseMapper<ProductParam> {
}
