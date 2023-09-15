package com.chua.proxy.support.utils;

import lombok.Getter;
import lombok.Setter;

/**
 * 脱壳
 *
 * @author CH
 */
public class Shuck<T> {

    public static final int CODE_OK = 0;

    public static final int CODE_ERROR = -1;

    public static final String MESSAGE_OK = "Succeed";

    public static final String MESSAGE_ERROR = "Internal Server Error";

    @Setter
    @Getter
    private int code;

    @Setter
    @Getter
    private String message;

    @Getter
    @Setter
    private T data;

    public Shuck() {
    }

    public Shuck(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Shuck<T> success(T data) {
        return success(MESSAGE_OK, data);
    }

    public static <T> Shuck<T> success(String message, T data) {
        return new Shuck<>(CODE_OK, message, data);
    }

    public String toJsonNoData() {
        return jsonOf(this.code, this.message);
    }

    public static String jsonOf(int code, String message) {
        return "{\"code\": " + code + ", \"message\": \"" + message + "\"}";
    }

}
