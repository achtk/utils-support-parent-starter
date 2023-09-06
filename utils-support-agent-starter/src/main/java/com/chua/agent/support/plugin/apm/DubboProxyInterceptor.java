package com.chua.agent.support.plugin.apm;

import com.chua.agent.support.span.NewTrackManager;
import com.chua.agent.support.span.Span;
import com.chua.agent.support.utils.ClassUtils;
import com.chua.agent.support.utils.NetAddress;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.*;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import static com.chua.agent.support.store.AgentStore.log;

/**
 * @author CH
 * @since 2021-08-27
 */
public class DubboProxyInterceptor implements Interceptor {
    @Override
    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder) {
        return builder.method(ElementMatchers.named("invokeWithContext")).intercept(MethodDelegation.to(DubboProxyInterceptor.class));
    }

    @Override
    public ElementMatcher<? super TypeDescription> type() {
        return ElementMatchers.named("org.apache.dubbo.rpc.cluster.support.AbstractClusterInvoker");
    }


    @RuntimeType
    public static Object before(@AllArguments Object[] objects, @Origin Method method, @This Object obj, @SuperCall Callable<?> callable) throws Exception {
        Span span = null;
        try {
            span = sendTrace(objects, obj);
        } catch (Throwable ignored) {
        }
        try {
            Object invoke = NewTrackManager.invoke(callable);
            return invoke;
        } finally {
            NewTrackManager.refreshCost(span);
        }
    }


    /**
     * 发送到链路
     * @param objects objects
     * @param obj 对象
     *
     * @return
     */
    private static Span sendTrace(Object[] objects, Object obj) {
        Span exitSpan = NewTrackManager.getLastSpan();
        if (null != exitSpan) {
            Object bean = objects[1];
            String className = ClassUtils.getObject(0, bean).toString();
            String methodName = ClassUtils.getObject("methodName", bean).toString();

            log(Level.INFO, exitSpan.getMessage());

            Span entrySpan = NewTrackManager.createEntrySpan();
            List<String> stackTraceElement = new LinkedList<>();
            stackTraceElement.add(className + "." + methodName);
            stackTraceElement.add("--------------参数-----------------");
            String[] parameterType = (String[]) ClassUtils.getObject("compatibleParamSignatures", bean);
            Object[] values = (Object[]) ClassUtils.getObject("arguments", bean);
            for (int i = 0, objectLength = parameterType.length; i < objectLength; i++) {
                Object o = values[i];
                stackTraceElement.add("(" + parameterType[i] + "): " + o);
            }

            StringBuffer center = new StringBuffer();
            stackTraceElement.add("--------------注册中心-----------------");
            stackTraceElement.addAll(createCenterMessage(obj, center));

            stackTraceElement.add("--------------信息-----------------");
            String current = createMessage(objects[0], stackTraceElement);

            entrySpan.setMessage("<span style='color: blue; font-size:1000;'><span class='badge badge-primary'>Dubbo("+ current +")</span>" + className + "." + methodName + "</span>");
            entrySpan.setHeader(stackTraceElement);
            entrySpan.setMethod(exitSpan.getMessage());
            entrySpan.setTypeMethod(entrySpan.getMessage());
            entrySpan.setType(exitSpan.getType());
            entrySpan.setDb(current);
            entrySpan.setFrom(className + "." + methodName);
            return entrySpan;
        }
        return null;
    }

    private static Collection<? extends String> createCenterMessage(Object obj, StringBuffer center) {
        List<String> rs = new LinkedList<>();
        try {
            Object o1 = ClassUtils.getObject(0, obj);
            Object url = ClassUtils.getObject("url", o1);
            NetAddress netAddress = NetAddress.of(url.toString());
            rs.add("注册协议: " + netAddress.getProtocol());
            rs.add("注册中心地址: " + netAddress.getAddress());
            center.append(netAddress.getAddress());

            netAddress.parametric().forEach((k, v) -> {
                rs.add(k + ": " + v);
            });
        } catch (Throwable ignored) {
        }

        return rs;
    }

    private static String createMessage(Object object, List<String> stackTraceElement) {
        String current = null;
        try {

            Object o1 = ClassUtils.getObject(0, object);
            Object o11 = ClassUtils.getObject(0, o1);
            Object url = ClassUtils.getObject(0, o11);
            String url1 = url.toString();
            stackTraceElement.add(url1);
            try {
                current = url1.substring(url1.indexOf("->") + 2).trim();
            } catch (Exception ignored) {
            }

        } catch (Exception ignored) {
        }
        return current;
    }
}
