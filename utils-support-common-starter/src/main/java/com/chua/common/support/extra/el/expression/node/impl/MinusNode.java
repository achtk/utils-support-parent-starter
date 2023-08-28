package com.chua.common.support.extra.el.expression.node.impl;

import com.chua.common.support.extra.el.expression.token.Operator;
import com.chua.common.support.extra.el.expression.util.number.MinusUtil;

import java.util.Map;
/**
 * 基础类
 * @author CH
 */
public class MinusNode extends OperatorResultNode
{
    public MinusNode()
    {
        super(Operator.MINUS);
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
        return MinusUtil.calculate((Number) leftValue, (Number) rightValue);
    }
}
