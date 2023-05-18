package com.chua.common.support.extra.el.template.parser;

import com.chua.common.support.extra.el.template.Template;
import com.chua.common.support.extra.el.template.execution.Execution;

import java.util.Deque;

public interface Invoker
{
    int scan(String sentence, int offset, Deque<Execution> executions, Template template, StringBuilder cache);
}
