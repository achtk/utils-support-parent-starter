package com.chua.common.support.reflection.describe.provider;

import com.chua.common.support.converter.Converter;
import com.chua.common.support.reflection.describe.TypeDescribe;
import com.chua.common.support.utils.ClassUtils;

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
    <T> T executeSelf(Class<T> target, Object... args);

    /**
     * 执行方法
     *
     * @param args 参数
     * @return value
     */
    default Object executeSelf(Object... args) {
        return executeSelf(Object.class, args);
    }

    /**
     * 执行方法
     *
     * @param targetType 参数
     * @return 返回类型
     */
    @SuppressWarnings("ALL")
    default <T> T executeSelf(Class<T> targetType) {
        try {
            return Converter.convertIfNecessary(executeSelf(Object.class, new Object[0]), targetType);
        } catch (Exception e) {
            if (targetType.isMemberClass()) {
                return (T) ClassUtils.memberDefault(targetType);
            }
        }
        return null;
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
    TypeDescribe isChainSelf(Object... args);

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
