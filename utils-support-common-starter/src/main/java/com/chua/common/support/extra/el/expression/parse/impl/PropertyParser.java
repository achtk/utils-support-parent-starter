package com.chua.common.support.extra.el.expression.parse.impl;

import com.chua.common.support.extra.el.expression.node.CalculateNode;
import com.chua.common.support.extra.el.expression.node.impl.ClassPropertyNode;
import com.chua.common.support.extra.el.expression.node.impl.ObjectPropertyNode;
import com.chua.common.support.extra.el.expression.parse.Invoker;
import com.chua.common.support.extra.el.expression.token.ValueResult;
import com.chua.common.support.extra.el.expression.util.Functions;
import com.chua.common.support.utils.CharUtils;

import java.util.Deque;
/**
 * 基础类
 * @author CH
 */
public class PropertyParser extends NodeParser {

    @Override
    public int parse(String el, int offset, Deque<CalculateNode> nodes, int function, Invoker next) {

        boolean b = '.' != getChar(offset, el)
                || (nodes.peek() != null && nodes.peek().token() == ValueResult.TYPE_ENUM);
        if (b) {
            return next.parse(el, offset, nodes, function);
        }
        int origin = offset;
        offset += 1;
        char c;
        while (CharUtils.isAlphabet(c = getChar(offset, el)) || CharUtils.isDigital(c)) {
            offset++;
        }
        
        if (c == '(') {
            return next.parse(el, origin, nodes, function);
        }
        String literals = el.substring(origin + 1, offset);
        CalculateNode beanNode = nodes.pop();
        CalculateNode current;
        if (beanNode.token() == ValueResult.TYPE) {
            current = new ClassPropertyNode(literals, beanNode);
        } else {
            current = new ObjectPropertyNode(literals, beanNode, Functions.isRecognizeEveryTime(function));
        }
        nodes.push(current);
        return offset;
    }
}
