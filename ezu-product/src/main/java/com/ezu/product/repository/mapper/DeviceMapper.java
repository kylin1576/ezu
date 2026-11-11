package com.ezu.product.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ezu.product.domain.entity.Device;
import org.apache.ibatis.annotations.Mapper;

/**
 * 设备SN台账 Mapper
 */
@Mapper
public interface DeviceMapper extends BaseMapper<Device> {
}
