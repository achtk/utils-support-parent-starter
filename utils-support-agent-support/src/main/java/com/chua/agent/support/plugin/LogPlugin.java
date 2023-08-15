package com.chua.agent.support.plugin;

import com.chua.agent.support.store.TransPointStore;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.*;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * 日志检测插件
 *
 * @author CH
 */
public class LogPlugin implements Plugin {

    public static final List<Object> EXT_PLUGIN = new LinkedList<>();
    private static final Set<String> IGNORE = new HashSet<>();
    private static final String LOGBACK = "ch.qos.logback.core.rolling.RollingFileAppender";

    static {
        IGNORE.add("org.apache.coyote.http11.Http11OutputBuffer");
        IGNORE.add("com.alibaba.druid.support.json.JSONWriter");
        IGNORE.add("org.apache.tomcat.util.net.NioChannel");
        IGNORE.add("org.apache.catalina.connector.CoyoteWriter");
        IGNORE.add("ch.qos.logback.core.FileAppender");
    }

    @Override
    public String name() {
        return "log";
    }

    @Override
    public DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<?> transform(DynamicType.Builder<?> builder) {
        return builder.method(ElementMatchers.named("writeBytes")
                        .or(ElementMatchers.named("write"))
                        .or(ElementMatchers.named("encode")))
                .intercept(MethodDelegation.to(LogPlugin.class));
    }

    @Override
    public ElementMatcher<? super TypeDescription> type() {
        return ElementMatchers.named("ch.qos.logback.core.OutputStreamAppender")
                .or(ElementMatchers.named("org.apache.logging.log4j.core.layout.StringBuilderEncoder"))
                .or(ElementMatchers.named("org.apache.log4j.helpers.QuietWriter"));
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

        invokeProxyListener(target, method, objects, delegate, callable);
        Class<?> aClass = target.getClass();

        String name = aClass.getTypeName();
        if (LOGBACK.equals(name) || (ignore(aClass) && null != callable)) {
            return callable.call();
        }

        if (objects.length == 0 && null != callable) {
            return callable.call();
        }

        registerSlf4j(objects);

        if (null != callable) {
            return callable.call();
        }
        return null;
    }

    private static void invokeProxyListener( // 被拦截的目标对象 （动态生成的目标对象）
                                             @This Object target,
                                             // 正在执行的方法Method 对象（目标对象父类的Method）
                                             @Origin Method method,
                                             // 正在执行的方法的全部参数
                                             @AllArguments Object[] objects,
                                             // 目标对象的一个代理
                                             @Super Object delegate,
                                             // 方法的调用者对象 对原始方法的调用依靠它
                                             @SuperCall Callable<?> callable) {
        for (Object proxy : EXT_PLUGIN) {
            if (null == proxy) {
                continue;
            }

            invokeProxy(proxy, target, method, objects, delegate, callable);
        }
    }

    private static void invokeProxy( // 被拦截的目标对象 （动态生成的目标对象）
                                     Object proxy,
                                     @This Object target,
                                     // 正在执行的方法Method 对象（目标对象父类的Method）
                                     @Origin Method method,
                                     // 正在执行的方法的全部参数
                                     @AllArguments Object[] objects,
                                     // 目标对象的一个代理
                                     @Super Object delegate,
                                     // 方法的调用者对象 对原始方法的调用依靠它
                                     @SuperCall Callable<?> callable) {

        Class<?> aClass = proxy.getClass();
        try {
            Method intercept = aClass.getDeclaredMethod("intercept", Object.class, Method.class, Object[].class, Object.class, Callable.class);
            intercept.setAccessible(true);
            intercept.invoke(null, target, method, objects, delegate, callable);
        } catch (Throwable ignored) {
        }
    }


    public static void registerSlf4j(Object[] allArguments) {
        Object value = allArguments[0];
        if (null != value) {
            String msg = null;
            if (value instanceof byte[]) {
                msg = new String((byte[]) value);
            } else if (value instanceof String) {
                if (!"".equals(value)) {
                    msg = value.toString();
                }
            } else if (value instanceof StringBuilder) {
                msg = value.toString();
            }

            if (msg.contains("org.zbus.net.tcp.TcpClient")) {
                return;
            }
            TransPointStore.INSTANCE.publish(msg);
        }

    }


    /**
     * 是否忽略
     *
     * @param cls 对象
     * @return 是否忽略
     */
    private static boolean ignore(Class<?> cls) {
        return IGNORE.contains(cls.getName());
    }
}
