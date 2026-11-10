package com.ezu.common.page;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页响应
 */
@Data
public class PageVo<T> {

    private Long total;
    private List<T> list;

    public PageVo() {
        this.total = 0L;
        this.list = new ArrayList<>();
    }

    public PageVo(Long total, List<T> list) {
        this.total = total;
        this.list = list;
    }
}
