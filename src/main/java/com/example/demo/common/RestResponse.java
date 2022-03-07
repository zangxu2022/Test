package com.example.demo.common;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ApiUser: liuzheqiang
 * Date: 2017/9/25
 * Time: 17:17
 */
@Data
@NoArgsConstructor
public class RestResponse<T> {

    private static final String DEFAULT_SUCCESS_RESULT = "SUCCESS";

    private static final String DEFAULT_FAIL_RESULT = "FAIL";

    private static final String MAINTAIN_RESULT = "MAINTAIN";

    public static final String UNAUTHORIZED_CODE = "401";

    private static final String DEFAULT_SUCCESS_MESSAGE = "OK";

    private String code;
    private String message;
    private T data;

    private RestResponse(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess(){
        return DEFAULT_SUCCESS_RESULT.equals(this.code);
    }

    public static <T> RestResponse<T> success() {
        return successWithMessage(DEFAULT_SUCCESS_MESSAGE);
    }

    public static <T> RestResponse<T> success(T data) {
        return send(DEFAULT_SUCCESS_RESULT, DEFAULT_SUCCESS_MESSAGE, data);
    }

    public static <T> RestResponse<T> successWithMessage(String message) {
        return send(DEFAULT_SUCCESS_RESULT, message, null);
    }

    public static <T> RestResponse<T> successWithMessage(T data, String message) {
        return send(DEFAULT_SUCCESS_RESULT, message, data);
    }

    public static <T> RestResponse<T> failureWithMessage(String code, String message) {
        return send(code, message, null);
    }

    public static <T> RestResponse<T> failureWithMessage(String code, String message, T data) {
        return send(code, message, data);
    }

    public static <T> RestResponse<T> failureWithMessage(String message) {
        return send(DEFAULT_FAIL_RESULT, message, null);
    }

    public static <T> RestResponse<T> failureWithMessage(T data, String message) {
        return send(DEFAULT_FAIL_RESULT, message, data);
    }

    public static <T> RestResponse<T> failOfUnauthorized(String message) {
        return send(UNAUTHORIZED_CODE, message, null);
    }

    public static <T> RestResponse<T> maintainOfUnauthorized(String message) {
        return send(MAINTAIN_RESULT, message, null);
    }

    public static <T> RestResponse<T> send(String code, String message, T data) {
        return new RestResponse<T>(code, message, data);
    }

    public static <T> RestResponse<T> parseToMap(String json, Class<T> type) {
        return JSON.parseObject(json,
                new TypeReference<RestResponse<T>>(type) {});
    }

}
