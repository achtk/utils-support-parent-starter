package com.chua.common.support.extra.el.expression.node.impl;

import com.chua.common.support.extra.el.expression.node.CalculateNode;
import com.chua.common.support.extra.el.expression.token.Token;

import java.util.Map;

public class TypeNode implements CalculateNode
{
    private final Class<?> ckass;
    private final Token    token;

    public TypeNode(Class<?> ckass, Token token)
    {
        this.ckass = ckass;
        this.token = token;
    }

    @Override
    public Object calculate(Map<String, Object> variables)
    {
        return ckass;
    }

    @Override
    public Token token()
    {
        return token;
    }

    @Override
    public String literals()
    {
        return ckass.getName();
    }

    public String toString()
    {
        return literals();
    }
}
