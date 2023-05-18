package com.chua.common.support.value;


import java.util.LinkedList;
import java.util.List;

/**
 * 值
 *
 * @author CH
 * @since 2022/8/9 21:51
 */
public class SimpleIndexValue extends LinkedList<KeyValue> implements IndexValue {

    @Override
    public boolean isNull() {
        return this.isEmpty();
    }

    @Override
    public KeyValue getValue() {
        return null;
    }


    @Override
    public Throwable getThrowable() {
        return null;
    }

    @Override
    public KeyValue get(int index) {
        return index > this.size() ? null : super.get(index);
    }

    @Override
    public List<KeyValue> getAll() {
        return this;
    }
}
