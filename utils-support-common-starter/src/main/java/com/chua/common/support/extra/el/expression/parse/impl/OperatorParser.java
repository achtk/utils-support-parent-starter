package com.chua.common.support.extra.el.expression.parse.impl;

import com.chua.common.support.extra.el.expression.node.CalculateNode;
import com.chua.common.support.extra.el.expression.node.impl.OperatorNode;
import com.chua.common.support.extra.el.expression.parse.Invoker;
import com.chua.common.support.extra.el.expression.token.Operator;
import com.chua.common.support.utils.CharUtils;

import java.util.Deque;
/**
 * 基础类
 * @author CH
 */
public class OperatorParser extends BaseNodeParser {

    @Override
    public int parse(String el, int offset, Deque<CalculateNode> nodes, int function, Invoker next) {
        if (!CharUtils.isOperator(getChar(offset, el))) {
            return next.parse(el, offset, nodes, function);
        }
        String literals = new String(new char[]{getChar(offset, el), getChar(offset + 1, el)});
        if (Operator.literalsOf(literals) != null) {
            nodes.push(new OperatorNode(Operator.literalsOf(literals)));
            offset += 2;
            return offset;
        }
        literals = String.valueOf(getChar(offset, el));
        if (Operator.literalsOf(literals) != null) {
            nodes.push(new OperatorNode(Operator.literalsOf(literals)));
            offset += 1;
            return offset;
        }
        throw new IllegalArgumentException("无法识别:" + literals + "检查:" + el.substring(0, offset));
    }
}
