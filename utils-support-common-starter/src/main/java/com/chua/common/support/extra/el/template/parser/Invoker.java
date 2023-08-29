package com.chua.common.support.extra.el.template.parser;

import com.chua.common.support.extra.el.template.Template;
import com.chua.common.support.extra.el.template.execution.Execution;

import java.util.Deque;

/**
 * 基础类
 *
 * @author CH
 */
public interface Invoker {
    /**
     * 执行
     *
     * @param sentence   sentence
     * @param offset     offset
     * @param executions executions
     * @param template   template
     * @param cache      cache
     * @return result
     */
    int scan(String sentence, int offset, Deque<Execution> executions, Template template, StringBuilder cache);
}
