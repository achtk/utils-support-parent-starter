package com.chua.common.support.lang.expression.parser;

import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.value.Value;

import java.util.Map;

/**
 * 解析器
 * @author CH
 */
public interface ExpressionParser {
    static ExpressionParser create() {
        return ServiceProvider.of(ExpressionParser.class).getNewExtension("el");
    }

    /**
     * 设置参数
     * @param name 名称
     * @param value 值
     * @return this
     */

    ExpressionParser setVariable(String name, Object value);
    /**
     * 设置参数
     * @param value 值
     * @return this
     */

    default ExpressionParser setVariable(Map<String, Object> value) {
        value.forEach(this::setVariable);
        return this;
    }

    /**
     * 解析表达式
     * @param express 表达式
     * @return 结果
     */
    Value<?> parseExpression(String express);
}
