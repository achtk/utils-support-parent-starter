package com.chua.agent.support.plugin.apm;

import com.chua.agent.support.handler.DubboProxyInterceptorHandler;
import com.chua.agent.support.trace.NewTrackManager;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.*;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * @author CH
 * @since 2021-08-27
 */
public class DubboContextProxyInterceptor implements Interceptor {
    @Override
    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder) {
        return builder.method(ElementMatchers.named("doInvokeAndReturn")
                    .or(ElementMatchers.named("waitForResultIfSync")))
                .intercept(MethodDelegation.to(DubboContextProxyInterceptor.class));
    }

    @Override
    public ElementMatcher<? super TypeDescription> type() {
        return ElementMatchers.named("org.apache.dubbo.rpc.protocol.AbstractInvoker");
    }


    @RuntimeType
    public static Object before(@AllArguments Object[] args, @Origin Method method, @This Object obj, @SuperCall Callable<?> callable) throws Exception {
        DubboProxyInterceptorHandler.insertPoint(NewTrackManager.getLastSpan(), args);
        Object invoke = NewTrackManager.invoke(callable);
        if("waitForResultIfSync".equals(method.getName())) {
            DubboProxyInterceptorHandler.receivePoint(args[0]);
        }
        return invoke;
    }

}
