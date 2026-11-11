package com.ezu.user.api;

import com.ezu.common.page.PageQuery;
import com.ezu.common.page.PageVo;
import com.ezu.common.response.ResultData;
import com.ezu.common.satoken.StpUserUtil;
import com.ezu.user.application.UserAddressApplicationService;
import com.ezu.user.application.UserApplicationService;
import com.ezu.user.domain.req.AddressSaveReq;
import com.ezu.user.domain.vo.AddressVo;
import com.ezu.user.domain.vo.UserVo;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * C端用户中心
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/app/user")
public class AppUserController {

    private final UserApplicationService userApplicationService;
    private final UserAddressApplicationService userAddressApplicationService;

    /**
     * 当前登录用户信息
     */
    @GetMapping("/info")
    public ResultData<UserVo> info() {
        return ResultData.success(userApplicationService.info(StpUserUtil.getUserId()));
    }

    /**
     * 本人地址列表
     */
    @PostMapping("/address/list")
    public ResultData<PageVo<AddressVo>> addressList(@Validated @RequestBody PageQuery pageQuery) {
        return ResultData.success(userAddressApplicationService.list(StpUserUtil.getUserId(), pageQuery));
    }

    /**
     * 新增/编辑地址
     */
    @PostMapping("/address/save")
    public ResultData<Long> addressSave(@Validated @RequestBody AddressSaveReq req) {
        return ResultData.success(userAddressApplicationService.save(StpUserUtil.getUserId(), req));
    }

    /**
     * 删除地址
     */
    @PostMapping("/address/delete/{id}")
    public ResultData<Void> addressDelete(@PathVariable("id") Long id) {
        userAddressApplicationService.delete(StpUserUtil.getUserId(), id);
        return ResultData.success(null);
    }
}
