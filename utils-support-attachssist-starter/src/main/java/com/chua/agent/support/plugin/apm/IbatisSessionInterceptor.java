package com.chua.agent.support.plugin.apm;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * @author CH
 * @since 2021-08-27
 */
public class IbatisSessionInterceptor implements Interceptor {
    @Override
    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder) {
        return builder.method(ElementMatchers.any().and(ElementMatchers.named("getConfiguration"))).intercept(MethodDelegation.to(SpringInterceptor.class));
    }

    @Override
    public ElementMatcher<? super TypeDescription> type() {
        return ElementMatchers.named("org.mybatis.spring.SqlSessionTemplate");
    }

}
