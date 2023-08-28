package com.chua.common.support.extra.el.expression.parse.impl;

import com.chua.common.support.extra.el.expression.node.CalculateNode;
import com.chua.common.support.extra.el.expression.node.impl.SymBolNode;
import com.chua.common.support.extra.el.expression.parse.Invoker;
import com.chua.common.support.extra.el.expression.token.Symbol;

import java.util.Deque;
/**
 * 基础类
 * @author CH
 */
public class LeftParenParser extends NodeParser {

    @Override
    public int parse(String el, int offset, Deque<CalculateNode> nodes, int function, Invoker next) {
        if ('(' != getChar(offset, el)) {
            return next.parse(el, offset, nodes, function);
        }
        offset += 1;
        nodes.push(new SymBolNode(Symbol.LEFT_PAREN));
        return offset;
    }
}
