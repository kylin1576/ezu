package com.ezu.common.page;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 分页请求基类（pageSize=-1 表示不分页查全量）
 */
@Data
public class PageQuery {

    @NotNull(message = "pageNum不能为空")
    @Min(value = 1, message = "页码最小为1")
    private Integer pageNum = 1;

    @NotNull(message = "pageSize不能为空")
    private Integer pageSize = 10;

    @AssertTrue(message = "pageSize取值不合法（-1=不分页 或 1~500）")
    public boolean isPageSizeLegal() {
        return pageSize == null || pageSize == -1 || (pageSize >= 1 && pageSize <= 500);
    }
}
