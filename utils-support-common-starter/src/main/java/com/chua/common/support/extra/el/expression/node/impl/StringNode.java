package com.chua.common.support.extra.el.expression.node.impl;

import com.chua.common.support.extra.el.expression.node.CalculateNode;
import com.chua.common.support.extra.el.expression.token.Token;
import com.chua.common.support.extra.el.expression.token.ValueResult;

import java.util.Map;

/**
 * 基础类
 *
 * @author CH
 */
public class StringNode implements CalculateNode {
    private final String literals;

    public StringNode(String literals) {
        this.literals = literals;
    }

    @Override
    public Object calculate(Map<String, Object> variables) {
        return literals;
    }

    @Override
    public Token token() {
        return ValueResult.STRING;
    }

    @Override
    public String literals() {
        return '\'' + literals + "'";
    }

    @Override
    public String toString() {
        return literals();
    }
}
