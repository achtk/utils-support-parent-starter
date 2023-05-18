package com.chua.agent.support.plugin.apm;

import com.chua.agent.support.Agent;
import com.chua.agent.support.handler.ApacheHttpInterceptorHandler;
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
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.logging.Level;

/**
 * apache http client
 * @author CH
 */
public class ApacheHttpInterceptor  implements Interceptor{
    @Override
    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder) {
        return builder.method(ElementMatchers.named("doReceiveResponse").or(ElementMatchers.named("execute"))).intercept(MethodDelegation.to(ApacheHttpInterceptor.class));
    }

    @Override
    public ElementMatcher<? super TypeDescription> type() {
        return ElementMatchers.named("org.apache.http.protocol.HttpRequestExecutor");
    }

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
        String name = method.getName();
        if (name.toLowerCase().contains("execute")) {
            try {
                ApacheHttpInterceptorHandler.sendTrace(null, target, method, objects);
            } catch (Exception ignored) {
            }
        }
        Object call;
        try {
            call = callable.call();
            if (!name.toLowerCase().contains("execute")) {
                try {
                    ApacheHttpInterceptorHandler.sendTrace(call, target, method, objects);
                } catch (Exception ignored) {
                }
            }
        } finally {
            NewTrackManager.refreshCost(NewTrackManager.getLastSpan());
        }
        return call;
    }

    private static void sendTrace(Object call, Object target, Method method, Object[] objects) {
        Span exitSpan = NewTrackManager.getLastSpan();
        if (null != exitSpan) {
            Object request = objects[0];
            Object methodName = ClassUtils.getObject("method", request);
            Object uri = ClassUtils.getObject("uri", request);
            Object target1 = ClassUtils.getObject("target", request);
            Agent.log(Level.INFO, exitSpan.getMessage());

            List<String> stack = new LinkedList<>();
            stack.add("" + target1 + uri);
            stack.add("<strong class=\"node-details__name collapse-handle\">Request headers</strong>");
            try {
                List headers = (List) ClassUtils.getObject(0, ClassUtils.getObject("headergroup", request));
                for (Object header : headers) {
                    stack.add(header.toString());
                }
            } catch (Exception ignored) {
            }
            stack.add("<strong class=\"node-details__name collapse-handle\">Request body</strong>");
            try {
                stack.add(new String((byte[]) ClassUtils.getObject(0, ClassUtils.getObject("entity", request))));
            } catch (Exception ignored) {
            }
            stack.add("<strong class=\"node-details__name collapse-handle\">Response headers</strong>");
            String s = call.toString();
            for (String s1 : s.split(",")) {
                stack.add(s1.trim().replace("[", "").replace("]", ""));
            }

            Span sql1 = new Span();
            sql1.setLinkId(exitSpan.getLinkId());
            sql1.setPid(exitSpan.getPid());
            sql1.setEnterTime(new Date());
            sql1.setId(UUID.randomUUID().toString());
            sql1.setMessage(methodName + " " + target1 + uri);
            sql1.setMethod(method.getName());
            sql1.setTypeMethod(target.getClass().getTypeName() + "." + sql1.getMethod());
            sql1.setType(target.getClass().getTypeName());
            sql1.setFrom(sql1.getTypeMethod());
            NewTrackManager.registerSpan(sql1);

            Span sql2 = new Span();
            sql2.setLinkId(exitSpan.getLinkId());
            sql2.setPid(sql1.getId());
            sql2.setEnterTime(new Date());
            sql2.setId(UUID.randomUUID().toString());
            sql2.setTypeMethod("<span>" + methodName + " " + target1 + uri + "</span>");
            sql2.setHeader(stack);
            sql2.setMethod(method.getName());
            sql2.setType(target.getClass().getTypeName());
            sql2.setFrom(sql1.getTypeMethod());
            NewTrackManager.registerSpan(sql2);
        }
    }
}

