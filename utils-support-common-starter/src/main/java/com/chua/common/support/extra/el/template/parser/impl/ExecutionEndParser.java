package com.chua.common.support.extra.el.template.parser.impl;

import com.chua.common.support.extra.el.template.ScanMode;
import com.chua.common.support.extra.el.template.Template;
import com.chua.common.support.extra.el.template.execution.Execution;
import com.chua.common.support.extra.el.template.parser.Invoker;
import com.chua.common.support.extra.el.template.parser.BaseParser;

import java.util.Deque;

/**
 * 基础类
 *
 * @author CH
 */
public class ExecutionEndParser extends BaseParser {

    @Override
    public int parse(String sentence, int offset, Deque<Execution> executions, Template template, StringBuilder cache, Invoker next) {
        if (template.getMode() != ScanMode.EXECUTION 
                || '%' != getChar(offset, sentence) 
                || '>' != getChar(offset + 1, sentence)) {
            return next.scan(sentence, offset, executions, template, cache);
        }
        template.setMode(ScanMode.LITERALS);
        offset += 2;
        return offset;
    }
}
