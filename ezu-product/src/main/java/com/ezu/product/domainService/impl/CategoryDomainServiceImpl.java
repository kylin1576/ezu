package com.ezu.product.domainService.impl;

import cn.hutool.core.bean.BeanUtil;
import com.ezu.common.exception.BusinessException;
import com.ezu.product.domain.entity.Category;
import com.ezu.product.domain.enums.CategoryStatus;
import com.ezu.product.domain.req.CategorySaveReq;
import com.ezu.product.domain.vo.CategoryTreeVo;
import com.ezu.product.domainService.CategoryDomainService;
import com.ezu.product.repository.CategoryRepository;
import com.ezu.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 商品品类领域服务实现
 */
@Service
@RequiredArgsConstructor
public class CategoryDomainServiceImpl implements CategoryDomainService {

    /** 顶级品类 parent_id 值 */
    private static final long ROOT_PARENT_ID = 0L;

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    public List<CategoryTreeVo> tree() {
        List<CategoryTreeVo> vos = categoryRepository.lambdaQuery()
                .orderByAsc(Category::getSort)
                .orderByAsc(Category::getId)
                .list().stream()
                .map(category -> {
                    CategoryTreeVo vo = new CategoryTreeVo();
                    BeanUtil.copyProperties(category, vo);
                    return vo;
                })
                .collect(Collectors.toList());
        Map<Long, CategoryTreeVo> idMap = vos.stream()
                .collect(Collectors.toMap(CategoryTreeVo::getId, Function.identity()));
        List<CategoryTreeVo> roots = new ArrayList<>();
        for (CategoryTreeVo vo : vos) {
            if (vo.getParentId() == ROOT_PARENT_ID) {
                roots.add(vo);
                continue;
            }
            CategoryTreeVo parent = idMap.get(vo.getParentId());
            if (parent != null) {
                parent.getChildren().add(vo);
            } else {
                // 父级缺失（脏数据）：挂到根，避免节点丢失
                roots.add(vo);
            }
        }
        return roots;
    }

    @Override
    public List<CategoryTreeVo> enabledTree() {
        List<CategoryTreeVo> vos = categoryRepository.lambdaQuery()
                .eq(Category::getStatus, CategoryStatus.ENABLED)
                .orderByAsc(Category::getSort)
                .orderByAsc(Category::getId)
                .list().stream()
                .map(category -> {
                    CategoryTreeVo vo = new CategoryTreeVo();
                    BeanUtil.copyProperties(category, vo);
                    return vo;
                })
                .collect(Collectors.toList());
        Map<Long, CategoryTreeVo> idMap = vos.stream()
                .collect(Collectors.toMap(CategoryTreeVo::getId, Function.identity()));
        List<CategoryTreeVo> roots = new ArrayList<>();
        for (CategoryTreeVo vo : vos) {
            if (vo.getParentId() == ROOT_PARENT_ID) {
                roots.add(vo);
                continue;
            }
            // 二级品类：父级不在启用集（被禁用）时随父级整体不出现，不提升到根
            CategoryTreeVo parent = idMap.get(vo.getParentId());
            if (parent != null) {
                parent.getChildren().add(vo);
            }
        }
        return roots;
    }

    @Override
    public Long save(CategorySaveReq req) {
        if (req.getId() != null) {
            Category exist = categoryRepository.getById(req.getId());
            if (exist == null) {
                throw new BusinessException("品类【{}】不存在", req.getId());
            }
        }
        if (req.getParentId() != ROOT_PARENT_ID) {
            checkParent(req);
        }
        Category category = new Category();
        BeanUtil.copyProperties(req, category);
        if (req.getId() == null) {
            categoryRepository.save(category);
        } else {
            categoryRepository.updateById(category);
        }
        return category.getId();
    }

    @Override
    public void delete(Long id) {
        Category category = categoryRepository.getById(id);
        if (category == null) {
            throw new BusinessException("品类【{}】不存在", id);
        }
        if (categoryRepository.countByParentId(id) > 0) {
            throw new BusinessException("品类【{}】下存在子品类，无法删除", category.getName());
        }
        if (productRepository.countByCategoryId(id) > 0) {
            throw new BusinessException("品类【{}】下存在商品，无法删除", category.getName());
        }
        categoryRepository.removeById(id);
    }

    /**
     * 二级品类父级校验：父级存在且为顶级（只允许两级，同时防自挂/降级成三级）
     */
    private void checkParent(CategorySaveReq req) {
        if (Objects.equals(req.getId(), req.getParentId())) {
            throw new BusinessException("父级品类不能是品类自己");
        }
        Category parent = categoryRepository.getById(req.getParentId());
        if (parent == null) {
            throw new BusinessException("父级品类【{}】不存在", req.getParentId());
        }
        if (parent.getParentId() != ROOT_PARENT_ID) {
            throw new BusinessException("只支持两级品类，父级【{}】不是顶级品类", parent.getName());
        }
        if (req.getId() != null && categoryRepository.countByParentId(req.getId()) > 0) {
            throw new BusinessException("品类【{}】下存在子品类，不能挂到其他品类下", req.getId());
        }
    }
}
