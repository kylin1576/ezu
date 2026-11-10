package com.ezu.common.exception;

import cn.dev33.satoken.exception.NotLoginException;
import com.ezu.common.response.ResultCode;
import com.ezu.common.response.ResultData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理：业务异常 HTTP 一律 200 + 错误 code
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResultData<Void> handleBusiness(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return ResultData.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(NotLoginException.class)
    public ResultData<Void> handleNotLogin(NotLoginException e) {
        return ResultData.fail(ResultCode.UNAUTHORIZED, "未登录或登录已过期");
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResultData<Void> handleValid(BindException e) {
        String msg = e.getBindingResult().getFieldErrors().isEmpty()
                ? "参数校验失败"
                : e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        return ResultData.fail(ResultCode.PARAM_ERROR, msg);
    }

    @ExceptionHandler(Exception.class)
    public ResultData<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return ResultData.fail(ResultCode.SYSTEM_ERROR, "系统繁忙，请稍后重试");
    }
}
