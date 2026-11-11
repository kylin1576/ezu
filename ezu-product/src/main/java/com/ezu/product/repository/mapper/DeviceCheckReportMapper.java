package com.ezu.product.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ezu.product.domain.entity.DeviceCheckReport;
import org.apache.ibatis.annotations.Mapper;

/**
 * 设备质检/验收报告 Mapper
 */
@Mapper
public interface DeviceCheckReportMapper extends BaseMapper<DeviceCheckReport> {
}
