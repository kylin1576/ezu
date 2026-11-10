package com.ezu.common.response;

/**
 * 全局响应码（业务异常 HTTP 一律返回 200，以 code 判断成败）
 */
public interface ResultCode {

    int SUCCESS = 200;
    int PARAM_ERROR = 400;
    int UNAUTHORIZED = 401;
    int FORBIDDEN = 403;
    int SYSTEM_ERROR = 500;
}
