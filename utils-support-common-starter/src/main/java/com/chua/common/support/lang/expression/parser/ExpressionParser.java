package com.chua.common.support.lang.expression.parser;

import com.chua.common.support.value.Value;

/**
 * 解析器
 * @author CH
 */
public interface ExpressionParser {
    /**
     * 设置参数
     * @param name 名称
     * @param value 值
     * @return this
     */

    ExpressionParser setVariable(String name, Object value);

    /**
     * 解析表达式
     * @param express 表达式
     * @return 结果
     */
    Value<?> parseExpression(String express);
}
