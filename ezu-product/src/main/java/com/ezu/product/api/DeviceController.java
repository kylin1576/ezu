package com.ezu.product.api;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ezu.common.page.PageVo;
import com.ezu.common.response.ResultData;
import com.ezu.product.application.DeviceApplicationService;
import com.ezu.product.domain.req.DeviceCheckSaveReq;
import com.ezu.product.domain.req.DeviceInboundReq;
import com.ezu.product.domain.req.DeviceQueryReq;
import com.ezu.product.domain.req.DeviceRepairSaveReq;
import com.ezu.product.domain.vo.DeviceDetailVo;
import com.ezu.product.domain.vo.DeviceListVo;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 后台设备SN台账管理（入库/质检/维修/报废）
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/device")
public class DeviceController {

    private final DeviceApplicationService deviceApplicationService;

    /**
     * 设备台账列表
     */
    @PostMapping("/list")
    public ResultData<PageVo<DeviceListVo>> list(@Validated @RequestBody DeviceQueryReq req) {
        return ResultData.success(deviceApplicationService.list(req));
    }

    /**
     * 采购入库（返回设备编号列表）
     */
    @SaCheckPermission(value = "device:inbound", type = "admin")
    @PostMapping("/inbound")
    public ResultData<List<String>> inbound(@Validated @RequestBody DeviceInboundReq req) {
        return ResultData.success(deviceApplicationService.inbound(req));
    }

    /**
     * 保存质检/验收报告（返回报告ID）
     */
    @SaCheckPermission(value = "device:check", type = "admin")
    @PostMapping("/check/save")
    public ResultData<Long> checkSave(@Validated @RequestBody DeviceCheckSaveReq req) {
        return ResultData.success(deviceApplicationService.checkSave(req));
    }

    /**
     * 保存维修记录（返回维修记录ID）
     */
    @SaCheckPermission(value = "device:repair", type = "admin")
    @PostMapping("/repair/save")
    public ResultData<Long> repairSave(@Validated @RequestBody DeviceRepairSaveReq req) {
        return ResultData.success(deviceApplicationService.repairSave(req));
    }

    /**
     * 设备报废（仅1在库/4维修可报废，终态）
     */
    @SaCheckPermission(value = "device:scrapped", type = "admin")
    @PostMapping("/scrapped/{deviceId}")
    public ResultData<Void> scrapped(@PathVariable Long deviceId) {
        deviceApplicationService.scrapped(deviceId);
        return ResultData.success(null);
    }

    /**
     * 设备详情（含质检报告/维修记录）
     */
    @GetMapping("/detail/{id}")
    public ResultData<DeviceDetailVo> detail(@PathVariable Long id) {
        return ResultData.success(deviceApplicationService.detail(id));
    }
}
