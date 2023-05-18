package com.chua.common.support.value;


import com.chua.common.support.converter.Converter;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 值
 *
 * @author CH
 * @since 2022/8/9 21:51
 */
public class SimpleTypeValue implements TypeValue {

    private List<Object> value = new LinkedList<>();
    public SimpleTypeValue(List<Object> value) {
        this.value = value;
    }

    public SimpleTypeValue(Object... args) {
        value.addAll(Arrays.asList(args));
    }
    @Override
    public <T> T get(Class<T> type) {
        for (Object o : value) {
            if(null != o && type.isAssignableFrom(o.getClass())) {
                return (T) o;
            }
        }

        for (Object o : value) {
            if(null != o) {
                T necessary = Converter.convertIfNecessary(o, type);
                if(null != necessary) {
                    return necessary;
                }
            }
        }

        return null;
    }


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
        return value.isEmpty();
    }
}
