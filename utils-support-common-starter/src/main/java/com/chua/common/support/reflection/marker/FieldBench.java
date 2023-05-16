package com.chua.common.support.reflection.marker;

import com.chua.common.support.reflection.describe.CraftDescribe;
import com.chua.common.support.reflection.describe.FieldDescribe;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.value.Value;

/**
 * 字段执行器
 *
 * @author CH
 */
public class FieldBench implements Bench {
    private final FieldDescribe fieldDescribe;
    private final Object entity;

    public FieldBench(FieldDescribe fieldDescribe, Object entity) {
        this.fieldDescribe = fieldDescribe;
        this.entity = entity;
    }

    @Override
    public Class<?> returnType() {
        return ClassUtils.forName(fieldDescribe.returnType());
    }

    @Override
    public Value<Object> execute(Object... args) {
        return fieldDescribe.get(entity);
    }

    /**
     * 获取值
     *
     * @return 获取值
     */
    public Value<Object> get() {
        return fieldDescribe.get(entity);
    }

    /**
     * 获取值
     *
     * @param entity 对象
     * @return 获取值
     */
    public Value<Object> get(Object entity) {
        return fieldDescribe.get(entity);
    }

    /**
     * 获取值
     *
     * @param args 参数
     * @return 获取值
     */
    public Value<Object> set(Object args) {
        return fieldDescribe.set(entity, args);
    }

    /**
     * 获取值
     *
     * @param entity 对象
     * @param args   参数
     * @return 获取值
     */
    public Value<Object> set(Object entity, Object args) {
        return fieldDescribe.set(entity, args);
    }

    @Override
    public Value<Object> executeBean(Object entity, Object... args) {
        return fieldDescribe.get(entity);
    }

    /**
     * 执行
     *
     * @param craftDescribe 参数
     * @return 结果
     */
    @Override
    public Value<Object> execute(CraftDescribe craftDescribe) {
        return execute(craftDescribe.obj() == null ? entity : craftDescribe.obj(), craftDescribe.parameters().toArray());
    }
}
