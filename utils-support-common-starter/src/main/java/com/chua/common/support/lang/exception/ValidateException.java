package com.chua.common.support.lang.exception;

import com.chua.common.support.utils.StringUtils;

/**
 * validate
 * @author CH
 */
public class ValidateException extends RuntimeException{

    public ValidateException() {
    }

    public ValidateException(String message) {
        super(message);
    }

    public ValidateException(String message, Throwable cause) {
        super(message, cause);
    }
    public ValidateException(String messageTemplate, Object... params) {
        super(StringUtils.format(messageTemplate, params));
    }

    public ValidateException(Throwable cause) {
        super(cause);
    }

    public ValidateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
