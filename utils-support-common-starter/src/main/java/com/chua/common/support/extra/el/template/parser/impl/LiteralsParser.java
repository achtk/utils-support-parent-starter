package com.chua.common.support.extra.el.template.parser.impl;

import com.chua.common.support.extra.el.template.ScanMode;
import com.chua.common.support.extra.el.template.Template;
import com.chua.common.support.extra.el.template.execution.Execution;
import com.chua.common.support.extra.el.template.parser.Invoker;
import com.chua.common.support.extra.el.template.parser.Parser;

import java.util.Deque;

public class LiteralsParser extends Parser
{

    @Override
    public int parse(String sentence, int offset, Deque<Execution> executions, Template template, StringBuilder cache, Invoker next)
    {
        if (template.getMode() != ScanMode.LITERALS)
        {
            offset = skipWhiteSpace(offset, sentence);
            return offset;
        }
        cache.append(getChar(offset, sentence));
        return offset + 1;
    }
}