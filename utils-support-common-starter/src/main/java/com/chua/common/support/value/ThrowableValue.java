package com.chua.common.support.value;

/**
 * 空值
 *
 * @author CH
 */

public final class ThrowableValue implements Value<Object> {

    final Throwable e;

    public ThrowableValue(Throwable e) {
        this.e = e;
    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public Throwable getThrowable() {
        return e;
    }

    @Override
    public boolean isNull() {
        return true;
    }
}
