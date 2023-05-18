package com.chua.common.support.lang.expression.parser;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.collection.ImmutableCollection;
import com.chua.common.support.extra.el.expression.Expression;
import com.chua.common.support.value.Value;

import java.util.Map;

/**
 * 解析器
 * @author CH
 */
@Spi("el")
public class DelegateExpressionParser implements ExpressionParser{

    final Map<String, Object> context = ImmutableCollection.<String, Object>newMap().build();

    @Override
    public ExpressionParser setVariable(String name, Object value) {
        context.put(name, value);
        return this;
    }

    @Override
    public Value<?> parseExpression(String express) {
        Expression lexer = Expression.parse(express);
        return Value.of(lexer.calculate(context));
    }
}
