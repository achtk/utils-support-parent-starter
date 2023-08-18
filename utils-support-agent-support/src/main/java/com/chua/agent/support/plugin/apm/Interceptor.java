package com.chua.agent.support.plugin.apm;

import com.chua.agent.support.span.Span;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatcher;

import java.lang.reflect.Method;
import java.util.logging.Level;

import static com.chua.agent.support.store.AgentStore.log;

/**
 * 拦截器
 *
 * @author CH
 */
public interface Interceptor {

    /**
     * 编译器
     *
     * @param builder 编译器
     */
    DynamicType.Builder<?> transform(DynamicType.Builder<?> builder);

    /**
     * 类型
     *
     * @return 类型
     */
    ElementMatcher<? super TypeDescription> type();

    /**
     * 链路
     *
     * @param method   方法
     * @param args     参数
     * @param lastSpan 链路
     */
    static void doRefreshSpan(Method method, Object[] args, Span lastSpan) {
        String className = method.getDeclaringClass().getName();
        String methodName = method.getName();
        lastSpan.setFrom(className + "." + methodName);
        lastSpan.setMessage("链路追踪(MQ)：" + lastSpan.getLinkId() + " " + className + "." + methodName + " 耗时：" + lastSpan.getCostTime() + "ms");
        lastSpan.setStack(Thread.currentThread().getStackTrace());
        lastSpan.setTypeMethod(className + "." + methodName);
        lastSpan.setMethod(method.getDeclaringClass().getSimpleName() + "." + methodName);
        lastSpan.setType(className);
        log(Level.INFO, lastSpan.getMessage());
    }
}
