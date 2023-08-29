package com.chua.common.support.extra.el.expression.parse.impl;

import com.chua.common.support.extra.el.expression.node.CalculateNode;
import com.chua.common.support.extra.el.expression.node.impl.SymBolNode;
import com.chua.common.support.extra.el.expression.parse.Invoker;
import com.chua.common.support.extra.el.expression.token.Symbol;

import java.util.Deque;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_COMMA_CHAR;
import static com.chua.common.support.constant.CommonConstant.SYMBOL_LEFT_SLASH;

/**
 * 基础类
 * @author CH
 */
public class CommaParser extends NodeParser {

    @Override
    public int parse(String el, int offset, Deque<CalculateNode> nodes, int function, Invoker next) {
        if (SYMBOL_COMMA_CHAR != getChar(offset, el)) {
            return next.parse(el, offset, nodes, function);
        }
        nodes.push(new SymBolNode(Symbol.COMMA));
        offset += 1;
        return offset;
    }
}
