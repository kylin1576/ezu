package com.ezu.product.domainService.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ezu.common.exception.BusinessException;
import com.ezu.common.page.PageVo;
import com.ezu.common.response.BatchOperateResultVo;
import com.ezu.common.util.NoGenerator;
import com.ezu.product.domain.entity.Category;
import com.ezu.product.domain.entity.Product;
import com.ezu.product.domain.entity.ProductParam;
import com.ezu.product.domain.entity.ProductSku;
import com.ezu.product.domain.entity.RentalPlan;
import com.ezu.product.domain.enums.ProductStatus;
import com.ezu.product.domain.enums.SkuStatus;
import com.ezu.product.domain.req.ShelfReq;
import com.ezu.product.domain.req.SpuQueryReq;
import com.ezu.product.domain.req.SpuSaveReq;
import com.ezu.product.domain.vo.SpuDetailVo;
import com.ezu.product.domain.vo.SpuListVo;
import com.ezu.product.domainService.ProductDomainService;
import com.ezu.product.repository.CategoryRepository;
import com.ezu.product.repository.ProductParamRepository;
import com.ezu.product.repository.ProductRepository;
import com.ezu.product.repository.ProductSkuRepository;
import com.ezu.product.repository.RentalPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 商品SPU领域服务实现
 */
@Service
@RequiredArgsConstructor
public class ProductDomainServiceImpl implements ProductDomainService {

    /** 商品编号前缀 */
    private static final String NO_PREFIX_PRODUCT = "G";

    /** SKU编号前缀 */
    private static final String NO_PREFIX_SKU = "S";

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductSkuRepository productSkuRepository;
    private final RentalPlanRepository rentalPlanRepository;
    private final ProductParamRepository productParamRepository;
    private final NoGenerator noGenerator;

    @Override
    public PageVo<SpuListVo> queryPage(SpuQueryReq req) {
        IPage<Product> page = productRepository.queryPage(req);
        List<Product> records = page.getRecords();
        if (records.isEmpty()) {
            return new PageVo<>(page.getTotal(), new ArrayList<>());
        }
        // 第一轮批量：品类名回显
        Set<Long> categoryIds = records.stream().map(Product::getCategoryId).collect(Collectors.toSet());
        Map<Long, String> categoryNameMap = categoryRepository.listByIds(categoryIds).stream()
                .collect(Collectors.toMap(Category::getId, Category::getName));
        // 第二轮批量：SKU 数 + 各 SKU 启用方案 → 最低租金起（无循环查库）
        List<Long> productIds = records.stream().map(Product::getId).collect(Collectors.toList());
        Map<Long, List<ProductSku>> skuMap = productSkuRepository.listByProductIds(productIds).stream()
                .collect(Collectors.groupingBy(ProductSku::getProductId));
        List<Long> skuIds = skuMap.values().stream()
                .flatMap(List::stream)
                .map(ProductSku::getId)
                .collect(Collectors.toList());
        Map<Long, List<RentalPlan>> planMap = rentalPlanRepository.listBySkuIds(skuIds).stream()
                .filter(plan -> plan.getStatus() != null && plan.getStatus() == SkuStatus.ENABLED)
                .collect(Collectors.groupingBy(RentalPlan::getSkuId));
        List<SpuListVo> list = records.stream().map(product -> {
            SpuListVo vo = new SpuListVo();
            BeanUtil.copyProperties(product, vo);
            vo.setCategoryName(categoryNameMap.get(product.getCategoryId()));
            List<ProductSku> skus = skuMap.getOrDefault(product.getId(), Collections.emptyList());
            vo.setSkuCount(skus.size());
            vo.setMinRentPrice(skus.stream()
                    .flatMap(sku -> planMap.getOrDefault(sku.getId(), Collections.emptyList()).stream())
                    .map(RentalPlan::getPrice)
                    .min(BigDecimal::compareTo)
                    .orElse(null));
            return vo;
        }).collect(Collectors.toList());
        // pageSize=-1 全量时 MP 跳过 count 查询，total 以实际条数回填
        long total = req.getPageSize() == -1 ? list.size() : page.getTotal();
        return new PageVo<>(total, list);
    }

    @Override
    public String save(SpuSaveReq req) {
        Product product;
        if (req.getId() == null) {
            product = new Product();
            BeanUtil.copyProperties(req, product);
            product.setProductNo(noGenerator.next(NO_PREFIX_PRODUCT));
            product.setStatus(ProductStatus.OFF_SHELF);
        } else {
            product = productRepository.getById(req.getId());
            if (product == null) {
                throw new BusinessException("商品【{}】不存在", req.getId());
            }
            BeanUtil.copyProperties(req, product);
        }
        // 图集 List → JSON 字符串（显式覆盖，避免类型自动转换）
        product.setImages(req.getImages() == null ? null : JSONUtil.toJsonStr(req.getImages()));
        if (req.getId() == null) {
            productRepository.save(product);
        } else {
            productRepository.updateById(product);
        }
        replaceSkus(product.getId(), req);
        replaceParams(product.getId(), req);
        return product.getProductNo();
    }

    @Override
    public SpuDetailVo detail(Long id) {
        Product product = productRepository.getById(id);
        if (product == null) {
            throw new BusinessException("商品【{}】不存在", id);
        }
        SpuDetailVo vo = new SpuDetailVo();
        BeanUtil.copyProperties(product, vo);
        if (StrUtil.isNotBlank(product.getImages())) {
            vo.setImages(JSONUtil.toList(product.getImages(), String.class));
        }
        List<ProductSku> skus = productSkuRepository.listByProductIds(Collections.singletonList(id));
        List<Long> skuIds = skus.stream().map(ProductSku::getId).collect(Collectors.toList());
        Map<Long, List<RentalPlan>> planMap = rentalPlanRepository.listBySkuIds(skuIds).stream()
                .collect(Collectors.groupingBy(RentalPlan::getSkuId));
        vo.setSkus(skus.stream().map(sku -> {
            SpuDetailVo.SkuVo skuVo = new SpuDetailVo.SkuVo();
            BeanUtil.copyProperties(sku, skuVo);
            skuVo.setRentalPlans(planMap.getOrDefault(sku.getId(), Collections.emptyList()).stream().map(plan -> {
                SpuDetailVo.PlanVo planVo = new SpuDetailVo.PlanVo();
                BeanUtil.copyProperties(plan, planVo);
                return planVo;
            }).collect(Collectors.toList()));
            return skuVo;
        }).collect(Collectors.toList()));
        vo.setParams(productParamRepository.listByProductId(id).stream().map(param -> {
            SpuDetailVo.ParamVo paramVo = new SpuDetailVo.ParamVo();
            BeanUtil.copyProperties(param, paramVo);
            return paramVo;
        }).collect(Collectors.toList()));
        return vo;
    }

    @Override
    public BatchOperateResultVo shelf(ShelfReq req) {
        List<Long> ids = req.getIds().stream().distinct().collect(Collectors.toList());
        Map<Long, Product> existMap = productRepository.listByIds(ids).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));
        List<Long> toUpdateIds = new ArrayList<>();
        List<String> successList = new ArrayList<>();
        for (Long id : ids) {
            Product product = existMap.get(id);
            // 不存在（含已删）或状态已符合目标 → 跳过
            if (product == null || Objects.equals(product.getStatus(), req.getStatus())) {
                continue;
            }
            toUpdateIds.add(id);
            successList.add(product.getProductNo());
        }
        if (!toUpdateIds.isEmpty()) {
            productRepository.lambdaUpdate()
                    .set(Product::getStatus, req.getStatus())
                    .in(Product::getId, toUpdateIds)
                    .update();
        }
        return new BatchOperateResultVo(ids.size(), successList.size(), ids.size() - successList.size(), successList);
    }

    /**
     * SKU + 租金方案全删全插（先逻辑删旧方案/SKU，再插新；sku_no 重新生成）
     */
    private void replaceSkus(Long productId, SpuSaveReq req) {
        List<Long> oldSkuIds = productSkuRepository.listByProductIds(Collections.singletonList(productId)).stream()
                .map(ProductSku::getId)
                .collect(Collectors.toList());
        rentalPlanRepository.removeBySkuIds(oldSkuIds);
        productSkuRepository.removeByProductId(productId);
        if (CollUtil.isEmpty(req.getSkus())) {
            return;
        }
        List<ProductSku> skus = req.getSkus().stream().map(item -> {
            ProductSku sku = new ProductSku();
            BeanUtil.copyProperties(item, sku);
            // 全删全插：忽略前端带回的旧 id/skuNo，统一重新生成，避免与逻辑删的旧行主键冲突
            sku.setId(null);
            sku.setProductId(productId);
            sku.setSkuNo(noGenerator.next(NO_PREFIX_SKU));
            sku.setStatus(SkuStatus.ENABLED);
            return sku;
        }).collect(Collectors.toList());
        productSkuRepository.saveBatch(skus);
        List<RentalPlan> plans = new ArrayList<>();
        for (int i = 0; i < req.getSkus().size(); i++) {
            SpuSaveReq.SkuItem item = req.getSkus().get(i);
            if (CollUtil.isEmpty(item.getRentalPlans())) {
                continue;
            }
            for (SpuSaveReq.PlanItem planItem : item.getRentalPlans()) {
                RentalPlan plan = new RentalPlan();
                BeanUtil.copyProperties(planItem, plan);
                plan.setId(null);
                plan.setSkuId(skus.get(i).getId());
                plan.setStatus(SkuStatus.ENABLED);
                plans.add(plan);
            }
        }
        if (!plans.isEmpty()) {
            rentalPlanRepository.saveBatch(plans);
        }
    }

    /**
     * 商品参数全删全插
     */
    private void replaceParams(Long productId, SpuSaveReq req) {
        productParamRepository.removeByProductId(productId);
        if (CollUtil.isEmpty(req.getParams())) {
            return;
        }
        List<ProductParam> params = req.getParams().stream().map(item -> {
            ProductParam param = new ProductParam();
            BeanUtil.copyProperties(item, param);
            param.setId(null);
            param.setProductId(productId);
            return param;
        }).collect(Collectors.toList());
        productParamRepository.saveBatch(params);
    }
}
