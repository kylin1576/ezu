package com.ezu.product.domainService.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ezu.common.exception.BusinessException;
import com.ezu.common.page.PageVo;
import com.ezu.common.util.NoGenerator;
import com.ezu.product.domain.entity.Device;
import com.ezu.product.domain.entity.DeviceCheckReport;
import com.ezu.product.domain.entity.DeviceRepair;
import com.ezu.product.domain.entity.Product;
import com.ezu.product.domain.entity.ProductSku;
import com.ezu.product.domain.enums.DeviceStatus;
import com.ezu.product.domain.req.DeviceCheckSaveReq;
import com.ezu.product.domain.req.DeviceInboundReq;
import com.ezu.product.domain.req.DeviceQueryReq;
import com.ezu.product.domain.req.DeviceRepairSaveReq;
import com.ezu.product.domain.vo.DeviceDetailVo;
import com.ezu.product.domain.vo.DeviceListVo;
import com.ezu.product.domainService.DeviceDomainService;
import com.ezu.product.repository.DeviceCheckReportRepository;
import com.ezu.product.repository.DeviceRepairRepository;
import com.ezu.product.repository.DeviceRepository;
import com.ezu.product.repository.ProductRepository;
import com.ezu.product.repository.ProductSkuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 设备SN台账领域服务实现
 */
@Service
@RequiredArgsConstructor
public class DeviceDomainServiceImpl implements DeviceDomainService {

    /** 设备资产编号前缀 */
    private static final String NO_PREFIX_DEVICE = "Z";

    /** 报告类型：入库质检 */
    private static final int CHECK_TYPE_INBOUND = 1;

    /** 锁定候选数量：一次取 5 台逐台 CAS，覆盖常见并发度后快速失败（单遍 bounded） */
    private static final int LOCK_CANDIDATES = 5;

    private final DeviceRepository deviceRepository;
    private final DeviceCheckReportRepository deviceCheckReportRepository;
    private final DeviceRepairRepository deviceRepairRepository;
    private final ProductSkuRepository productSkuRepository;
    private final ProductRepository productRepository;
    private final NoGenerator noGenerator;

    @Override
    public PageVo<DeviceListVo> queryPage(DeviceQueryReq req) {
        IPage<Device> page = deviceRepository.queryPage(req);
        List<Device> records = page.getRecords();
        if (records.isEmpty()) {
            return new PageVo<>(page.getTotal(), new ArrayList<>());
        }
        // 第一轮批量：SKU 回显（SPU 编辑为全删全插，旧 SKU 已逻辑删时 listByIds 查不到 → 回显空属正常）
        Set<Long> skuIds = records.stream().map(Device::getSkuId).collect(Collectors.toSet());
        Map<Long, ProductSku> skuMap = productSkuRepository.listByIds(skuIds).stream()
                .collect(Collectors.toMap(ProductSku::getId, Function.identity()));
        // 第二轮批量：商品名回显
        Set<Long> productIds = skuMap.values().stream().map(ProductSku::getProductId).collect(Collectors.toSet());
        Map<Long, Product> productMap = CollUtil.isEmpty(productIds) ? new HashMap<>()
                : productRepository.listByIds(productIds).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));
        List<DeviceListVo> list = records.stream().map(device -> {
            DeviceListVo vo = new DeviceListVo();
            BeanUtil.copyProperties(device, vo);
            ProductSku sku = skuMap.get(device.getSkuId());
            if (sku != null) {
                vo.setSpecName(sku.getSpecName());
                Product product = productMap.get(sku.getProductId());
                if (product != null) {
                    vo.setProductName(product.getName());
                }
            }
            return vo;
        }).collect(Collectors.toList());
        // pageSize=-1 全量时 MP 跳过 count 查询，total 以实际条数回填
        long total = req.getPageSize() == -1 ? list.size() : page.getTotal();
        return new PageVo<>(total, list);
    }

    @Override
    public List<String> inbound(DeviceInboundReq req) {
        // 校验 SKU 存在（getById 过滤逻辑删；不校验启用，允许给停用 SKU 补录设备）
        ProductSku sku = productSkuRepository.getById(req.getSkuId());
        if (sku == null) {
            throw new BusinessException("SKU【{}】不存在", req.getSkuId());
        }
        List<Device> devices = req.getItems().stream().map(item -> {
            Device device = new Device();
            BeanUtil.copyProperties(item, device);
            device.setSkuId(req.getSkuId());
            device.setDeviceNo(noGenerator.next(NO_PREFIX_DEVICE));
            device.setStatus(DeviceStatus.IN_STOCK);
            return device;
        }).collect(Collectors.toList());
        deviceRepository.saveBatch(devices);
        return devices.stream().map(Device::getDeviceNo).collect(Collectors.toList());
    }

    @Override
    public Long saveCheckReport(DeviceCheckSaveReq req) {
        Device device = requireDevice(req.getDeviceId());
        DeviceCheckReport report = new DeviceCheckReport();
        BeanUtil.copyProperties(req, report);
        // 照片 List → JSON 字符串落库
        report.setAttachments(req.getAttachments() == null ? null : JSONUtil.toJsonStr(req.getAttachments()));
        deviceCheckReportRepository.save(report);
        // 入库质检定级 → 回写设备成色
        if (Objects.equals(req.getType(), CHECK_TYPE_INBOUND) && req.getConditionLevel() != null) {
            deviceRepository.lambdaUpdate()
                    .eq(Device::getId, device.getId())
                    .set(Device::getConditionLevel, req.getConditionLevel())
                    .update();
        }
        return report.getId();
    }

    @Override
    public Long saveRepair(DeviceRepairSaveReq req) {
        Device device = requireDevice(req.getDeviceId());
        if (req.getEndDate() == null) {
            // 开始维修：在租/在库 → 维修
            checkStatus(device, DeviceStatus.RENTED, DeviceStatus.IN_STOCK);
            transitionStatus(device, DeviceStatus.REPAIRING);
        } else {
            // 维修完成（含一次性登记已完成的维修）→ 回在库
            checkStatus(device, DeviceStatus.REPAIRING, DeviceStatus.RENTED, DeviceStatus.IN_STOCK);
            transitionStatus(device, DeviceStatus.IN_STOCK);
        }
        DeviceRepair repair = new DeviceRepair();
        BeanUtil.copyProperties(req, repair);
        deviceRepairRepository.save(repair);
        return repair.getId();
    }

    @Override
    public void scrapped(Long deviceId) {
        Device device = requireDevice(deviceId);
        // 仅在库/维修可报废（在租灭失走订单定损流程）
        checkStatus(device, DeviceStatus.IN_STOCK, DeviceStatus.REPAIRING);
        transitionStatus(device, DeviceStatus.SCRAPPED);
    }

    @Override
    public DeviceDetailVo detail(Long id) {
        Device device = requireDevice(id);
        DeviceDetailVo vo = new DeviceDetailVo();
        BeanUtil.copyProperties(device, vo);
        // SKU 可能因 SPU 编辑（全删全插）被逻辑删，查不到回显空属正常
        ProductSku sku = productSkuRepository.getById(device.getSkuId());
        if (sku != null) {
            vo.setSpecName(sku.getSpecName());
            Product product = productRepository.getById(sku.getProductId());
            if (product != null) {
                vo.setProductName(product.getName());
            }
        }
        vo.setCheckReports(deviceCheckReportRepository.listByDeviceId(id).stream().map(report -> {
            DeviceDetailVo.CheckReportVo reportVo = new DeviceDetailVo.CheckReportVo();
            BeanUtil.copyProperties(report, reportVo);
            if (StrUtil.isNotBlank(report.getAttachments())) {
                reportVo.setAttachments(JSONUtil.toList(report.getAttachments(), String.class));
            }
            return reportVo;
        }).collect(Collectors.toList()));
        vo.setRepairs(deviceRepairRepository.listByDeviceId(id).stream().map(repair -> {
            DeviceDetailVo.RepairVo repairVo = new DeviceDetailVo.RepairVo();
            BeanUtil.copyProperties(repair, repairVo);
            return repairVo;
        }).collect(Collectors.toList()));
        return vo;
    }

    @Override
    public Long lockDevice(Long skuId) {
        // 单遍 bounded：候选只查一次（5台），逐台条件更新 1→2，任一成功即返回；
        // 禁止事务内重查候选重试——REPEATABLE READ 快照不刷新，重查结果不变会死循环
        List<Device> candidates = deviceRepository.selectInStockCandidates(skuId, LOCK_CANDIDATES);
        if (candidates.isEmpty()) {
            // 候选为空两种可能：真无库存 / 并发瞬间被锁完，按同一快照的在库计数分流文案
            Integer available = deviceRepository.countInStockBySkuIds(List.of(skuId)).get(skuId);
            if (available == null || available == 0) {
                throw new BusinessException("商品【{}】已无可租设备", resolveProductName(skuId));
            }
            throw new BusinessException("商品可租设备已被抢租，请重试");
        }
        for (Device candidate : candidates) {
            boolean locked = deviceRepository.lambdaUpdate()
                    .eq(Device::getId, candidate.getId())
                    .eq(Device::getStatus, DeviceStatus.IN_STOCK)
                    .set(Device::getStatus, DeviceStatus.LOCKED)
                    .update();
            if (locked) {
                return candidate.getId();
            }
        }
        throw new BusinessException("商品可租设备已被抢租，请重试");
    }

    @Override
    public void releaseDevice(Long deviceId) {
        transition(deviceId, DeviceStatus.LOCKED, DeviceStatus.IN_STOCK);
    }

    @Override
    public void rentDevice(Long deviceId) {
        transition(deviceId, DeviceStatus.LOCKED, DeviceStatus.RENTED);
    }

    @Override
    public void returnDevice(Long deviceId) {
        transition(deviceId, DeviceStatus.RENTED, DeviceStatus.RETURN_CHECKING);
    }

    @Override
    public void backToStock(Long deviceId) {
        transition(deviceId, DeviceStatus.RETURN_CHECKING, DeviceStatus.IN_STOCK);
    }

    @Override
    public Map<Long, Integer> countAvailable(List<Long> skuIds) {
        return deviceRepository.countInStockBySkuIds(skuIds);
    }

    /**
     * 无库存报错用的商品名称：SKU→SPU 名称回查；SKU/SPU 已删（SPU 编辑为全删全插）时降级为 SKU ID
     */
    private String resolveProductName(Long skuId) {
        ProductSku sku = productSkuRepository.getById(skuId);
        if (sku != null) {
            Product product = productRepository.getById(sku.getProductId());
            if (product != null) {
                return product.getName();
            }
        }
        return String.valueOf(skuId);
    }

    /**
     * 按设备ID查询，不存在抛业务异常
     */
    private Device requireDevice(Long deviceId) {
        Device device = deviceRepository.getById(deviceId);
        if (device == null) {
            throw new BusinessException("设备【{}】不存在", deviceId);
        }
        return device;
    }

    /**
     * 校验设备当前状态在期望集合内，否则抛「设备状态【{}】不允许该操作」
     */
    private void checkStatus(Device device, int... expectStatuses) {
        for (int expect : expectStatuses) {
            if (Objects.equals(device.getStatus(), expect)) {
                return;
            }
        }
        throw new BusinessException("设备状态【{}】不允许该操作", device.getStatus());
    }

    /**
     * 带前置状态的条件状态流转（WHERE id AND status=当前值，防并发误流转）
     */
    private void transitionStatus(Device device, int target) {
        deviceRepository.lambdaUpdate()
                .eq(Device::getId, device.getId())
                .eq(Device::getStatus, device.getStatus())
                .set(Device::getStatus, target)
                .update();
    }

    /**
     * 查设备→校验前置状态→条件流转（跨模块状态机方法共用）
     */
    private void transition(Long deviceId, int from, int to) {
        Device device = requireDevice(deviceId);
        checkStatus(device, from);
        transitionStatus(device, to);
    }
}
