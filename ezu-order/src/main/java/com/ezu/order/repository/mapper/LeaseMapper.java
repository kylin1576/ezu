package com.ezu.order.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ezu.order.domain.entity.Lease;
import org.apache.ibatis.annotations.Mapper;

/**
 * 租约 Mapper
 */
@Mapper
public interface LeaseMapper extends BaseMapper<Lease> {
}
