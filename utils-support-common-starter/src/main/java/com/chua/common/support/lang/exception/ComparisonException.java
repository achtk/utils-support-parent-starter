package com.chua.common.support.lang.exception;

/**
 * 比较异常
 * @author CH
 */
public class ComparisonException extends IllegalArgumentException {
    private final String cleanMessage;
    private final String o1;
    private final String o2;

    public ComparisonException(String cleanMessage, String o1, String o2) {
        super(o1 +" " + o2 + cleanMessage);
        this.cleanMessage = cleanMessage;
        this.o1 = o1;
        this.o2 = o2;
    }
}
