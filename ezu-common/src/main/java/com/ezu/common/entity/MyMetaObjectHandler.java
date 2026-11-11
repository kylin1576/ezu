package com.ezu.common.entity;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.ezu.common.satoken.StpAdminUtil;
import com.ezu.common.satoken.StpUserUtil;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 审计字段自动填充：createBy/updateBy 按 admin 优先、user 次之取登录人，未登录（启动初始化/定时/回调线程）不填
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        Long operatorId = currentOperatorId();
        if (operatorId != null) {
            this.strictInsertFill(metaObject, "createBy", Long.class, operatorId);
            this.strictInsertFill(metaObject, "updateBy", Long.class, operatorId);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        Long operatorId = currentOperatorId();
        if (operatorId != null) {
            this.strictUpdateFill(metaObject, "updateBy", Long.class, operatorId);
        }
    }

    /**
     * 当前操作人：admin 账号优先，其次 user 账号，均未登录或无 Sa-Token 上下文返回 null
     */
    private Long currentOperatorId() {
        try {
            if (StpAdminUtil.isLogin()) {
                return StpAdminUtil.stpLogic.getLoginIdAsLong();
            }
            if (StpUserUtil.isLogin()) {
                return StpUserUtil.stpLogic.getLoginIdAsLong();
            }
        } catch (Exception e) {
            // 无请求上下文（启动初始化/定时/异步线程）：保持 null
        }
        return null;
    }
}
