package com.ezu.product.domain.req;

import com.ezu.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * C端商品列表查询
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AppProductQueryReq extends PageQuery {

    /** 二级品类ID */
    private Long categoryId;

    /** 品牌（精确匹配） */
    private String brand;

    /** 关键词（模糊匹配商品名称/副标题） */
    private String keyword;
}
