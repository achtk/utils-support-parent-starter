package com.chua.common.support.extra.el.expression.parse.impl;

import com.chua.common.support.extra.el.expression.node.CalculateNode;
import com.chua.common.support.extra.el.expression.node.impl.BracketNode;
import com.chua.common.support.extra.el.expression.parse.Invoker;
import com.chua.common.support.extra.el.expression.token.Symbol;
import com.chua.common.support.extra.el.expression.util.OperatorResultUtil;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_RIGHT_SQUARE_BRACKET_CHAR;

/**
 * 基础类
 * @author CH
 */
public class RightBracketParser extends BaseNodeParser {

    @Override
    public int parse(String el, int offset, Deque<CalculateNode> nodes, int function, Invoker next) {
        if (SYMBOL_RIGHT_SQUARE_BRACKET_CHAR != getChar(offset, el)) {
            return next.parse(el, offset, nodes, function);
        }
        List<CalculateNode> list = new LinkedList<CalculateNode>();
        CalculateNode pred;
        while ((pred = nodes.pollFirst()) != null) {
            if (pred.token() != Symbol.LEFT_BRACKET) {
                list.add(0, pred);
            } else {
                break;
            }
        }
        if (pred == null) {
            throw new IllegalArgumentException(el.substring(0, offset));
        }
        CalculateNode valueNode = OperatorResultUtil.aggregate(list, el, offset);
        CalculateNode beanNode = nodes.pollFirst();
        if (beanNode == null) {
            throw new UnsupportedOperationException();
        }
        nodes.push(new BracketNode(beanNode, valueNode));
        offset += 1;
        return offset;
    }
}
