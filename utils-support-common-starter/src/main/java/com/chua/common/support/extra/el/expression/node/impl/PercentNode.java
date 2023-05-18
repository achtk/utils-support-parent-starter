package com.chua.common.support.extra.el.expression.node.impl;

import com.chua.common.support.extra.el.expression.token.Operator;
import com.chua.common.support.extra.el.expression.util.number.PercentUtil;

import java.util.Map;

public class PercentNode extends OperatorResultNode
{
    public PercentNode()
    {
        super(Operator.PERCENT);
    }

    @Override
    public Object calculate(Map<String, Object> variables)
    {
        Object leftValue = leftOperand.calculate(variables);
        if (leftValue == null)
        {
            return null;
        }
        Object rightValue = rightOperand.calculate(variables);
        if (rightValue == null)
        {
            return null;
        }
        return PercentUtil.calculate((Number) leftValue, (Number) rightValue);
    }
}
