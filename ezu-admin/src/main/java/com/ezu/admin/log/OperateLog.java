package com.ezu.admin.log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日志注解：标注在写操作 Controller 方法上，由 {@link OperateLogAspect} 环绕记录到 t_operate_log
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperateLog {

    /**
     * 业务模块（如 系统管理）
     */
    String module();

    /**
     * 操作（如 保存后台账号）
     */
    String operation();
}
