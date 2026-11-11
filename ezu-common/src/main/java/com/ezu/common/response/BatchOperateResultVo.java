package com.ezu.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 批量操作结果（让前端能提示「N 条因不满足条件被跳过」）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchOperateResultVo {

    /** 传入总数 */
    private Integer totalCount = 0;
    /** 成功数 */
    private Integer successCount = 0;
    /** 跳过数（不满足条件） */
    private Integer skippedCount = 0;
    /** 成功操作的单号/ID列表 */
    private List<String> successList = new ArrayList<>();

    public BatchOperateResultVo(int totalCount, int successCount, int skippedCount) {
        this.totalCount = totalCount;
        this.successCount = successCount;
        this.skippedCount = skippedCount;
    }
}
