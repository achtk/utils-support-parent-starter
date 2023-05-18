package com.chua.common.support.extra.el.expression.node.impl;

import com.chua.common.support.extra.el.expression.node.CalculateNode;
import com.chua.common.support.extra.el.expression.token.Operator;
import com.chua.common.support.extra.el.expression.token.Token;

import java.util.Map;

public class OperatorNode implements CalculateNode
{
    private final Operator operatorType;

    public OperatorNode(Operator operatorType)
    {
        this.operatorType = operatorType;
    }

    // 操作符节点不会有计算动作
    @Override
    public Object calculate(Map<String, Object> variables)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Token token()
    {
        return operatorType;
    }

    @Override
    public String literals()
    {
        return operatorType.getLiterals();
    }

    @Override
    public String toString()
    {
        return literals();
    }
}
