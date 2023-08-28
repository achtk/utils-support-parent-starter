package com.chua.common.support.extra.el.expression.parse;

import com.chua.common.support.extra.el.expression.node.CalculateNode;

import java.util.Deque;

/**
 * 基础类
 *
 * @author CH
 */
public interface Invoker {
    int parse(String el, int offset, Deque<CalculateNode> nodes, int function);
}
