package com.chua.common.support.reflection.marker;

import com.chua.common.support.reflection.describe.ConstructDescribe;
import com.chua.common.support.reflection.describe.CraftDescribe;
import com.chua.common.support.value.Value;

/**
 * 方法执行器
 *
 * @author CH
 */
public class ConstructBench implements Bench {

    private final ConstructDescribe constructDescribe;
    private transient final Object entity;

    public ConstructBench(ConstructDescribe constructDescribe, Object entity) {
        this.constructDescribe = constructDescribe;
        this.entity = entity;
    }

    @Override
    public Class<?> returnType() {
        return null;
    }

    @Override
    public Value<Object> execute(Object... args) {
        return constructDescribe.invoke(entity, args);
    }

    @Override
    public Value<Object> executeBean(Object entity, Object[] args, Object... plugin) {
        return constructDescribe.invoke(entity, args);
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
