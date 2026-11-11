package com.ezu.product.domainService;

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

/**
 * 商品SPU领域服务
 */
public interface ProductDomainService {

    /**
     * 分页查询（品类名回显、SKU数、最低租金起）
     */
    PageVo<SpuListVo> queryPage(SpuQueryReq req);

    /**
     * 整体保存（SPU+SKU+租金方案+参数；SKU/方案/参数全删全插）
     */
    String save(SpuSaveReq req);

    /**
     * 详情（主表+SKU列表（各带租金方案）+参数）
     */
    SpuDetailVo detail(Long id);

    /**
     * 批量上下架（不存在或状态已符合的计入跳过，successList 放 productNo）
     */
    BatchOperateResultVo shelf(ShelfReq req);

    /**
     * C端商品分页（仅上架SPU；最低租金起（启用SKU的启用方案）+ 在库可租数合计）
     */
    PageVo<AppProductListVo> queryAppPage(AppProductQueryReq req);

    /**
     * C端商品详情（仅上架SPU；只返回启用SKU及其启用方案，各SKU带在库可租数）
     */
    AppProductDetailVo appDetail(Long id);
}
