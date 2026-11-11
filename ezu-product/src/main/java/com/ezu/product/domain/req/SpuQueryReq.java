package com.ezu.product.domain.req;

import com.ezu.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品SPU列表查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SpuQueryReq extends PageQuery {

    /** 品类ID */
    private Long categoryId;

    /** 品牌（模糊） */
    private String brand;

    /** 商品名称（模糊） */
    private String name;

    /** 状态（1=上架，2=下架） */
    private Integer status;
}
