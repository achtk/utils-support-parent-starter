package com.chua.common.support.extra.el.expression.parse.impl;

import com.chua.common.support.extra.el.expression.node.CalculateNode;
import com.chua.common.support.extra.el.expression.node.MethodNode;
import com.chua.common.support.extra.el.expression.node.impl.CompileObjectMethodNode;
import com.chua.common.support.extra.el.expression.node.impl.ReflectMethodNode;
import com.chua.common.support.extra.el.expression.node.impl.StaticObjectMethodNode;
import com.chua.common.support.extra.el.expression.parse.Invoker;
import com.chua.common.support.extra.el.expression.token.ValueResult;
import com.chua.common.support.extra.el.expression.util.CharType;
import com.chua.common.support.extra.el.expression.util.Functions;

import java.util.Deque;

public class MethodParser extends NodeParser
{

    @Override
    public int parse(String el, int offset, Deque<CalculateNode> nodes, int function, Invoker next)
    {
        if ('.' != getChar(offset, el))
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
        // 该情况意味着是属性
        if (c != '(')
        {
            return next.parse(el, offset, nodes, function);
        }
        String        literals = el.substring(origin + 1, offset);
        CalculateNode beanNode = nodes.pop();
        MethodNode    methodNode;
        if (beanNode.token() == ValueResult.TYPE)
        {
            methodNode = new StaticObjectMethodNode(literals, beanNode);
        }
        else
        {
            if (Functions.isMethodInvokeByCompile(function))
            {
                methodNode = new CompileObjectMethodNode(literals, beanNode, Functions.isRecognizeEveryTime(function));
            }
            else
            {
                methodNode = new ReflectMethodNode(literals, beanNode, Functions.isRecognizeEveryTime(function));
            }
        }
        nodes.push(methodNode);
        // 当前位置是(，所以位置+1
        offset += 1;
        return offset;
    }
}
