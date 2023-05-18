package com.chua.common.support.value;

/**
 * 空值
 *
 * @author CH
 */

public final class NullValue implements Value<Object> {
    public static final Value INSTANCE = new NullValue();

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public Throwable getThrowable() {
        return null;
    }

    @Override
    public boolean isNull() {
        return true;
    }
}
