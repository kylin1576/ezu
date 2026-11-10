package com.ezu.common.response;

import lombok.Data;

/**
 * 统一响应信封
 */
@Data
public class ResultData<T> {

    private int code;
    private String msg;
    private T data;

    public static <T> ResultData<T> success(T data) {
        ResultData<T> result = new ResultData<>();
        result.code = ResultCode.SUCCESS;
        result.msg = "success";
        result.data = data;
        return result;
    }

    public static <T> ResultData<T> fail(int code, String msg) {
        ResultData<T> result = new ResultData<>();
        result.code = code;
        result.msg = msg;
        return result;
    }

    public boolean isSuccess() {
        return this.code == ResultCode.SUCCESS;
    }
}
