package com.chua.common.support.extra.el.expression.node.impl;

import com.chua.common.support.extra.el.expression.node.CalculateNode;
import com.chua.common.support.extra.el.expression.token.Operator;
import com.chua.common.support.extra.el.expression.token.Token;
import com.chua.common.support.extra.el.expression.token.ValueResult;

public abstract class OperatorResultNode implements CalculateNode
{
    protected CalculateNode leftOperand;
    protected CalculateNode rightOperand;
    protected Operator      type;

    protected OperatorResultNode(Operator type)
    {
        this.type = type;
    }

    public void setLeftOperand(CalculateNode node)
    {
        leftOperand = node;
    }

    public void setRightOperand(CalculateNode node)
    {
        rightOperand = node;
    }

    @Override
    public Token token()
    {
        return ValueResult.OPERATOR_RESULT;
    }

    public String literals()
    {
        return leftOperand.literals() + type.getLiterals() + rightOperand.literals();
    }

    @Override
    public String toString()
    {
        return literals();
    }
}
