package com.chua.common.support.extra.el.expression.parse.impl;

import com.chua.common.support.extra.el.expression.node.CalculateNode;
import com.chua.common.support.extra.el.expression.node.impl.SymBolNode;
import com.chua.common.support.extra.el.expression.parse.Invoker;
import com.chua.common.support.extra.el.expression.token.Symbol;

import java.util.Deque;

public class LeftBracketParser extends NodeParser
{

    @Override
    public int parse(String el, int offset, Deque<CalculateNode> nodes, int function, Invoker next)
    {
        if ('[' != getChar(offset, el))
        {
            return next.parse(el, offset, nodes, function);
        }
        nodes.push(new SymBolNode(Symbol.LEFT_BRACKET));
        offset += 1;
        return offset;
    }
}
