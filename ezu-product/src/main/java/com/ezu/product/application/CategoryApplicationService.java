package com.ezu.product.application;

import com.ezu.product.domain.req.CategorySaveReq;
import com.ezu.product.domain.vo.CategoryTreeVo;
import com.ezu.product.domainService.CategoryDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 商品品类应用服务
 */
@Service
@RequiredArgsConstructor
public class CategoryApplicationService {

    private final CategoryDomainService categoryDomainService;

    /**
     * 全量品类两级树
     */
    public List<CategoryTreeVo> tree() {
        return categoryDomainService.tree();
    }

    /**
     * 启用品类两级树（C端；禁用品类及其子品类不出现）
     */
    public List<CategoryTreeVo> enabledTree() {
        return categoryDomainService.enabledTree();
    }

    /**
     * 新增/编辑品类
     */
    @Transactional(rollbackFor = Exception.class)
    public Long save(CategorySaveReq req) {
        return categoryDomainService.save(req);
    }

    /**
     * 删除品类
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        categoryDomainService.delete(id);
    }
}
