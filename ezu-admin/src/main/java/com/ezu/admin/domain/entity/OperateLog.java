package com.ezu.admin.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 后台操作日志（只增不改，无逻辑删除——不继承 BaseEntity）
 */
@Data
@TableName("t_operate_log")
public class OperateLog {

    /** 主键（雪花ID） */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 操作人ID（定时任务等场景为空） */
    private Long adminUserId;

    /** 操作人姓名（冗余，展示用） */
    private String operatorName;

    /** 业务模块（如 商品管理） */
    private String module;

    /** 操作（如 新增SKU） */
    private String operation;

    /** 请求方法（类#方法） */
    private String method;

    /** 请求参数（JSON） */
    private String params;

    /** 操作IP */
    private String ip;

    /** 耗时（毫秒） */
    private Integer costMs;

    /** 操作时间 */
    private LocalDateTime createTime;
}
