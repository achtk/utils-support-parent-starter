package com.chua.common.support.extra.el.expression.parse.impl;

import com.chua.common.support.extra.el.expression.node.CalculateNode;
import com.chua.common.support.extra.el.expression.node.impl.TypeNode;
import com.chua.common.support.extra.el.expression.parse.Invoker;
import com.chua.common.support.extra.el.expression.token.ValueResult;
import com.chua.common.support.utils.CharUtils;

import java.util.Deque;

import static com.chua.common.support.constant.CommonConstant.*;

/**
 * 基础类
 *
 * @author CH
 */
public class TypeParser extends BaseNodeParser {

    @Override
    public int parse(String el, int offset, Deque<CalculateNode> nodes, int function, Invoker next) {
        if (LETTER_UPPERCASE_T != getChar(offset, el) || SYMBOL_LEFT_BRACKETS_CHAR != getChar(offset + 1, el)) {
            return next.parse(el, offset, nodes, function);
        }
        offset += 2;
        offset = skipWhiteSpace(offset, el);
        int origin = offset;
        char c;
        while (CharUtils.isAlphabet(c = getChar(offset, el)) || SYMBOL_DOT_CHAR == c || SYMBOL_DOLLAR_CHAR == c) {
            offset++;
        }
        int end = offset;
        offset = skipWhiteSpace(offset, el);
        if (SYMBOL_RIGHT_BRACKETS_CHAR != getChar(offset, el)) {
            throw new IllegalArgumentException("类型操作没有被)包围，检查:" + el.substring(origin, offset));
        }
        String literals = el.substring(origin, end);
        try {
            Class<?> type = Class.forName(literals);
            if (Enum.class.isAssignableFrom(type)) {
                nodes.push(new TypeNode(type, ValueResult.TYPE_ENUM));
            } else {
                nodes.push(new TypeNode(type, ValueResult.TYPE));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        offset += 1;
        return offset;
    }
}
