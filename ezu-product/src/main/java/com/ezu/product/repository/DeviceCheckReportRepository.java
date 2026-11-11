package com.ezu.product.repository;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.ezu.product.domain.entity.DeviceCheckReport;
import com.ezu.product.repository.mapper.DeviceCheckReportMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 设备质检/验收报告数据访问
 */
@Repository
public class DeviceCheckReportRepository extends ServiceImpl<DeviceCheckReportMapper, DeviceCheckReport> {

    /**
     * 按设备查询报告（新在前）
     */
    public List<DeviceCheckReport> listByDeviceId(Long deviceId) {
        return this.lambdaQuery()
                .eq(DeviceCheckReport::getDeviceId, deviceId)
                .orderByDesc(DeviceCheckReport::getCreateTime)
                .orderByDesc(DeviceCheckReport::getId)
                .list();
    }
}
