package com.chua.agent.support.plugin.apm;

import com.chua.agent.support.span.span.Span;
import com.chua.agent.support.trace.NewTrackManager;
import com.chua.agent.support.utils.ClassUtils;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.*;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * @author CH
 * @link org.springframework.web.servlet.DispatcherServlet
 */
public class DispatcherInterceptor implements Interceptor {
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
        Span span = null;
        try {
            span = sendTrace(method, objects);
        } catch (Exception ignored) {
        }
        try {
            return callable.call();
        } finally {
            NewTrackManager.refreshCost(span);
        }
    }

    /**
     * 发送到链路
     *
     * @param method method
     * @param args   args
     */
    private static Span sendTrace(Method method, Object[] args) {
        Span lastSpan = NewTrackManager.createEntrySpan();
        Object httpServletRequest = args[0];

        Interceptor.doRefreshSpan(method, args, lastSpan);
//        NewTrackManager.registerSpan(lastSpan);


        List<String> stack = new LinkedList<>();
        Enumeration<String> headerNames = (Enumeration<String>) ClassUtils.invoke("getHeaderNames", httpServletRequest);
        while (headerNames.hasMoreElements()) {
            String element = headerNames.nextElement();
            stack.add(element + ":" + ClassUtils.invoke("getHeader", httpServletRequest, element));
        }

        String requestURI = ClassUtils.invoke("getMethod", httpServletRequest) + " "
                + ClassUtils.invoke("getServerName", httpServletRequest) + ":"
                + ClassUtils.invoke("getServerPort", httpServletRequest)
                + ClassUtils.invoke("getContextPath", httpServletRequest)
                + ClassUtils.invoke("getRequestURI", httpServletRequest);
        Span sql1 = new Span();
        sql1.setLinkId(lastSpan.getLinkId());
        sql1.setPid(lastSpan.getId());
        sql1.setEnterTime(new Date());
        sql1.setId(UUID.randomUUID().toString());
        sql1.setMessage("<span style='color: red; font-size:1000;'>" + requestURI + "</span>");
        sql1.setHeader(stack);
        sql1.setMethod(sql1.getMessage());
        sql1.setTypeMethod(requestURI);
        sql1.setType(method.getDeclaringClass().getTypeName());
        NewTrackManager.registerSpan(sql1);
        return lastSpan;
    }

    @Override
    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder) {
        return builder.method(ElementMatchers.named("doService"))
                .intercept(MethodDelegation.to(DispatcherInterceptor.class));
    }

    @Override
    public ElementMatcher<? super TypeDescription> type() {
        return ElementMatchers.hasSuperType(ElementMatchers.named("org.springframework.web.servlet.DispatcherServlet"));
    }
}
