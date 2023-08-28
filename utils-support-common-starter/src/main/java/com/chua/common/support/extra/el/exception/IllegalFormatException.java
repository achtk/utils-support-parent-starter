package com.chua.common.support.extra.el.exception;

/**
 * 基础类
 *
 * @author CH
 */
public class IllegalFormatException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -1549862548075188968L;

    public IllegalFormatException(String msg, String area) {
        super(msg + ",问题区间:" + area);
    }
}
