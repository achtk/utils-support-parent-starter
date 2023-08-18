package com.chua.agent.support.plugin.apm;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * @author CH
 */
public class HttpClient3xInterceptor implements Interceptor {
    @Override
    public DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<?> transform(DynamicType.Builder<?> builder) {
        return builder.method(ElementMatchers.isMethod().and(
                        ElementMatchers.named("executeMethod")
                                .and(ElementMatchers.takesArgument(0, ElementMatchers.named("org.apache.commons.httpclient.HostConfiguration")))
                                .and(ElementMatchers.takesArgument(1, ElementMatchers.named("org.apache.commons.httpclient.HttpMethod")))
                                .and(ElementMatchers.takesArgument(2, ElementMatchers.named("org.apache.commons.httpclient.HttpState")))
                )
        ).intercept(MethodDelegation.to(ApacheHttpInterceptor.class));
    }

    @Override
    public ElementMatcher<? super TypeDescription> type() {
        return ElementMatchers.hasSuperType(ElementMatchers.named("org.apache.commons.httpclient.HttpClient"));
    }
}
