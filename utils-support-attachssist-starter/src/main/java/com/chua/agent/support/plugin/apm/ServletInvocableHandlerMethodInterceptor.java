package com.chua.agent.support.plugin.apm;

import com.chua.agent.support.span.span.Span;
import com.chua.agent.support.trace.NewTrackManager;
import com.chua.agent.support.utils.StringUtils;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.*;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Method;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static com.chua.agent.support.plugin.apm.SpringInterceptor.sendRemoteSpan;

/**
 * @author CH
 * @link org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod
 */
public class ServletInvocableHandlerMethodInterceptor implements Interceptor {
    /**
     * 将返回值转换成具体的方法返回值类型,加了这个注解 intercept 方法才会被执行
     *
     * @param target   目标
     * @param method   方法
     * @param objects  参数
     * @param delegate 目标对象的一个代理
     * @param callable 方法的调用者对象
     * @return 结果
     * @throws Exception ex
     */
    @RuntimeType
    public static Object intercept(
            // 被拦截的目标对象 （动态生成的目标对象）
            @This Object target,
            // 正在执行的方法Method 对象（目标对象父类的Method）
            @Origin Method method,
            // 正在执行的方法的全部参数
            @AllArguments Object[] objects,
            // 目标对象的一个代理
            @Super Object delegate,
            // 方法的调用者对象 对原始方法的调用依靠它
            @SuperCall Callable<?> callable) throws Exception {
        try {
            return callable.call();
        } finally {
            Stack<Span> result = NewTrackManager.currentSpans();
            sendRemoteSpan(result.stream().filter(it -> !StringUtils.isNullOrEmpty(it.getTypeMethod())).collect(Collectors.toList()), objects);
        }
    }

    @Override
    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder) {
        return builder.method(ElementMatchers.named("invokeForRequest"))
                .intercept(MethodDelegation.to(ServletInvocableHandlerMethodInterceptor.class));
    }

    @Override
    public ElementMatcher<? super TypeDescription> type() {
        return ElementMatchers.named("org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod");
    }
}
