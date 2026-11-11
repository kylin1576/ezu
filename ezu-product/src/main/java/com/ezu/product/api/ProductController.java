package com.ezu.product.api;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ezu.common.page.PageVo;
import com.ezu.common.response.BatchOperateResultVo;
import com.ezu.common.response.ResultData;
import com.ezu.product.application.ProductApplicationService;
import com.ezu.product.domain.req.ShelfReq;
import com.ezu.product.domain.req.SpuQueryReq;
import com.ezu.product.domain.req.SpuSaveReq;
import com.ezu.product.domain.vo.SpuDetailVo;
import com.ezu.product.domain.vo.SpuListVo;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台商品SPU管理（SPU+SKU+租金方案+参数整体维护）
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/product/spu")
public class ProductController {

    private final ProductApplicationService productApplicationService;

    /**
     * 商品列表
     */
    @PostMapping("/list")
    public ResultData<PageVo<SpuListVo>> list(@Validated @RequestBody SpuQueryReq req) {
        return ResultData.success(productApplicationService.list(req));
    }

    /**
     * 新增/编辑商品（整体保存，返回商品编号）
     */
    @SaCheckPermission(value = "spu:save", type = "admin")
    @PostMapping("/save")
    public ResultData<String> save(@Validated @RequestBody SpuSaveReq req) {
        return ResultData.success(productApplicationService.save(req));
    }

    /**
     * 商品详情
     */
    @GetMapping("/detail/{id}")
    public ResultData<SpuDetailVo> detail(@PathVariable Long id) {
        return ResultData.success(productApplicationService.detail(id));
    }

    /**
     * 商品批量上下架
     */
    @SaCheckPermission(value = "spu:shelf", type = "admin")
    @PostMapping("/shelf")
    public ResultData<BatchOperateResultVo> shelf(@Validated @RequestBody ShelfReq req) {
        return ResultData.success(productApplicationService.shelf(req));
    }
}
