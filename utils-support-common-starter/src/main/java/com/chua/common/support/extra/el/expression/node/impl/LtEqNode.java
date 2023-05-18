package com.chua.common.support.extra.el.expression.node.impl;

import com.chua.common.support.extra.el.expression.token.Operator;
import com.chua.common.support.extra.el.expression.util.number.GtUtil;

import java.util.Map;

public class LtEqNode extends OperatorResultNode
{
    public LtEqNode()
    {
        super(Operator.LT_EQ);
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
        return (Boolean) GtUtil.calculate((Number) leftValue, (Number) rightValue) == false;
    }
}
