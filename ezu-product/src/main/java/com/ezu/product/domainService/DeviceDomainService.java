package com.ezu.product.domainService;

import com.ezu.common.page.PageVo;
import com.ezu.product.domain.req.DeviceCheckSaveReq;
import com.ezu.product.domain.req.DeviceInboundReq;
import com.ezu.product.domain.req.DeviceQueryReq;
import com.ezu.product.domain.req.DeviceRepairSaveReq;
import com.ezu.product.domain.vo.DeviceDetailVo;
import com.ezu.product.domain.vo.DeviceListVo;

import java.util.List;
import java.util.Map;

/**
 * 设备SN台账领域服务（台账维护 + 设备状态机）
 */
public interface DeviceDomainService {

    /**
     * 分页查询（SKU规格名/商品名两轮批量回显；keyword模糊匹配SN/设备编号）
     */
    PageVo<DeviceListVo> queryPage(DeviceQueryReq req);

    /**
     * 批量入库（校验SKU存在；逐台生成Z编号、置在库；返回设备编号列表）
     */
    List<String> inbound(DeviceInboundReq req);

    /**
     * 保存质检/验收报告（type=1入库质检且定级非空时回写t_device.condition_level，同事务）
     */
    Long saveCheckReport(DeviceCheckSaveReq req);

    /**
     * 保存维修记录（endDate空=开始维修，在租/在库→维修；endDate非空=维修完成，→在库）
     */
    Long saveRepair(DeviceRepairSaveReq req);

    /**
     * 报废（仅1在库/4维修可报废→5终态）
     */
    void scrapped(Long deviceId);

    /**
     * 详情（主表+SKU/商品回显+质检报告列表+维修记录列表）
     */
    DeviceDetailVo detail(Long id);

    // ==================== 设备状态机方法（ezu-order 下单/履约跨模块调用） ====================

    /**
     * CAS锁定一台在库设备（选该SKU下id最小的一台，条件更新1→2；单次尝试，
     * 并发抢失败或无在库设备返回null，循环重试由调用方负责）
     */
    Long lockDevice(Long skuId);

    /**
     * 释放锁定（2锁定→1在库，订单取消时调用）
     */
    void releaseDevice(Long deviceId);

    /**
     * 确认租出（2锁定→3在租，订单生效时调用）
     */
    void rentDevice(Long deviceId);

    /**
     * 用户还回（3在租→6归还验收中）
     */
    void returnDevice(Long deviceId);

    /**
     * 验收通过入库（6归还验收中→1在库）
     */
    void backToStock(Long deviceId);

    /**
     * 各SKU在库（可租）设备数
     */
    Map<Long, Integer> countAvailable(List<Long> skuIds);
}
