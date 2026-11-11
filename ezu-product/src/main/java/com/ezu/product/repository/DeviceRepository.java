package com.ezu.product.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.ezu.product.domain.entity.Device;
import com.ezu.product.domain.enums.DeviceStatus;
import com.ezu.product.domain.req.DeviceQueryReq;
import com.ezu.product.repository.mapper.DeviceMapper;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设备SN台账数据访问
 */
@Repository
public class DeviceRepository extends ServiceImpl<DeviceMapper, Device> {

    /**
     * 分页查询（skuId/status 精确，keyword 模糊匹配 sn/设备编号；pageSize=-1 查全量）
     */
    public IPage<Device> queryPage(DeviceQueryReq req) {
        Page<Device> page = new Page<>(req.getPageNum(), req.getPageSize());
        return this.lambdaQuery()
                .eq(req.getSkuId() != null, Device::getSkuId, req.getSkuId())
                .eq(req.getStatus() != null, Device::getStatus, req.getStatus())
                .and(StrUtil.isNotBlank(req.getKeyword()), w -> w
                        .like(Device::getSn, req.getKeyword())
                        .or()
                        .like(Device::getDeviceNo, req.getKeyword()))
                .orderByDesc(Device::getCreateTime)
                .orderByDesc(Device::getId)
                .page(page);
    }

    /**
     * 取指定SKU下 id 最小的一台在库设备（锁定预占候选；单台）
     */
    public Device selectFirstInStock(Long skuId) {
        return this.lambdaQuery()
                .eq(Device::getSkuId, skuId)
                .eq(Device::getStatus, DeviceStatus.IN_STOCK)
                .orderByAsc(Device::getId)
                .last("LIMIT 1")
                .one();
    }

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
