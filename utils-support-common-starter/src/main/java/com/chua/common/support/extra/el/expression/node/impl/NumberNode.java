package com.chua.common.support.extra.el.expression.node.impl;

import com.chua.common.support.extra.el.expression.node.CalculateNode;
import com.chua.common.support.extra.el.expression.token.Token;
import com.chua.common.support.extra.el.expression.token.ValueResult;

import java.util.Map;
/**
 * 基础类
 * @author CH
 */
public class NumberNode implements CalculateNode
{
    private Number value;

    public NumberNode(String literals)
    {
        if (literals.indexOf('.') > -1)
        {
            value = Float.valueOf(literals);
            if (Float.isInfinite((Float) value))
            {
                value = Double.valueOf(literals);
            }
        }
        else
        {
            try
            {
                value = Integer.valueOf(literals);
            }
            catch (NumberFormatException e)
            {
                value = Long.valueOf(literals);
            }
        }
    }

    @Override
    public Object calculate(Map<String, Object> variables)
    {
        return value;
    }

    @Override
    public Token token()
    {
        return ValueResult.NUMBER;
    }

    @Override
    public String literals()
    {
        return value.toString();
    }

    @Override
    public String toString()
    {
        return literals();
    }
}
