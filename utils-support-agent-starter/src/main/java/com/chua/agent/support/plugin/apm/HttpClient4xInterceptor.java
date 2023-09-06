package com.chua.agent.support.plugin.apm;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * @author CH
 */
public class HttpClient4xInterceptor implements Interceptor {
    @Override
    public DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<?> transform(DynamicType.Builder<?> builder) {
        return builder.method(ElementMatchers.isMethod().and(ElementMatchers.<MethodDescription>named("doExecute"))).intercept(MethodDelegation.to(ApacheHttpInterceptor.class));
    }

    @Override
    public ElementMatcher<? super TypeDescription> type() {
        return ElementMatchers.hasSuperType(ElementMatchers.named("org.apache.http.impl.client.CloseableHttpClient"))
                .and(ElementMatchers.not(ElementMatchers.<TypeDescription>isInterface()));
    }
}
