package com.chua.common.support.extra.el.template.parser.impl;

import com.chua.common.support.extra.el.exception.IllegalFormatException;
import com.chua.common.support.extra.el.expression.Expression;
import com.chua.common.support.extra.el.template.ScanMode;
import com.chua.common.support.extra.el.template.Template;
import com.chua.common.support.extra.el.template.execution.Execution;
import com.chua.common.support.extra.el.template.execution.impl.ExpressionExecution;
import com.chua.common.support.extra.el.template.parser.Invoker;
import com.chua.common.support.extra.el.template.parser.Parser;

import java.util.Deque;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_DOLLAR_CHAR;

/**
 * 基础类
 *
 * @author CH
 */
public class ExpressionParser extends Parser {

    @Override
    public int parse(String sentence, int offset, Deque<Execution> executions, Template template, StringBuilder cache, Invoker next) {
        if (template.getMode() != ScanMode.LITERALS) {
            return next.scan(sentence, offset, executions, template, cache);
        }
        if (getChar(offset, sentence) != SYMBOL_DOLLAR_CHAR || getChar(offset + 1, sentence) != '{') {
            return next.scan(sentence, offset, executions, template, cache);
        }
        extractLiterals(cache, executions);
        offset += 2;
        int start = offset;
        int length = sentence.length();
        while (getChar(offset, sentence) != '}' && offset < length) {
            offset++;
        }
        if (offset >= length) {
            throw new IllegalFormatException("语法错误，不是闭合的表达式", sentence.substring(0, start));
        }
        ExpressionExecution execution = new ExpressionExecution(Expression.parse(sentence.substring(start, offset)));
        executions.push(execution);
        return offset + 1;
    }
}
