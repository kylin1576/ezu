package com.ezu.startup.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.ezu.common.response.ResultData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查
 */
@SaIgnore
@RestController
public class HealthController {

    @GetMapping("/_health")
    public ResultData<String> health() {
        return ResultData.success("ok");
    }
}
