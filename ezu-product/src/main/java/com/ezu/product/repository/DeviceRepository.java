package com.ezu.product.repository;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.ezu.product.domain.entity.Device;
import com.ezu.product.domain.enums.DeviceStatus;
import com.ezu.product.repository.mapper.DeviceMapper;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设备SN台账数据访问（业务接口在 T6 实现）
 */
@Repository
public class DeviceRepository extends ServiceImpl<DeviceMapper, Device> {

    /**
     * 按SKU批量统计在库设备数（status=1，单条 GROUP BY 查询）
     */
    public Map<Long, Integer> countInStockBySkuIds(Collection<Long> skuIds) {
        if (CollUtil.isEmpty(skuIds)) {
            return new HashMap<>();
        }
        QueryWrapper<Device> wrapper = new QueryWrapper<>();
        wrapper.select("sku_id", "COUNT(*) AS cnt")
                .in("sku_id", skuIds)
                .eq("status", DeviceStatus.IN_STOCK)
                .groupBy("sku_id");
        List<Map<String, Object>> rows = this.listMaps(wrapper);
        Map<Long, Integer> result = new HashMap<>();
        for (Map<String, Object> row : rows) {
            result.put(((Number) row.get("sku_id")).longValue(), ((Number) row.get("cnt")).intValue());
        }
        return result;
    }
}
