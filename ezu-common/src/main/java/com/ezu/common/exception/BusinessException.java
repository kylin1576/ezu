package com.ezu.common.exception;

import cn.hutool.core.util.StrUtil;
import com.ezu.common.response.ResultCode;
import lombok.Getter;

/**
 * 业务异常（消息用 {} 占位符，由 hutool StrUtil.format 格式化）
 */
@Getter
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(String message, Object... args) {
        super(StrUtil.format(message, args));
        this.code = ResultCode.SYSTEM_ERROR;
    }

    public BusinessException(int code, String message, Object... args) {
        super(StrUtil.format(message, args));
        this.code = code;
    }
}
