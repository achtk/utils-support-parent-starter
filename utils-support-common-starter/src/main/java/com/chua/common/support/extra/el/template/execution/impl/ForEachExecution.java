package com.chua.common.support.extra.el.template.execution.impl;

import com.chua.common.support.extra.el.expression.Expression;
import com.chua.common.support.extra.el.template.execution.Execution;
import com.chua.common.support.extra.el.template.execution.WithBodyExecution;

import java.util.Collection;
import java.util.Map;

/**
 * 基础类
 *
 * @author CH
 */
public class ForEachExecution implements WithBodyExecution {
    private final String itemName;
    private final Expression collection;
    private Execution[] body;

    public ForEachExecution(String itemName, Expression collection) {
        this.itemName = itemName;
        this.collection = collection;
    }

    @Override
    public boolean execute(Map<String, Object> variables, StringBuilder cache) {
        Object result = collection.calculate(variables);
        if (result == null) {
            return true;
        }
        if (result instanceof Collection<?>) {
            for (Object each : ((Collection<?>) result)) {
                variables.put(itemName, each);
                for (Execution execution : body) {
                    execution.execute(variables, cache);
                }
            }
            variables.remove(itemName);
        }
        return true;
    }

    @Override
    public void check() {
        // TODO Auto-generated method stub
    }

    @Override
    public void setBody(Execution... executions) {
        body = executions;
    }

    @Override
    public boolean isBodyNotSet() {
        return body == null;
    }
}
