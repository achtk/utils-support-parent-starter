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
public class KeywordNode implements CalculateNode {
    private final Object keywordValue;

    public KeywordNode(String literals) {
        if ("true".equalsIgnoreCase(literals)) {
            keywordValue = Boolean.TRUE;
        } else if ("false".equalsIgnoreCase(literals)) {
            keywordValue = Boolean.FALSE;
        } else if ("null".equalsIgnoreCase(literals)) {
            keywordValue = null;
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public Object calculate(Map<String, Object> variables) {
        return keywordValue;
    }

    @Override
    public Token token() {
        return ValueResult.CONSTANT;
    }

    @Override
    public String toString() {
        return "KeywordNode [keywordValue=" + keywordValue + "]";
    }

    @Override
    public String literals() {
        if (keywordValue != null) {
            return keywordValue.toString();
        } else {
            return "NULL";
        }
    }
}
