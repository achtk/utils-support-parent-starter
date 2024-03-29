package com.chua.agent.support.plugin;

import com.alibaba.json.JSONObject;
import com.chua.agent.support.Agent;
import com.chua.agent.support.annotation.Path;
import com.chua.agent.support.utils.StringUtils;
import com.chua.agent.support.ws.SimpleWsServer;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.*;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static com.chua.agent.support.Agent.ENV_RETRY;

/**
 * slf4j
 *
 * @author CH
 */
public class LogAgentPlugin implements HtmlAgentPlugin {

    public static final LogAgentPlugin INSTANCE = new LogAgentPlugin();
    public static final List<Object> EXT_PLUGIN = new LinkedList<>();

    private static final LinkedBlockingQueue<String> LOGGER = new LinkedBlockingQueue<>();
    private static final Map<String, AtomicInteger> INDEX_MAP = new ConcurrentHashMap<>();

    private static final Set<String> IGNORE = new HashSet<>();

    static {
        IGNORE.add("org.apache.coyote.http11.Http11OutputBuffer");
        IGNORE.add("com.alibaba.druid.support.json.JSONWriter");
        IGNORE.add("org.apache.tomcat.util.net.NioChannel");
        IGNORE.add("org.apache.catalina.connector.CoyoteWriter");
        IGNORE.add("ch.qos.logback.core.FileAppender");
    }

    private String address;


    @Path("slf4j_sse")
    public String html() {
        return "slf4j_sse.html";
    }

    @Path(value = "slf4j_sse_data", type = "event")
    public byte[] data() {
        AtomicInteger absent = INDEX_MAP.computeIfAbsent(address, i -> new AtomicInteger(0));
        byte[] bytes = null;
        String s = LOGGER.poll();
        if (null != s) {
            s = s.replace("\t", "<span style='margin-left:40px;'></span>");
            if (s.contains("<span ")) {
                s = "html:" + s;
            }
            bytes = StringUtils.utf8Bytes("id:" + absent.incrementAndGet() + "\nevent: message\ndata:" + s + "\n\nretry:" + Agent.getIntegerValue(ENV_RETRY, 200) + "\n\n");
        } else {
            bytes = StringUtils.utf8Bytes("id: -1\nevent: message\ndata: \n\nretry:" + Agent.getIntegerValue(ENV_RETRY, 200) + "\n\n");
        }
        return bytes;
    }


    @Override
    public String name() {
        return "log";
    }

    @Override
    public Class<?> pluginType() {
        return this.getClass();
    }

    @Override
    public DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<?> transform(DynamicType.Builder<?> builder) {
        return builder.method(ElementMatchers.named("writeBytes")
                        .or(ElementMatchers.named("write"))
                        .or(ElementMatchers.named("encode")))
                .intercept(MethodDelegation.to(LogAgentPlugin.class));
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
        if ("ch.qos.logback.core.rolling.RollingFileAppender".equals(name)) {
            return callable.call();
        }

        if (ignore(aClass) && null != callable) {
            try {
                return callable.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
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
//        while (LOGGER.size() > Agent.getIntegerValue(ENV_LIMIT, 10000)) {
//            LOGGER.poll();
//        }

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

            if (!StringUtils.isNullOrEmpty(msg)) {
                String[] split = msg.split("\r\n");
                for (String s : split) {
                    SimpleWsServer.sendLog(s);
                }
//                LOGGER.addAll(Arrays.asList(msg.split("\r\n")));
            }
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


    @Override
    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public void setParameter(JSONObject parameter) {

    }
}
