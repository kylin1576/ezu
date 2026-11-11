package com.ezu.order.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ezu.order.domain.entity.Bill;
import org.apache.ibatis.annotations.Mapper;

/**
 * 账单 Mapper
 */
@Mapper
public interface BillMapper extends BaseMapper<Bill> {
}
