package com.ezu.product.repository;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.ezu.product.domain.entity.DeviceRepair;
import com.ezu.product.repository.mapper.DeviceRepairMapper;
import org.springframework.stereotype.Repository;

/**
 * 设备维修记录数据访问（业务接口在 T6 实现）
 */
@Repository
public class DeviceRepairRepository extends ServiceImpl<DeviceRepairMapper, DeviceRepair> {
}
