package com.ezu.product.api;

import com.ezu.common.response.ResultData;
import com.ezu.product.application.CategoryApplicationService;
import com.ezu.product.domain.vo.CategoryTreeVo;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * C端品类浏览
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/app/product/category")
public class AppCategoryController {

    private final CategoryApplicationService categoryApplicationService;

    /**
     * 品类树（仅启用品类，禁用及其子品类不出现）
     */
    @GetMapping("/list")
    public ResultData<List<CategoryTreeVo>> list() {
        return ResultData.success(categoryApplicationService.enabledTree());
    }
}
