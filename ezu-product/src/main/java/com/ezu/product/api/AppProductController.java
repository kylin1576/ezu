package com.ezu.product.api;

import com.ezu.common.page.PageVo;
import com.ezu.common.response.ResultData;
import com.ezu.product.application.ProductApplicationService;
import com.ezu.product.domain.req.AppProductQueryReq;
import com.ezu.product.domain.vo.AppProductDetailVo;
import com.ezu.product.domain.vo.AppProductListVo;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * C端商品浏览
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/app/product")
public class AppProductController {

    private final ProductApplicationService productApplicationService;

    /**
     * 商品分页列表（仅上架）
     */
    @PostMapping("/list")
    public ResultData<PageVo<AppProductListVo>> list(@Validated @RequestBody AppProductQueryReq req) {
        return ResultData.success(productApplicationService.appList(req));
    }

    /**
     * 商品详情（仅上架；非上架抛「商品不存在或已下架」）
     */
    @GetMapping("/detail/{id}")
    public ResultData<AppProductDetailVo> detail(@PathVariable Long id) {
        return ResultData.success(productApplicationService.appDetail(id));
    }
}
