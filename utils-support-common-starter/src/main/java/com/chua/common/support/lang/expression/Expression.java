package com.chua.common.support.lang.expression;

/**
 * 表达式
 * @author CH
 */
public interface Expression {
    /**
     * 创建代理
     *
     * @param <T>  类型
     * @param type 类型
     * @return 代理
     */
    <T> T createProxy(Class<T> type);

    /**
     * 对象
     *
     * @param type 类型
     * @param <T>  类型
     * @return 对象
     */
    <T> T create(Class<T> type);

    /**
     * 类型
     * @return 类型
     */
    Class<?> getType();
}
