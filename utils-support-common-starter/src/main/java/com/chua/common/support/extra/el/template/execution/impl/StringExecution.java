package com.chua.common.support.extra.el.template.execution.impl;

import com.chua.common.support.extra.el.template.execution.Execution;

import java.util.Map;

public class StringExecution implements Execution
{
    private final String literals;

    public StringExecution(String literals)
    {
        this.literals = literals;
    }

    @Override
    public boolean execute(Map<String, Object> variables, StringBuilder cache)
    {
        cache.append(literals);
        return true;
    }

    @Override
    public void check()
    {
    }
}
