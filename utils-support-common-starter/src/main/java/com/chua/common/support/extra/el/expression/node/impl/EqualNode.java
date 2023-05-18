package com.chua.common.support.extra.el.expression.node.impl;

import com.chua.common.support.extra.el.expression.token.Operator;
import com.chua.common.support.extra.el.expression.util.number.EqUtil;

import java.util.Map;

public class EqualNode extends OperatorResultNode
{

    public EqualNode()
    {
        super(Operator.EQ);
    }

    @Override
    public Object calculate(Map<String, Object> variables)
    {
        Object leftValue  = leftOperand.calculate(variables);
        Object rightValue = rightOperand.calculate(variables);
        if (leftValue == null && rightValue == null)
        {
            return false;
        }
        else if (leftValue == null && rightValue != null)
        {
            return false;
        }
        else if (leftValue != null && rightValue == null)
        {
            return false;
        }
        else
        {
            if (leftValue instanceof Number && rightValue instanceof Number)
            {
                return EqUtil.calculate((Number) leftValue, (Number) rightValue);
            }
            else
            {
                return leftValue.equals(rightValue);
            }
        }
    }
}
