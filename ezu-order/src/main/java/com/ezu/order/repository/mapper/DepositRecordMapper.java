package com.ezu.order.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ezu.order.domain.entity.DepositRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 押金流水 Mapper
 */
@Mapper
public interface DepositRecordMapper extends BaseMapper<DepositRecord> {
}
