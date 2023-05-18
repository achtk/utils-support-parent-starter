package com.chua.common.support.extra.el.expression.parse;

import com.chua.common.support.extra.el.expression.node.CalculateNode;

import java.util.Deque;

public interface Invoker
{
    int parse(String el, int offset, Deque<CalculateNode> nodes, int function);
}
