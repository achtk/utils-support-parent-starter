package com.chua.common.support.extra.el.expression.parse.impl;

import com.chua.common.support.extra.el.exception.IllegalFormatException;
import com.chua.common.support.extra.el.expression.node.CalculateNode;
import com.chua.common.support.extra.el.expression.node.impl.StringNode;
import com.chua.common.support.extra.el.expression.parse.Invoker;

import java.util.Deque;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_RIGHT_ONE_SLASH_CHAR;

/**
 * 基础类
 *
 * @author CH
 */
public class ConstantStringParser extends BaseNodeParser {

    @Override
    public int parse(String el, int offset, Deque<CalculateNode> nodes, int function, Invoker next) {
        if (SYMBOL_RIGHT_ONE_SLASH_CHAR != getChar(offset, el)) {
            return next.parse(el, offset, nodes, function);
        }
        offset += 1;
        int origin = offset;
        int length = el.length();
        while (offset < length && getChar(offset, el) != SYMBOL_RIGHT_ONE_SLASH_CHAR) {
            offset++;
        }
        if (getChar(offset, el) != SYMBOL_RIGHT_ONE_SLASH_CHAR) {
            throw new IllegalFormatException("字符串表达式没有被'包围", el.substring(origin - 1));
        }
        String literals = el.substring(origin, offset);
        nodes.push(new StringNode(literals));
        offset += 1;
        return offset;
    }
}
