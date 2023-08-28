package com.chua.common.support.extra.el.expression.parse.impl;

import com.chua.common.support.extra.el.expression.node.CalculateNode;
import com.chua.common.support.extra.el.expression.node.impl.EnumNode;
import com.chua.common.support.extra.el.expression.parse.Invoker;
import com.chua.common.support.extra.el.expression.token.ValueResult;

import java.util.Deque;

/**
 * 基础类
 *
 * @author CH
 */
public class EnumParser extends NodeParser {

    @Override
    public int parse(String el, int offset, Deque<CalculateNode> nodes, int function, Invoker next) {
        // 如果是后一种情况，意味着此时应该是一个枚举值而不是属性
        if ('.' != getChar(offset, el) || (nodes.peek() != null && nodes.peek().token() != ValueResult.TYPE_ENUM)) {
            return next.parse(el, offset, nodes, function);
        }
        int origin = offset;
        offset += 1;
        char c;
        while (CharType.isAlphabet(c = getChar(offset, el)) || CharType.isDigital(c)) {
            offset++;
        }
        if (c == '(') {
            throw new IllegalArgumentException("非法的el表达式，检查:" + el.substring(origin, offset));
        }
        String literals = el.substring(origin + 1, offset);
        CalculateNode beanNode = nodes.pop();
        CalculateNode current = new EnumNode(beanNode, literals);
        nodes.push(current);
        return offset;
    }
}
