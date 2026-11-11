package com.ezu.product.repository;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.ezu.product.domain.entity.DeviceCheckReport;
import com.ezu.product.repository.mapper.DeviceCheckReportMapper;
import org.springframework.stereotype.Repository;

/**
 * 设备质检/验收报告数据访问（业务接口在 T6 实现）
 */
@Repository
public class DeviceCheckReportRepository extends ServiceImpl<DeviceCheckReportMapper, DeviceCheckReport> {
}
