package com.ezu.product.domainService;

import com.ezu.product.domain.req.CategorySaveReq;
import com.ezu.product.domain.vo.CategoryTreeVo;

import java.util.List;

/**
 * 商品品类领域服务
 */
public interface CategoryDomainService {

    /**
     * 全量品类两级树（sort 升序）
     */
    List<CategoryTreeVo> tree();

    /**
     * 保存品类（id为空=新增，非空=编辑；parentId≠0 时校验父级存在且为顶级）
     */
    Long save(CategorySaveReq req);

    /**
     * 删除品类（有子品类或挂有商品时拦截）
     */
    void delete(Long id);
}
