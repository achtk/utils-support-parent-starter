package com.chua.common.support.extra.el.expression.node.impl;

import com.chua.common.support.extra.el.expression.node.CalculateNode;
import com.chua.common.support.extra.el.expression.node.MethodNode;
import com.chua.common.support.extra.el.expression.token.Intermediate;
import com.chua.common.support.extra.el.expression.token.Token;
import com.chua.common.support.extra.el.expression.token.ValueResult;

public abstract class AbstractMethodNode implements MethodNode
{
    protected CalculateNode[] argsNodes;
    protected Token           token = Intermediate.METHOD;

    @Override
    public void setArgsNodes(CalculateNode[] argsNodes)
    {
        this.argsNodes = argsNodes;
        token = ValueResult.METHOD;
    }

    @Override
    public Token token()
    {
        return token;
    }
}
