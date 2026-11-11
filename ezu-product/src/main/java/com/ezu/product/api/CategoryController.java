package com.ezu.product.api;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ezu.common.response.ResultData;
import com.ezu.product.application.CategoryApplicationService;
import com.ezu.product.domain.req.CategorySaveReq;
import com.ezu.product.domain.vo.CategoryTreeVo;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 后台品类管理
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/product/category")
public class CategoryController {

    private final CategoryApplicationService categoryApplicationService;

    /**
     * 品类树（全量两级）
     */
    @PostMapping("/list")
    public ResultData<List<CategoryTreeVo>> list() {
        return ResultData.success(categoryApplicationService.tree());
    }

    /**
     * 新增/编辑品类
     */
    @SaCheckPermission(value = "category:save", type = "admin")
    @PostMapping("/save")
    public ResultData<Long> save(@Validated @RequestBody CategorySaveReq req) {
        return ResultData.success(categoryApplicationService.save(req));
    }

    /**
     * 删除品类（有子品类或挂有商品时拦截）
     */
    @SaCheckPermission(value = "category:save", type = "admin")
    @PostMapping("/delete/{id}")
    public ResultData<Void> delete(@PathVariable Long id) {
        categoryApplicationService.delete(id);
        return ResultData.success(null);
    }
}
