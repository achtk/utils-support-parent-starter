package com.chua.agent.support.pointer;

import com.chua.agent.support.span.span.Span;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 服务点
 */
public interface ServerPoint {
    /**
     * 解析参数
     *
     * @param objects 参数
     * @param method  方法
     * @param obj     对象
     * @param span    结果
     */
    List<Span> doAnalysis(Object[] objects, Method method, Object obj, Span span);

    /**
     * 拦截类
     *
     * @return 类
     */
    String[] filterType();

    /**
     * 拦截方法
     *
     * @return 方法
     */
    String[] filterMethod();
}
