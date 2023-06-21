package com.chua.common.support.lang.any;

import com.chua.common.support.value.DelegateValue;

import java.util.Collection;
import java.util.Map;

/**
 * 任意类型
 * @author CH
 */
public class Any extends DelegateValue<Object> {

    public Any(Object value, Throwable e, Object defaultValue) {
        super(value, e, defaultValue);
    }

    public Any(Object value, Object defaultValue) {
        super(value, defaultValue);
    }

    public Any(Object value, Throwable e) {
        super(value, e);
    }

    public Any(Object value) {
        super(value);
    }

    public boolean isMap() {
        return getValue() instanceof Map;
    }

    public boolean isCollection() {
        return getValue() instanceof Collection;
    }
}
