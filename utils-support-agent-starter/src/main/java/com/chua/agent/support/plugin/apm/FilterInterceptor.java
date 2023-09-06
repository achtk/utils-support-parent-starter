package com.chua.agent.support.plugin.apm;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * 过滤器
 *
 * @author CH
 */
public class FilterInterceptor implements Interceptor {
    @Override
    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder) {
        return builder.method(ElementMatchers.isMethod()
                .and(
                        ElementMatchers.<MethodDescription>nameStartsWith("do")
                )
        ).intercept(MethodDelegation.to(SpringInterceptor.class));
    }

    @Override
    public ElementMatcher<? super TypeDescription> type() {
        return ElementMatchers.hasSuperType(
                        ElementMatchers.named("javax.servlet.Filter")
                )
                .and(ElementMatchers.not(ElementMatchers.nameStartsWith("javax.servlet.http")))
                .and(ElementMatchers.not(ElementMatchers.named("org.apache.tomcat.websocket.server.WsFilter")))
                .and(ElementMatchers.not(ElementMatchers.nameStartsWith("org.springframework")));
    }
}
