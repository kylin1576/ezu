package com.ezu.product.domain.req;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 商品批量上下架请求
 */
@Data
public class ShelfReq {

    /** 商品ID列表 */
    @NotEmpty(message = "请选择商品")
    private List<Long> ids;

    /** 目标状态（1=上架，2=下架） */
    @NotNull(message = "状态不能为空")
    @Min(value = 1, message = "状态取值不合法")
    @Max(value = 2, message = "状态取值不合法")
    private Integer status;
}
