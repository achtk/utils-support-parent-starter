package com.chua.common.support.extra.el.template.execution.impl;

import com.chua.common.support.extra.el.expression.Expression;
import com.chua.common.support.extra.el.template.execution.Execution;
import com.chua.common.support.extra.el.template.execution.WithBodyExecution;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 基础类
 *
 * @author CH
 */
public class IfExecution implements WithBodyExecution {

    private final Expression conditionLexer;
    private final List<Execution> elseIfExecutions = new LinkedList<Execution>();
    private Execution[] body;
    private Execution elseExecution;

    public IfExecution(Expression conditionLexer) {
        this.conditionLexer = conditionLexer;
    }

    @Override
    public boolean execute(Map<String, Object> variables, StringBuilder cache) {
        Object result = conditionLexer.calculate(variables);
        if (result == null) {
            throw new IllegalArgumentException("参数不存在，导致无法计算条件表达式");
        }
        if ((Boolean) result) {
            for (Execution each : body) {
                each.execute(variables, cache);
            }
        } else {
            for (Execution execution : elseIfExecutions) {
                if (execution.execute(variables, cache)) {
                    return true;
                }
            }
            if (elseExecution != null) {
                elseExecution.execute(variables, cache);
            }
        }
        return true;
    }

    public void addElseIf(ElseIfExecution execution) {
        elseIfExecutions.add(execution);
    }

    public void setElse(ElseExecution execution) {
        elseExecution = execution;
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
