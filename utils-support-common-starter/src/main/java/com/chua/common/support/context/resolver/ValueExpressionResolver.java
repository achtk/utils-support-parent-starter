package com.chua.common.support.context.resolver;

import java.lang.reflect.Field;

/**
 * 值适配器
 *
 * @author CH
 */
public interface ValueExpressionResolver {
    /**
     * 获取表达式
     *
     * @param field 字段
     * @return 表达式
     */
    String getExpression(Field field);
}
