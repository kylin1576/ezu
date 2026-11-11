package com.ezu.product.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ezu.product.domain.entity.DeviceRepair;
import org.apache.ibatis.annotations.Mapper;

/**
 * 设备维修记录 Mapper
 */
@Mapper
public interface DeviceRepairMapper extends BaseMapper<DeviceRepair> {
}
