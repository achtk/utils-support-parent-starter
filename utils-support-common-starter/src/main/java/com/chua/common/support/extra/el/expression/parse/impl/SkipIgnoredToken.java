package com.chua.common.support.extra.el.expression.parse.impl;

import com.chua.common.support.extra.el.expression.node.CalculateNode;
import com.chua.common.support.extra.el.expression.parse.Invoker;

import java.util.Deque;

public class SkipIgnoredToken extends NodeParser
{

    @Override
    public int parse(String el, int offset, Deque<CalculateNode> nodes, int function, Invoker next)
    {
        offset = skipWhiteSpace(offset, el);
        return next.parse(el, offset, nodes, function);
    }
}
