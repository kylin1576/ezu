package com.ezu.product.repository;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.ezu.product.domain.entity.Device;
import com.ezu.product.repository.mapper.DeviceMapper;
import org.springframework.stereotype.Repository;

/**
 * 设备SN台账数据访问（业务接口在 T6 实现）
 */
@Repository
public class DeviceRepository extends ServiceImpl<DeviceMapper, Device> {
}
