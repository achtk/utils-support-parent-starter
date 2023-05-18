package com.chua.common.support.extra.el.expression.node.impl;

import com.chua.common.support.extra.el.expression.node.CalculateNode;
import com.chua.common.support.extra.el.expression.node.QuestionNode;
import com.chua.common.support.extra.el.expression.token.Token;
import com.chua.common.support.extra.el.expression.token.ValueResult;

import java.util.Map;

public class QuestionNodeImpl implements QuestionNode
{
    private CalculateNode conditionNode;
    private CalculateNode expressionNode1;
    private CalculateNode expressionNode2;

    @Override
    public Object calculate(Map<String, Object> variables)
    {
        Object condition = conditionNode.calculate(variables);
        if (condition == null)
        {
            return null;
        }
        if ((Boolean) condition)
        {
            return expressionNode1.calculate(variables);
        }
        else
        {
            return expressionNode2.calculate(variables);
        }
    }

    @Override
    public Token token()
    {
        return ValueResult.OPERATOR_RESULT;
    }

    @Override
    public void setConditionNode(CalculateNode node)
    {
        conditionNode = node;
    }

    @Override
    public void setLeftNode(CalculateNode node)
    {
        expressionNode1 = node;
    }

    @Override
    public void setRightNode(CalculateNode node)
    {
        expressionNode2 = node;
    }

    @Override
    public String literals()
    {
        return conditionNode.literals() + "?" + expressionNode1.literals() + ":" + expressionNode2.literals();
    }

    @Override
    public String toString()
    {
        return literals();
    }
}
