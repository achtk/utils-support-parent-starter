package com.chua.common.support.extra.el.exception;

public class UnParsedException extends RuntimeException
{

    /**
     *
     */
    private static final long serialVersionUID = 6748928105241855848L;

    public UnParsedException(String el, Throwable e)
    {
        super("无法解析表达式:" + el, e);
    }
}
