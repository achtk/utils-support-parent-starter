package com.chua.common.support.extra.el.expression.parse.impl;

import com.chua.common.support.extra.el.expression.node.CalculateNode;
import com.chua.common.support.extra.el.expression.node.impl.ClassPropertyNode;
import com.chua.common.support.extra.el.expression.node.impl.ObjectPropertyNode;
import com.chua.common.support.extra.el.expression.parse.Invoker;
import com.chua.common.support.extra.el.expression.token.ValueResult;
import com.chua.common.support.extra.el.expression.util.CharType;
import com.chua.common.support.extra.el.expression.util.Functions;

import java.util.Deque;

public class PropertyParser extends NodeParser
{

    @Override
    public int parse(String el, int offset, Deque<CalculateNode> nodes, int function, Invoker next)
    {
        // 如果是后一种情况，意味着此时应该是一个枚举值而不是属性
        if ('.' != getChar(offset, el)//
                || (nodes.peek() != null && nodes.peek().token() == ValueResult.TYPE_ENUM))
        {
            return next.parse(el, offset, nodes, function);
        }
        int origin = offset;
        offset += 1;
        char c;
        while (CharType.isAlphabet(c = getChar(offset, el)) || CharType.isDigital(c))
        {
            offset++;
        }
        // 该情况意味着是方法
        if (c == '(')
        {
            return next.parse(el, origin, nodes, function);
        }
        String        literals = el.substring(origin + 1, offset);
        CalculateNode beanNode = nodes.pop();
        CalculateNode current;
        if (beanNode.token() == ValueResult.TYPE)
        {
            current = new ClassPropertyNode(literals, beanNode);
        }
        else
        {
            current = new ObjectPropertyNode(literals, beanNode, Functions.isRecognizeEveryTime(function));
        }
        nodes.push(current);
        return offset;
    }
}
