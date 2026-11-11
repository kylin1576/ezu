package com.ezu.order.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ezu.order.domain.entity.Logistics;
import org.apache.ibatis.annotations.Mapper;

/**
 * 物流记录 Mapper
 */
@Mapper
public interface LogisticsMapper extends BaseMapper<Logistics> {
}
