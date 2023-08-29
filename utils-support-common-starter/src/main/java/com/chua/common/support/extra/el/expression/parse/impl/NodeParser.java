package com.chua.common.support.extra.el.expression.parse.impl;

import com.chua.common.support.extra.el.expression.node.CalculateNode;
import com.chua.common.support.extra.el.expression.parse.Invoker;
import com.chua.common.support.lang.tokenizer.jieba.huaban.analysis.jieba.CharacterUtil;
import com.chua.common.support.utils.CharUtils;

import java.util.Deque;

/**
 * 基础类
 *
 * @author CH
 */
public abstract class NodeParser {

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

    protected char getChar(int offset, String sentence) {
        return offset >= sentence.length() ? (char) CharUtils.EOI : sentence.charAt(offset);
    }

    protected int skipWhiteSpace(int offset, String el) {
        while (CharUtils.isWhitespace(getChar(offset, el))) {
            offset++;
        }
        return offset;
    }

    protected int getIdentifier(int offset, String el) {
        int length = 0;
        char c;
        while (CharUtils.isAlphabet(c = getChar(length + offset, el)) || CharUtils.isDigital(c)) {
            length++;
        }
        return length + offset;
    }
}
