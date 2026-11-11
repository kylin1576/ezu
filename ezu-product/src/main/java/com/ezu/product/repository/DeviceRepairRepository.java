package com.ezu.product.repository;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.ezu.product.domain.entity.DeviceRepair;
import com.ezu.product.repository.mapper.DeviceRepairMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 设备维修记录数据访问
 */
@Repository
public class DeviceRepairRepository extends ServiceImpl<DeviceRepairMapper, DeviceRepair> {

    /**
     * 按设备查询维修记录（新在前）
     */
    public List<DeviceRepair> listByDeviceId(Long deviceId) {
        return this.lambdaQuery()
                .eq(DeviceRepair::getDeviceId, deviceId)
                .orderByDesc(DeviceRepair::getCreateTime)
                .orderByDesc(DeviceRepair::getId)
                .list();
    }
}
