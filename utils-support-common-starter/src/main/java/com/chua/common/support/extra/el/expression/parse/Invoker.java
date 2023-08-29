package com.chua.common.support.extra.el.expression.parse;

import com.chua.common.support.extra.el.expression.node.CalculateNode;

import java.util.Deque;

/**
 * 基础类
 *
 * @author CH
 */
public interface Invoker {
    /**
     * 執行
     *
     * @param el       节点
     * @param offset   位置
     * @param nodes    元素
     * @param function 回调
     * @return 结果
     */
    int parse(String el, int offset, Deque<CalculateNode> nodes, int function);
}
