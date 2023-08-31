package com.chua.common.support.file.univocity.parsers.common.input;

/**
 * Internal exception marker to signalize the end of the input.
 *
 * @author Administrator
 */
public final class EofException extends RuntimeException {

    private static final long serialVersionUID = -4064380464076294133L;

    /**
     * Creates a new exception
     */
    public EofException() {
        super();
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
