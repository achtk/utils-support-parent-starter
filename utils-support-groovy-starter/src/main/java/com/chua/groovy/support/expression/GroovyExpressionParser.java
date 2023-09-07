package com.chua.groovy.support.expression;

import com.chua.common.support.lang.expression.parser.ExpressionParser;
import com.chua.common.support.value.Value;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;

/**
 * groovy
 *
 * @author CH
 */
public class GroovyExpressionParser implements ExpressionParser {

    final Binding binding = new Binding();

    @Override
    public ExpressionParser setVariable(String name, Object value) {
        binding.setProperty(name, value);
        return this;
    }

    @Override
    public Value<?> parseExpression(String express) {
        GroovyShell groovyShell = new GroovyShell(binding);
        return Value.of(groovyShell.evaluate(express));
    }

    @Override
    public void addFunction(Class<?> type, Object bean) {

    }
}
