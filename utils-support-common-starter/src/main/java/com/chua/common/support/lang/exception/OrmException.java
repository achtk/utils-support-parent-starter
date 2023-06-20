package com.chua.common.support.lang.exception;

import com.chua.common.support.utils.StringUtils;

/**
 * orm
 * @author CH
 */
public class OrmException extends RuntimeException{

    public OrmException() {
    }

    public OrmException(String message) {
        super(message);
    }

    public OrmException(String message, Throwable cause) {
        super(message, cause);
    }
    public OrmException(String messageTemplate, Object... params) {
        super(StringUtils.format(messageTemplate, params));
    }

    public OrmException(Throwable cause) {
        super(cause);
    }

    public OrmException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
