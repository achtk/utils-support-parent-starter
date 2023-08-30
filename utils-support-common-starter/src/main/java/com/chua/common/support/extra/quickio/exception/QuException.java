package com.chua.common.support.extra.quickio.exception;

/**
 * 异常
 * @author CH
 */
public class QuException extends RuntimeException {

    public QuException(String message) {
        super(message);
    }


    public QuException(Throwable cause) {
        super(cause);
    }

}
