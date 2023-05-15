package com.chua.common.support.json.jsonpath.internal;

/**
 * @author Administrator
 */
public class EvaluationAbortException extends RuntimeException {

    private static final long serialVersionUID = 4419305302960432348L;

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
