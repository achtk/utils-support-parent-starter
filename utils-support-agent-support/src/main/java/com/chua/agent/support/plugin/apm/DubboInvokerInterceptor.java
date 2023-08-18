package com.chua.agent.support.plugin.apm;

import com.chua.agent.support.handler.DubboProxyInterceptorHandler;
import com.chua.agent.support.span.NewTrackManager;
import com.chua.agent.support.span.Span;
import com.chua.agent.support.span.TrackContext;
import com.chua.agent.support.utils.StringUtils;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.*;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.Callable;

import static com.chua.agent.support.plugin.apm.SpringInterceptor.getRequestLinkId;
import static com.chua.agent.support.plugin.apm.SpringInterceptor.getRequestLinkParentId;

/**
 * @author CH
 * @since 2021-08-27
 */
public class DubboInvokerInterceptor implements Interceptor {
    @Override
    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder) {
        return builder.method(ElementMatchers.named("invoke")).intercept(MethodDelegation.to(DubboInvokerInterceptor.class));
    }

    @Override
    public ElementMatcher<? super TypeDescription> type() {
        return ElementMatchers.named("org.apache.dubbo.rpc.proxy.AbstractProxyInvoker");
    }


    @RuntimeType
    public static Object before(@AllArguments Object[] objects, @Origin Method method, @This Object obj, @SuperCall Callable<?> callable) throws Exception {
        Span currentSpan = NewTrackManager.getCurrentSpan();
        if (null == currentSpan) {
            String linkId = getRequestLinkId(objects);

            if(null == linkId) {
                linkId = UUID.randomUUID().toString();
            }

            TrackContext.setLinkId(linkId);
        }

        Span entrySpan = NewTrackManager.createEntrySpan();
        String pId = getRequestLinkParentId(objects);
        if(!StringUtils.isNullOrEmpty(pId)) {
            entrySpan.setPid(pId);
        }
        Object call = callable.call();
        Interceptor.doRefreshSpan(method, objects, entrySpan);
        entrySpan.setFrom(null);
        DubboProxyInterceptorHandler.insertResponsePoint(call);
        return call;
    }



}
