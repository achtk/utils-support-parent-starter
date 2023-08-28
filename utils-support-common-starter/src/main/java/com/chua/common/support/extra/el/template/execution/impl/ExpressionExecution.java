package com.chua.common.support.extra.el.template.execution.impl;

import com.chua.common.support.extra.el.expression.Expression;
import com.chua.common.support.extra.el.template.execution.Execution;

import java.util.Map;

/**
 * 基础类
 *
 * @author CH
 */
public class ExpressionExecution implements Execution {
    private final Expression expression;

    public ExpressionExecution(Expression expression) {
        this.expression = expression;
    }

    @Override
    public boolean execute(Map<String, Object> variables, StringBuilder cache) {
        Object result = expression.calculate(variables);
        if (result != null) {
            cache.append(result);
        }
        return true;
    }

    @Override
    public void check() {
        // TODO Auto-generated method stub
    }
}
