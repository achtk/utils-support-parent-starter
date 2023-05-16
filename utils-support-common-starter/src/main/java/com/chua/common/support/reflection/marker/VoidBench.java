package com.chua.common.support.reflection.marker;

import com.chua.common.support.value.Value;

/**
 * 字段执行器
 *
 * @author CH
 */
public class VoidBench implements Bench {

    public static final Bench INSTANCE = new VoidBench();

    @Override
    public Class<?> returnType() {
        return void.class;
    }

    @Override
    public Value<Object> execute(Object... args) {
        return null;
    }

    @Override
    public Value<Object> executeBean(Object entity, Object... args) {
        return null;
    }
}
