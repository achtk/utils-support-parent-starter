package com.chua.common.support.extra.el.expression.parse.impl;

import com.chua.common.support.extra.el.expression.node.CalculateNode;
import com.chua.common.support.extra.el.expression.node.impl.NumberNode;
import com.chua.common.support.extra.el.expression.parse.Invoker;
import com.chua.common.support.extra.el.expression.token.Operator;
import com.chua.common.support.utils.CharUtils;

import java.util.Deque;
/**
 * 基础类
 * @author CH
 */
public class NumberParser extends NodeParser {

    private boolean match(String el, int offset, Deque<CalculateNode> nodes, int function) {
        if ('-' == getChar(offset, el)) {
            // 这种情况下，-是一个操作符
            if (nodes.peek() != null && !Operator.isOperator(nodes.peek().token())) {
                return false;
            }
            // 这种情况下，-代表是一个负数
            if (CharUtils.isDigital(getChar(offset + 1, el))) {
                return true;
            } else {
                throw new IllegalArgumentException("无法识别的-符号，不是负数也不是操作符,问题区间:" + el.substring(0, offset));
            }
        } else {
            return CharUtils.isDigital(getChar(offset, el));
        }
    }

    @Override
    public int parse(String el, int offset, Deque<CalculateNode> nodes, int function, Invoker next) {
        if (!match(el, offset, nodes, function)) {
            return next.parse(el, offset, nodes, function);
        }
        int index = offset;
        char c = getChar(offset, el);
        if (c == '-') {
            offset += 1;
        }
        boolean hasDot = false;
        while (CharUtils.isDigital(c = getChar(offset, el)) || (!hasDot && c == '.')) {
            offset++;
            if (c == '.') {
                hasDot = true;
            }
        }
        if (c == '.') {
            throw new IllegalArgumentException("非法的负数格式,问题区间:" + el.substring(index, offset));
        }
        String literals = el.substring(index, offset);
        nodes.push(new NumberNode(literals));
        return offset;
    }
}
