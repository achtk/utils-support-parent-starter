package com.chua.common.support.extra.el.expression.node.impl;

import com.chua.common.support.extra.el.expression.token.Operator;

import java.util.Map;

import static com.chua.common.support.extra.el.expression.util.OperatorResultUtil.trueOfFalse;

public class DoubleAmpNode extends OperatorResultNode
{
    public DoubleAmpNode()
    {
        super(Operator.DOUBLE_AMP);
    }

    @Override
    public Object calculate(Map<String, Object> variables)
    {
        return trueOfFalse(leftOperand.calculate(variables)) != false && trueOfFalse(rightOperand.calculate(variables)) != false;
    }

    @Override
    public String toString()
    {
        return literals();
    }
}
