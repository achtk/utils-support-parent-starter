package com.chua.common.support.reflection.marker;

import com.chua.common.support.converter.Converter;
import com.chua.common.support.reflection.describe.CraftDescribe;
import com.chua.common.support.value.Value;

/**
 * 执行环境
 *
 * @author CH
 */
public interface Bench {
    /**
     * 返回类型
     *
     * @return 类型
     */
    Class<?> returnType();

    /**
     * 执行
     *
     * @param args 参数
     * @return 结果
     */
    Value<Object> execute(Object... args);

    /**
     * 执行
     *
     * @param entity 实体
     * @param args   参数
     * @return 结果
     */
    Value<Object> executeBean(Object entity, Object... args);

    /**
     * 执行
     *
     * @param craftDescribe 参数
     * @return 结果
     */
    default Value<Object> execute(CraftDescribe craftDescribe) {
        return execute(craftDescribe.obj(), craftDescribe.parameters().toArray());
    }

    /**
     * 执行
     *
     * @param craftDescribe 参数
     * @param target        类型
     * @return 结果
     */
    default <T> T execute(CraftDescribe craftDescribe, Class<T> target) {
        return Converter.convertIfNecessary(execute(craftDescribe.obj(), craftDescribe.parameters().toArray()).getValue(), target);
    }
}
