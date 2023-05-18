package com.chua.common.support.database.expression;

/**
 * 表达式
 *
 * @author CH
 */
public interface Expression {

    /**
     * 获取数据
     *
     * @param type 类型
     * @param <T>  类型
     * @return 数据
     */
    <T> T getValue(Class<T> type);

}
