package com.chua.common.support.extra.el.expression.parse.impl;

import com.chua.common.support.extra.el.expression.node.CalculateNode;
import com.chua.common.support.extra.el.expression.parse.Invoker;
import com.chua.common.support.utils.CharUtils;

import java.util.Deque;

/**
 * 节点解析器
 * 基础类
 *
 * @author CH
 * @date 2023/08/31
 */
public abstract class BaseNodeParser {

    /**
     * 在解析节点后返回新的偏移量
     *
     * @param el       节点
     * @param offset   便宜
     * @param nodes    队列
     * @param function function
     * @param next     下个执行器
     * @return 偏移量
     */
    public abstract int parse(String el, int offset, Deque<CalculateNode> nodes, int function, Invoker next);

    /**
     * 字符
     *
     * @param offset   抵消
     * @param sentence 句子
     * @return char
     */
    protected char getChar(int offset, String sentence) {
        return offset >= sentence.length() ? (char) CharUtils.EOI : sentence.charAt(offset);
    }

    /**
     * 跳过空格
     *
     * @param offset 抵消
     * @param el     埃尔
     * @return int
     */
    protected int skipWhiteSpace(int offset, String el) {
        while (CharUtils.isWhitespace(getChar(offset, el))) {
            offset++;
        }
        return offset;
    }

    /**
     * 得到标识符
     *
     * @param offset 抵消
     * @param el     埃尔
     * @return int
     */
    protected int getIdentifier(int offset, String el) {
        int length = 0;
        char c;
        while (CharUtils.isAlphabet(c = getChar(length + offset, el)) || CharUtils.isDigital(c)) {
            length++;
        }
        return length + offset;
    }
}
