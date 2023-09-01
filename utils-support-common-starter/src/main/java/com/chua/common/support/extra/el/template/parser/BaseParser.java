package com.chua.common.support.extra.el.template.parser;

import com.chua.common.support.extra.el.exception.IllegalFormatException;
import com.chua.common.support.extra.el.template.Template;
import com.chua.common.support.extra.el.template.execution.Execution;
import com.chua.common.support.extra.el.template.execution.impl.StringExecution;
import com.chua.common.support.utils.CharUtils;

import java.util.Deque;

/**
 * 解析器
 * 基础类
 *
 * @author CH
 * @date 2023/08/31
 */
public abstract class BaseParser {

    protected static final Execution[] EMPTY_BODY = new Execution[0];

    /**
     * execute
     *
     * @param sentence   sentence
     * @param offset     offset
     * @param executions executions
     * @param template   template
     * @param cache      cache
     * @param next       next
     * @return result
     */
    public abstract int parse(String sentence, int offset, Deque<Execution> executions, Template template, StringBuilder cache, Invoker next);

    /**
     * 查询{的位置，如果查询不到抛出异常。如果查询到，则返回{位置+1的结果
     *
     * @param sentence
     * @param offset
     * @return
     */
    protected int findMethodBodyBegin(String sentence, int offset) {
        offset = skipWhiteSpace(offset, sentence);
        if ('{' != getChar(offset, sentence)) {
            throw new IllegalFormatException("方法体没有以{开始", sentence.substring(0, offset));
        }
        offset++;
        return offset;
    }

    protected void extractLiterals(StringBuilder cache, Deque<Execution> executions) {
        if (cache.length() != 0) {
            Execution execution = new StringExecution(cache.toString());
            cache.setLength(0);
            executions.push(execution);
        }
    }

    protected char getChar(int offset, String sentence) {
        return offset >= sentence.length() ? (char) CharUtils.EOI : sentence.charAt(offset);
    }

    protected int skipWhiteSpace(int offset, String el) {
        while (CharUtils.isWhitespace(getChar(offset, el))) {
            offset++;
        }
        return offset;
    }

    protected boolean isExecutionBegin(int offset, String sentence) {
        char c1 = getChar(offset, sentence);
        char c2 = getChar(offset + 1, sentence);
        return c1 == '<' && c2 == '%';
    }

    /**
     * offset当前位置为'(',寻找与之配对的)结束符.返回寻找到)位置。如果找不到，则返回-1
     *
     * @param sentence
     * @param offset
     * @return
     */
    protected int findEndRightBracket(String sentence, int offset) {
        offset++;
        int length = sentence.length();
        int countForLeftBracket = 0;
        do {
            char c = getChar(offset, sentence);
            if (c == '(') {
                countForLeftBracket++;
            } else if (c == ')') {
                if (countForLeftBracket > 0) {
                    countForLeftBracket--;
                } else {
                    // 此时找到if的括号的封闭括号
                    break;
                }
            }
            offset++;
        } while (offset < length);
        if (offset >= length) {
            return -1;
        }
        return offset;
    }

    /**
     * 搜索执行语句的结尾，也就是%>所在位置。返回>的坐标。如果没有找到，返回-1
     *
     * @param startIndex
     * @param sentence
     * @return
     */
    protected int findExectionEnd(int startIndex, String sentence) {
        int offset = startIndex;
        int length = sentence.length();
        while (offset < length) {
            char c = getChar(offset, sentence);
            if (c == '%') {
                offset = skipWhiteSpace(offset + 1, sentence);
                c = getChar(offset, sentence);
                if (c == '>') {
                    return offset;
                }
            }
            offset++;
        }
        return -1;
    }
}