package com.chua.common.support.reflection.describe.provider;

import com.chua.common.support.reflection.describe.TypeDescribe;

import static com.chua.common.support.constant.CommonConstant.EMPTY_OBJECT;


/**
 * 提供者
 *
 * @author CH
 */
public interface DescribeProvider {

    /**
     * 执行方法
     *
     * @param entity 参数
     * @param args   参数
     */
    default void execute(Object entity, Object... args) {
        execute(entity, Object.class, args);
    }

    /**
     * 执行方法
     *
     * @param args 参数
     */
    default void execute(Object... args) {
        execute(Object.class, args);
    }

    /**
     * 执行方法
     *
     * @param entity 实体
     * @param args   参数
     * @param target 类型
     * @param <T>    类型
     * @return T
     */
    <T> T execute(Object entity, Class<T> target, Object... args);

    /**
     * 执行方法
     *
     * @param entity 实体
     * @param target 类型
     * @return T
     */
    default <T> T execute(Object entity, Class<T> target) {
        return execute(entity, target, EMPTY_OBJECT);
    }

    /**
     * 执行方法
     *
     * @param target 类型
     * @return T
     */
    default <T> T execute(Class<T> target) {
        return execute(null, target, EMPTY_OBJECT);
    }

    /**
     * 执行方法
     *
     * @param target 类型
     * @param args   参数
     * @return T t
     */
    <T> T executeThis(Class<T> target, Object... args);
    /**
     * 执行方法
     *
     * @param args   参数
     * @return value
     */
    default Object executeThis(Object... args) {
        return executeThis(Object.class, args);
    }

    /**
     * 链式处理
     *
     * @param bean 对象
     * @param args 参数
     * @return 描述
     */
    default TypeDescribe isChain(Object bean, Object... args) {
        Object execute = execute(bean, Object.class, args);
        return new TypeDescribe(execute);
    }

    /**
     * 链式处理
     *
     * @param args 参数
     * @return 描述
     */
    default TypeDescribe isChain(Object... args) {
        return isChain(null, args);
    }
}
