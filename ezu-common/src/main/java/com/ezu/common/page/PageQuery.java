package com.ezu.common.page;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 分页请求基类（pageSize=-1 表示不分页查全量）
 */
@Data
public class PageQuery {

    @Min(value = 1, message = "页码最小为1")
    private Integer pageNum = 1;

    @Min(value = -1, message = "每页条数不合法")
    @Max(value = 500, message = "每页最多500条")
    private Integer pageSize = 10;
}
