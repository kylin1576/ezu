package com.ezu.common.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import com.ezu.common.response.ResultCode;
import com.ezu.common.response.ResultData;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

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
        log.warn("未登录访问: type={}", e.getType());
        return ResultData.fail(ResultCode.UNAUTHORIZED, "未登录或登录已过期");
    }

    @ExceptionHandler(NotPermissionException.class)
    public ResultData<Void> handleNotPermission(NotPermissionException e) {
        log.warn("无权限访问: {}", e.getPermission());
        return ResultData.fail(ResultCode.FORBIDDEN, "无操作权限");
    }

    @ExceptionHandler(NotRoleException.class)
    public ResultData<Void> handleNotRole(NotRoleException e) {
        log.warn("角色权限不足: {}", e.getRole());
        return ResultData.fail(ResultCode.FORBIDDEN, "无操作权限");
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResultData<Void> handleValid(BindException e) {
        String msg = e.getBindingResult().getFieldErrors().isEmpty()
                ? "参数校验失败"
                : e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        return ResultData.fail(ResultCode.PARAM_ERROR, msg);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResultData<Void> handleConstraintViolation(ConstraintViolationException e) {
        String message = e.getConstraintViolations().iterator().next().getMessage();
        log.warn("参数校验失败: {}", message);
        return ResultData.fail(ResultCode.PARAM_ERROR, message);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResultData<Void> handleHandlerMethodValidation(HandlerMethodValidationException e) {
        log.warn("参数校验失败: {}", e.getMessage());
        return ResultData.fail(ResultCode.PARAM_ERROR, "参数校验不通过");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResultData<Void> handleMessageNotReadable(HttpMessageNotReadableException e) {
        log.warn("请求体格式错误: {}", e.getMessage());
        return ResultData.fail(ResultCode.PARAM_ERROR, "请求体格式不正确");
    }

    @ExceptionHandler(Exception.class)
    public ResultData<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return ResultData.fail(ResultCode.SYSTEM_ERROR, "系统繁忙，请稍后重试");
    }
}
