package com.chua.common.support.extra.el.template.parser.impl;

import com.chua.common.support.extra.el.exception.IllegalFormatException;
import com.chua.common.support.extra.el.expression.Expression;
import com.chua.common.support.extra.el.template.ScanMode;
import com.chua.common.support.extra.el.template.Template;
import com.chua.common.support.extra.el.template.execution.Execution;
import com.chua.common.support.extra.el.template.execution.impl.ForEachExecution;
import com.chua.common.support.extra.el.template.parser.Invoker;
import com.chua.common.support.extra.el.template.parser.BaseParser;
import com.chua.common.support.utils.CharUtils;

import java.util.Deque;

import static com.chua.common.support.constant.CommonConstant.*;

/**
 * 基础类
 *
 * @author CH
 */
public class ForEachParser extends BaseParser {

    @Override
    public int parse(String sentence, int offset, Deque<Execution> executions, Template template, StringBuilder cache, Invoker next) {
        if (template.getMode() != ScanMode.EXECUTION) {
            return next.scan(sentence, offset, executions, template, cache);
        }
        int origin = offset;
        offset = skipWhiteSpace(offset, sentence);
        if (getChar(offset, sentence) != LETTER_LOWERCASE_F
                || getChar(offset + 1, sentence) != 'o'
                || getChar(offset + 2, sentence) != 'r'
        ) {
            return next.scan(sentence, origin, executions, template, cache);
        }
        offset = skipWhiteSpace(offset + 3, sentence);
        if (getChar(offset, sentence) != SYMBOL_LEFT_BRACKETS_CHAR) {
            throw new IllegalFormatException("for循环没有以(开始条件语句", sentence.substring(0, offset));
        }
        offset = skipWhiteSpace(offset + 1, sentence);
        int start = offset;
        while (CharUtils.isAlphabet(getChar(offset, sentence)) && offset < sentence.length()) {
            offset++;
        }
        if (offset >= sentence.length()) {
            throw new IllegalFormatException("for循环中的变量命名没有结束", sentence.substring(0, start));
        }
        String itemName = sentence.substring(start, offset);
        if (getChar(offset, sentence) != SYMBOL_SPACE_CHAR) {
            throw new IllegalFormatException("for循环语法错误", sentence.substring(0, start));
        }
        offset = skipWhiteSpace(offset + 1, sentence);
        if (getChar(offset, sentence) != LETTER_LOWERCASE_I || getChar(offset + 1, sentence) != LETTER_LOWERCASE_N) {
            throw new IllegalFormatException("for循环语法错误,缺少IN", sentence.substring(0, start));
        }
        offset = skipWhiteSpace(offset + 2, sentence);
        start = offset;
        offset = findEndRightBracket(sentence, offset);
        if (offset == -1) {
            throw new IllegalFormatException("for循环语法错误,缺少集合", sentence.substring(0, start));
        }
        ForEachExecution execution = new ForEachExecution(itemName, Expression.parse(sentence.substring(start, offset)));
        executions.push(execution);
        offset++;
        offset = findMethodBodyBegin(sentence, offset);
        return offset;
    }
}
