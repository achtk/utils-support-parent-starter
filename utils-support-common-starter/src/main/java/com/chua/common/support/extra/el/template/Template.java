package com.chua.common.support.extra.el.template;

import com.chua.common.support.extra.el.exception.IllegalFormatException;
import com.chua.common.support.extra.el.template.execution.Execution;
import com.chua.common.support.extra.el.template.execution.impl.StringExecution;
import com.chua.common.support.extra.el.template.parser.Invoker;
import com.chua.common.support.extra.el.template.parser.Parser;
import com.chua.common.support.extra.el.template.parser.impl.*;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
/**
 * 基础类
 * @author CH
 */
public class Template
{
    private static final ThreadLocal<StringBuilder> LOCAL = new ThreadLocal<StringBuilder>()
    {
        @Override
        protected StringBuilder initialValue()
        {
            return new StringBuilder();
        }
    };
    private static final Invoker                    DEFAULT_HEAD;

    static
    {
        Parser[] parsers = new Parser[]{ 
                new ExecutionBeginParser(), 
                new ExecutionEndParser(), 
                new IfParser(), 
                new ElseParser(), 
                new ForEachParser(), 
                new EndBraceParser(), 
                new ExpressionParser(), 
                new LiteralsParser(), 
        };
        Invoker pred = new Invoker()
        {

            @Override
            public int scan(String sentence, int offset, Deque<Execution> executions, Template template, StringBuilder cache)
            {
                return offset;
            }
        };
        for (int i = parsers.length - 1; i > -1; i--)
        {
            final Parser  parser = parsers[i];
            final Invoker next   = pred;
            Invoker invoker = new Invoker()
            {

                @Override
                public int scan(String sentence, int offset, Deque<Execution> executions, Template template, StringBuilder cache)
                {
                    return parser.parse(sentence, offset, executions, template, cache, next);
                }
            };
            pred = invoker;
        }
        DEFAULT_HEAD = pred;
    }

    private final Execution[]      runtimeExecutions;
    private final Invoker          head       = DEFAULT_HEAD;
    private       Deque<Execution> executions = new LinkedList<Execution>();
    private       ScanMode         mode       = ScanMode.LITERALS;

    private Template(String sentence)
    {
        StringBuilder cache  = new StringBuilder();
        int           offset = 0;
        int           length = sentence.length();
        mode = ScanMode.LITERALS;
        while (offset < length)
        {
            int result = head.scan(sentence, offset, executions, this, cache);
            if (result == offset)
            {
                throw new IllegalFormatException("没有解析器可以识别", sentence.substring(0, offset));
            }
            offset = result;
        }
        if (cache.length() != 0)
        {
            Execution execution = new StringExecution(cache.toString());
            executions.push(execution);
        }
        Deque<Execution> array = new LinkedList<Execution>();
        while (executions.isEmpty() == false)
        {
            array.push(executions.pollFirst());
        }
        runtimeExecutions = array.toArray(new Execution[0]);
        executions = null;
        mode = null;
    }

    public static Template parse(String sentence)
    {
        return new Template(sentence);
    }

    public ScanMode getMode()
    {
        return mode;
    }

    public void setMode(ScanMode mode)
    {
        this.mode = mode;
    }

    public String render(Map<String, Object> variables)
    {
        StringBuilder cache = LOCAL.get();
        for (Execution execution : runtimeExecutions)
        {
            execution.execute(variables, cache);
        }
        String result = cache.toString();
        cache.setLength(0);
        return result;
    }
}
