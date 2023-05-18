package com.chua.agent.support.plugin;

import com.chua.agent.support.span.span.Span;
import com.chua.agent.support.trace.NewTrackManager;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatcher;

import java.util.Collections;
import java.util.Date;
import java.util.UUID;

/**
 * 异常
 *
 * @author CH
 */
public class ExceptionAgentPlugin implements AgentPlugin {
    @Override
    public String name() {
        return "exception";
    }

    //    @Override
//    public void setAddress(String address) {
//
//    }
//
//    @Override
//    public void setParameter(JSONObject parameter) {
//
//    }
//
//    @Override
//    public DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<?> transform(DynamicType.Builder<?> builder) {
//        return builder.constructor(ElementMatchers.any()).intercept(Advice.to(ExceptionAgentPlugin.class));
//    }
//
//    @Override
//    public ElementMatcher<? super TypeDescription> type() {
//        return ElementMatchers.hasSuperType(ElementMatchers.named("java.lang.Exception"));
//    }
//
//    /**
//     * 将返回值转换成具体的方法返回值类型,加了这个注解 intercept 方法才会被执行
//     *
//     * @param target   目标
//     * @param method   方法
//     * @param objects  参数
//     * @param delegate 目标对象的一个代理
//     * @param callable 方法的调用者对象
//     * @return 结果
//     * @throws Exception ex
//     */
//    //@RuntimeType
//    public static Object intercept(
//            // 被拦截的目标对象 （动态生成的目标对象）
//            @This Object target,
//            // 正在执行的方法Method 对象（目标对象父类的Method）
//            @Origin Method method,
//            // 正在执行的方法的全部参数
//            @AllArguments Object[] objects,
//            // 目标对象的一个代理
//            @Super Object delegate,
//            // 方法的调用者对象 对原始方法的调用依靠它
//            @SuperCall Callable<?> callable) throws Exception {
//        return callable.call();
//    }
//
    //@RuntimeType
    public static void register(Object bean) {
        if (bean instanceof ClassNotFoundException) {
            return;
        }
        Span span = NewTrackManager.getLastSpan();

        if (null == span) {
            return;
        }

        Exception exception = (Exception) bean;
        if(exception.getClass().getTypeName().equalsIgnoreCase(span.getType())) {
            return;
        }


        if(exception instanceof ArrayIndexOutOfBoundsException) {
            return;
        }

        String s1 = exception.toString();
        if (s1.contains("xrebel")) {
            return;
        }

        if (s1.contains("java.lang.NoSuch")) {
            return;
        }


        if (s1.contains("javax.") || s1.contains("java.security.SignatureException")) {
            return;
        }


        Span sql1 = new Span();
        sql1.setLinkId(span.getLinkId());
        sql1.setPid(span.getId());
        sql1.setEnterTime(new Date());
        sql1.setId(UUID.randomUUID().toString());
        sql1.setMessage(exception.toString());
        sql1.setMethod(sql1.getMessage());
        sql1.setHeader(Collections.singletonList(exception.getLocalizedMessage()));
        String s = exception.toString();
        if(s.contains("<") || s.contains(">") || s.contains("=") || s.contains("\n")) {
            sql1.setTypeMethod(s);
        } else {
            sql1.setTypeMethod("<span style='color: red; font-size:1000;'>" + s + "</span>");
        }
        sql1.setModel("exception");
        sql1.setType(exception.getClass().getTypeName());
        if (sql1.getTypeMethod().equals(span.getTypeMethod())) {
            return;
        }
        NewTrackManager.registerSpan(sql1);
    }

    @Override
    public DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<?> transform(DynamicType.Builder<?> builder) {
        return null;
    }

    @Override
    public ElementMatcher<? super TypeDescription> type() {
        return null;
    }
}
