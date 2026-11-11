package com.ezu.product.application;

import com.ezu.common.page.PageVo;
import com.ezu.common.response.BatchOperateResultVo;
import com.ezu.product.domain.req.AppProductQueryReq;
import com.ezu.product.domain.req.ShelfReq;
import com.ezu.product.domain.req.SpuQueryReq;
import com.ezu.product.domain.req.SpuSaveReq;
import com.ezu.product.domain.vo.AppProductDetailVo;
import com.ezu.product.domain.vo.AppProductListVo;
import com.ezu.product.domain.vo.SpuDetailVo;
import com.ezu.product.domain.vo.SpuListVo;
import com.ezu.product.domainService.ProductDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 商品SPU应用服务
 */
@Service
@RequiredArgsConstructor
public class ProductApplicationService {

    private final ProductDomainService productDomainService;

    /**
     * 商品列表
     */
    public PageVo<SpuListVo> list(SpuQueryReq req) {
        return productDomainService.queryPage(req);
    }

    /**
     * 新增/编辑商品（SPU+SKU+租金方案+参数整体保存）
     */
    @Transactional(rollbackFor = Exception.class)
    public String save(SpuSaveReq req) {
        return productDomainService.save(req);
    }

    /**
     * 商品详情
     */
    public SpuDetailVo detail(Long id) {
        return productDomainService.detail(id);
    }

    /**
     * 商品批量上下架
     */
    @Transactional(rollbackFor = Exception.class)
    public BatchOperateResultVo shelf(ShelfReq req) {
        return productDomainService.shelf(req);
    }

    /**
     * C端商品列表（仅上架）
     */
    public PageVo<AppProductListVo> appList(AppProductQueryReq req) {
        return productDomainService.queryAppPage(req);
    }

    /**
     * C端商品详情（仅上架；非上架抛「商品不存在或已下架」）
     */
    public AppProductDetailVo appDetail(Long id) {
        return productDomainService.appDetail(id);
    }
}
