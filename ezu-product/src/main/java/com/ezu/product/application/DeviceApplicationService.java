package com.ezu.product.application;

import com.ezu.common.page.PageVo;
import com.ezu.product.domain.req.DeviceCheckSaveReq;
import com.ezu.product.domain.req.DeviceInboundReq;
import com.ezu.product.domain.req.DeviceQueryReq;
import com.ezu.product.domain.req.DeviceRepairSaveReq;
import com.ezu.product.domain.vo.DeviceDetailVo;
import com.ezu.product.domain.vo.DeviceListVo;
import com.ezu.product.domainService.DeviceDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 设备SN台账应用服务
 */
@Service
@RequiredArgsConstructor
public class DeviceApplicationService {

    private final DeviceDomainService deviceDomainService;

    /**
     * 设备台账列表
     */
    public PageVo<DeviceListVo> list(DeviceQueryReq req) {
        return deviceDomainService.queryPage(req);
    }

    /**
     * 采购入库（逐台生成设备编号）
     */
    @Transactional(rollbackFor = Exception.class)
    public List<String> inbound(DeviceInboundReq req) {
        return deviceDomainService.inbound(req);
    }

    /**
     * 保存质检/验收报告（入库质检定级回写设备成色，同事务）
     */
    @Transactional(rollbackFor = Exception.class)
    public Long checkSave(DeviceCheckSaveReq req) {
        return deviceDomainService.saveCheckReport(req);
    }

    /**
     * 保存维修记录（开始维修置维修态/完成维修回在库，同事务）
     */
    @Transactional(rollbackFor = Exception.class)
    public Long repairSave(DeviceRepairSaveReq req) {
        return deviceDomainService.saveRepair(req);
    }

    /**
     * 设备报废（终态）
     */
    @Transactional(rollbackFor = Exception.class)
    public void scrapped(Long deviceId) {
        deviceDomainService.scrapped(deviceId);
    }

    /**
     * 设备详情（含质检报告/维修记录）
     */
    public DeviceDetailVo detail(Long id) {
        return deviceDomainService.detail(id);
    }
}
