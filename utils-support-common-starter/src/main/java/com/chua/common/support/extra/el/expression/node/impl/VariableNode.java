package com.chua.common.support.extra.el.expression.node.impl;

import com.chua.common.support.extra.el.expression.node.CalculateNode;
import com.chua.common.support.extra.el.expression.token.Token;
import com.chua.common.support.extra.el.expression.token.ValueResult;

import java.util.Map;
/**
 * 基础类
 * @author CH
 */
public class VariableNode implements CalculateNode
{
    private final String literals;

    public VariableNode(String literals)
    {
        this.literals = literals;
    }

    @Override
    public Object calculate(Map<String, Object> variables)
    {
        return variables.get(literals);
    }

    @Override
    public Token token()
    {
        return ValueResult.VARIABLE;
    }

    @Override
    public String toString()
    {
        return "VariableNode [literals=" + literals + "]";
    }

    @Override
    public String literals()
    {
        return literals;
    }
}
