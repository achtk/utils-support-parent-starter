package com.chua.common.support.extra.el.expression.parse.impl;

import com.chua.common.support.extra.el.expression.node.CalculateNode;
import com.chua.common.support.extra.el.expression.node.impl.KeywordNode;
import com.chua.common.support.extra.el.expression.node.impl.VariableNode;
import com.chua.common.support.extra.el.expression.parse.Invoker;
import com.chua.common.support.extra.el.expression.token.KeyWord;
import com.chua.common.support.extra.el.expression.util.CharType;

import java.util.Deque;

public class IdentifierParser extends NodeParser
{

    @Override
    public int parse(String el, int offset, Deque<CalculateNode> nodes, int function, Invoker next)
    {
        if (CharType.isAlphabet(getChar(offset, el)) == false)
        {
            return next.parse(el, offset, nodes, function);
        }
        return parseIdentifier(el, offset, nodes);
    }

    private int parseIdentifier(String el, int offset, Deque<CalculateNode> nodes)
    {
        int  length = 0;
        char c;
        while (CharType.isAlphabet(c = getChar(length + offset, el)) || CharType.isDigital(c))
        {
            length++;
        }
        String literals = el.substring(offset, offset + length);
        offset += length;
        if (KeyWord.getKeyWord(literals) != null)
        {
            nodes.push(new KeywordNode(literals));
        }
        else
        {
            nodes.push(new VariableNode(literals));
        }
        return offset;
    }
}
