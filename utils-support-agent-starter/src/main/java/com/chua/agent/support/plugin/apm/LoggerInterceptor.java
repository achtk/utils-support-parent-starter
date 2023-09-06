package com.chua.agent.support.plugin.apm;//package com.chua.tools.agent.plugin.apm;
//
//import Interceptor;
//import net.bytebuddy.description.method.MethodDescription;
//import net.bytebuddy.description.type.TypeDescription;
//import net.bytebuddy.dynamic.DynamicType;
//import net.bytebuddy.implementation.MethodDelegation;
//import net.bytebuddy.matcher.ElementMatcher;
//import net.bytebuddy.matcher.ElementMatchers;
//
///**
// * @author CH
// * @since 2021-08-25
// */
//public class LoggerInterceptor implements Interceptor {
//    @Override
//    public DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<?> transform(DynamicType.Builder<?> builder) {
//        return builder.method(ElementMatchers.isMethod().and(
//                ElementMatchers.<MethodDescription>named("trace")
//                        .or(ElementMatchers.<MethodDescription>named("debug"))
//                        .or(ElementMatchers.<MethodDescription>named("info"))
//                        .or(ElementMatchers.<MethodDescription>named("warn"))
//                        .or(ElementMatchers.<MethodDescription>named("error"))
//                        .or(ElementMatchers.<MethodDescription>named("fatal"))))
//                .intercept(MethodDelegation.to(SpringInterceptor.class));
//    }
//
//    @Override
//    public ElementMatcher<? super TypeDescription> type() {
//        return ElementMatchers.named("org.apache.log4j.Logger")
//                .or(ElementMatchers.named("org.apache.log4j.Category"))
//                .or(ElementMatchers.named("org.apache.logging.log4j.spi.AbstractLogger"))
//                .or(ElementMatchers.named("ch.qos.logback.classic.Logger"))
//                .or(ElementMatchers.named("org.slf4j.helpers.NOPLogger"))
//                .or(ElementMatchers.named("org.apache.logging.slf4j.Log4jLogger"));
//    }
//}
