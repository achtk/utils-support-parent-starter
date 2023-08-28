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
public class EnumNode implements CalculateNode {
    private final Enum<?> value;

    @SuppressWarnings({"rawtypes", "unchecked"})
    public EnumNode(CalculateNode enumTypeNode, String literals) {
        Class<Enum> enumType = (Class<Enum>) enumTypeNode.calculate(null);
        value = Enum.valueOf(enumType, literals);
    }

    @Override
    public Object calculate(Map<String, Object> variables) {
        return value;
    }

    @Override
    public Token token() {
        return ValueResult.ENUM;
    }

    @Override
    public String literals() {
        return value.name();
    }

    @Override
    public String toString() {
        return literals();
    }
}
