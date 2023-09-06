package com.chua.agent.support.plugin.apm;

import com.chua.agent.support.formatter.DmlFormatter;
import com.chua.agent.support.span.NewTrackManager;
import com.chua.agent.support.span.Span;
import com.chua.agent.support.utils.ClassUtils;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.*;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * @author CH
 * @since 2021-08-27
 */
public class IbatisSessionInterceptor implements Interceptor {
    @Override
    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder) {
        return builder.method(ElementMatchers.any().and(ElementMatchers.named("prepareStatement"))).intercept(MethodDelegation.to(IbatisSessionInterceptor.class));
    }

    @Override
    public ElementMatcher<? super TypeDescription> type() {
        return ElementMatchers.named("org.apache.ibatis.executor.SimpleExecutor");
    }

    @RuntimeType
    public static Object before(@AllArguments Object[] objects, @Origin Method method, @This Object obj, @SuperCall Callable<?> callable) throws Exception {
        Object call = callable.call();
        try {
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(call);
            String statement = ClassUtils.getObject("statement", invocationHandler).toString();
            sendTrace(method, statement);
        } catch (Throwable ignored) {
        }
        return call;
    }

    private static void sendTrace(Method method, String statement) {
        String sql = statement.split(":")[1];
        sendTrace(null, method, sql, null);
    }

    /**
     * 发送到链路
     *
     * @param currentDb
     * @param method    method
     * @param sql       sql
     * @param address
     */
    private static void sendTrace(String currentDb, Method method, String sql, String address) {
        Span lastSpan = NewTrackManager.getLastSpan();
        if (null != lastSpan) {
            Interceptor.doRefreshSpan(method, new Object[0], lastSpan);

            String format = new DmlFormatter().format(sql);

            List<String> stack = new LinkedList<>();
            stack.add(format);
            Span sql1 = new Span();
            sql1.setLinkId(lastSpan.getLinkId());
            sql1.setPid(lastSpan.getId());
            sql1.setEnterTime(new Date());
            sql1.setDb(address);
            sql1.setId(UUID.randomUUID().toString());
            sql1.setMessage(sql);
            sql1.setHeader(stack);
            sql1.setMethod(method.getName());
            sql1.setTypeMethod("<span class='badge badge-primary' >" + currentDb + "</span>" + sql1.getMessage());
            sql1.setType(lastSpan.getType());
            sql1.setError("jdbcmysql");
            sql1.setModel("sql");
            sql1.setFrom(sql);
            sql1.setCostTime(lastSpan.getCostTime());
            sql1.setParents(Collections.singleton(lastSpan.getTypeMethod()));
            NewTrackManager.registerSpan(sql1);
        }
    }
}
