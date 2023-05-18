package com.chua.agent.support;

import com.chua.agent.support.log.FileLogResolver;
import com.chua.agent.support.log.LogResolver;
import com.chua.agent.support.plugin.AgentPlugin;
import com.chua.agent.support.plugin.HtmlAgentPlugin;
import com.chua.agent.support.server.SimpleHttpServer;
import com.chua.agent.support.servlet.SpringServlet;
import com.chua.agent.support.span.span.Span;
import com.chua.agent.support.transform.Spec;
import com.chua.agent.support.transform.TransformerImpl;
import com.chua.agent.support.utils.ClassUtils;
import com.chua.agent.support.utils.NetUtils;
import com.chua.agent.support.utils.StringUtils;
import com.chua.agent.support.ws.SimpleWsServer;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.channels.FileChannel;
import java.nio.channels.spi.AbstractInterruptibleChannel;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.nio.channels.spi.AbstractSelector;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipFile;

/**
 * 代理
 * env.log: true | false            开启环境日志
 * env.retry: <int>                 重试间隔; 默认200
 * vm.delay: <int>                 vm推送延迟; 默认5000 ms
 * vm.top: <int>                 前{}进程; 默认10
 * env.store: <int>                 信息存储
 * env.limit: <int>                 限制数量; 默认10000
 * log.level: <string>                 日志级别; 默认debug
 * http.port: <int>                 开启独立的http端口
 * http.host: <string>           开启独立的http地址
 * ws.port: <int>                 开启独立的ws端口
 * white.address: <string>           访问白名单
 * plugin.scan: <string>            插件扫描位置, 默认: com.chua.agent.support.plugin
 *
 * @author CH
 */
public class Agent {

    private static final String LOG4J = "org.slf4j.LoggerFactory";
    private static final String HTTP_PORT = "http.port";
    private static final String HTTP_HOST = "http.host";
    public static final String WS_PORT = "ws.port";
    private static final String PLUGIN_SCAN = "plugin.scan";
    private static final String ENV_LOG = "env.log";
    private static final String ENV_STORE = "env.store";
    public static final String ENV_LIMIT = "env.limit";
    public static final String ENV_RETRY = "env.retry";
    public static final String LOG_LEVEL = "long.level";
    public static final String CONTENT_PATH = "content.path";
    public static final String VM_DELAY = "vm.delay";
    public static final String VM_TOP = "vm.top";
    public static final String WHITE_ADDRESS = "white.address";
    private static final String DATE_FORMAT_DETAIL = "yyyy-MM-dd HH:mm:ss";


    public static final String DEFAULT_CONTEXT = "/agent";
    public static final String DEFAULT_ADDRESS = "0.0.0.0,0:0:0:0:0:0:0:1,127.0.0.1";

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT_DETAIL);
    private static final String DEFAULT_HOST = "0.0.0.0";
    private static Level LOG_LEVEL_LEVEL = Level.SEVERE;
    private static Logger logger = Logger.getLogger("premain");
    /**
     * 启动的插件
     */
    private static List<String> plugin = new LinkedList<>();
    /**
     * 参数
     */
    private static Map<String, Object> PARAMETER = new LinkedHashMap<>();

    public static Map<String, AgentPlugin> plugins = new LinkedHashMap<>();

    public static Instrumentation instrumentation;

    public static LogResolver logResolver;

    public static void agentmain(String agentArguments, Instrumentation instrumentation) throws Exception {
        premain(agentArguments, instrumentation);
    }

    public static void premain(String agentArguments, Instrumentation instrumentation) throws Exception {
        initialConfig(agentArguments);
        initialEnv();
        initialStore();
        initialPlugin();
        initialInst(instrumentation);
        initialJavaInst(instrumentation);
        initialRetransmit(instrumentation);
        initialWsServer();
    }


    /**
     * ws服务器
     */
    private static void initialWsServer() {
        int serverPort = getIntegerValue(WS_PORT, 10101);
        if (serverPort > -1) {
            int availablePort = NetUtils.getAvailablePort(serverPort);
            setIntegerValue(WS_PORT, availablePort);
            serverPort = availablePort;
            try {
                final SimpleWsServer simpleWsServer = new SimpleWsServer(serverPort);
                simpleWsServer.start();
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    try {
                        simpleWsServer.stop(0);
                    } catch (InterruptedException ignored) {
                    }
                }));
            } catch (Throwable ignored) {
                logResolver.register("端口: " + serverPort + "启动异常");
                logger.log(Level.INFO, "端口: " + serverPort + "启动异常," + ignored.getMessage() );
            }
        }
    }


    /**
     * 存储器
     */
    private static void initialStore() {
//        String store = getStringValue(ENV_STORE, "sql");
//
//        if("file".equalsIgnoreCase(store)) {
//            return;
//        }

        logResolver = new FileLogResolver();
    }


    /**
     * javassist
     *
     * @param instrumentation instrumentation
     */
    private static void initialJavaInst(Instrumentation instrumentation) {
        if (isMatch("stream")) {
            instrumentation.addTransformer(new TransformerImpl(Spec.createSpec()), true);
        }
    }


    /**
     * 重载
     *
     * @param instrumentation 加载器
     */
    private static void initialRetransmit(Instrumentation instrumentation) {
        if (isMatch("stream")) {
            List<Class> rt = new ArrayList<>(Arrays.asList(
                    FileInputStream.class,
                    FileOutputStream.class,
                    RandomAccessFile.class,
                    Exception.class,
                    ZipFile.class,
                    AbstractSelectableChannel.class,
                    AbstractInterruptibleChannel.class,
                    FileChannel.class,
                    PrintStream.class,
                    AbstractSelector.class
            ));
            try {
                Class<?> aClass = Class.forName("java.net.PlainSocketImpl");
                rt.add(aClass);
            } catch (ClassNotFoundException ignored) {
            }
            try {
                instrumentation.retransformClasses(rt.toArray(new Class[0]));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 初始化插件
     */
//    private static void initialPlugin() {
//        String scan = getStringValue(PLUGIN_SCAN, "com.chua.agent.support.plugin");
//        for (String s : scan.split(",")) {
//            initialPlugin();
//        }
//    }

    /**
     * 初始化插件
     */
    private static void initialPlugin() {
        List<Class<?>> classes = ClassUtils.getClasses("com.chua.agent.support.plugin");

        for (Class<?> aClass : classes) {
            if (aClass.isInterface()) {
                continue;
            }
            try {
                Class[] type = new Class[]{AgentPlugin.class};
                Object newInstance = aClass.newInstance();
                if (newInstance instanceof HtmlAgentPlugin) {
                    type = new Class[]{AgentPlugin.class, HtmlAgentPlugin.class};
                }

                AgentPlugin agentPlugin = (AgentPlugin) Proxy.newProxyInstance(
                        Thread.currentThread().getContextClassLoader(), type, new InvocationHandler() {
                            @Override
                            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                                method.setAccessible(true);
                                Object invoke = method.invoke(newInstance, args);
                                return invoke;
                            }
                        });
                String name = agentPlugin.name();
                if (StringUtils.isNullOrEmpty(name)) {
                    continue;
                }
                plugins.put(name, agentPlugin);
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * 获取参数
     *
     * @param key          索引
     * @param defaultValue 默认值
     * @return 参数
     */
    public static String getStringValue(String key, String defaultValue) {
        return PARAMETER.getOrDefault(key, defaultValue).toString();
    }

    /**
     * 获取参数
     *
     * @param key          索引
     * @param defaultValue 默认值
     * @return 参数
     */
    public static int getIntegerValue(String key, int defaultValue) {
        Object orDefault = PARAMETER.getOrDefault(key, defaultValue);
        if(orDefault instanceof String) {
            return Integer.parseInt(orDefault.toString());
        }
        return (int) orDefault;
    }
    /**
     * 设置参数
     *
     * @param key          索引
     * @param value         值
     */
    private static void setIntegerValue(String key, int value) {
        PARAMETER.put(key, value);
    }


    /**
     * 实例化
     *
     * @param instrumentation 虚拟机
     */
    private static void initialInst(Instrumentation instrumentation) {
        Agent.instrumentation = instrumentation;
        AgentBuilder.Default agentBuilder = new AgentBuilder.Default();
        AgentBuilder builder = agentBuilder
                .ignore(ElementMatchers.isSubTypeOf(Span.class))
                .ignore(ElementMatchers.nameStartsWith("com.chua.agent.support"))
                .ignore(ElementMatchers.nameContainsIgnoreCase("GeneratedMethodAccessor"))
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .with(AgentBuilder.TypeStrategy.Default.REBASE)
                .with(new AgentBuilder.InitializationStrategy.SelfInjection.Eager());
        AgentBuilder.Identified.Extendable transform = null;

        for (AgentPlugin plugin : plugins.values()) {
            String name = plugin.name();
            if (null == name) {
                continue;
            }
            if (isMatch(name)) {
                ElementMatcher<? super TypeDescription> type = plugin.type();
                if (null != type) {
                    if (null == transform) {
                        transform = builder.type(type).transform((builder1, typeDescription, classLoader, javaModule) -> plugin.transform(builder1));
                    } else {
                        transform = transform.type(type).transform((builder12, typeDescription, classLoader, javaModule) -> plugin.transform(builder12));
                    }
                }
                transform = plugin.transforms(transform);

            }
        }

        transform = installSpring(transform);

        transform.installOn(instrumentation);
    }


    private static boolean isMatch(String name) {
        try {
            Thread.currentThread().getContextClassLoader().loadClass(LOG4J);
            return Agent.plugin.contains(name) || Agent.plugin.isEmpty();
        } catch (ClassNotFoundException e) {
            return (Agent.plugin.contains(name) || Agent.plugin.isEmpty()) && !"vm".equalsIgnoreCase(name);
        }
    }

    private static AgentBuilder.Identified.Extendable installSpring(AgentBuilder.Identified.Extendable transform) {
        int serverPort = getIntegerValue(HTTP_PORT, -1);
        if (serverPort <= -1) {
            System.out.print(DATE_TIME_FORMATTER.format(LocalDateTime.now()) + " INFO  [Console] [1/main] open spring server port \r\n");
            transform = transform.type(ElementMatchers.hasSuperType(ElementMatchers.named(
                            "org.springframework.context.ApplicationContextAware"))).or(ElementMatchers.named("org.springframework.beans.factory.support.RootBeanDefinition"))
                    .transform((builder, typeDescription, classLoader, module) -> {
                        return builder.method(ElementMatchers.named("setApplicationContext").or(ElementMatchers.named("setTargetType")))
                                .intercept(MethodDelegation.to(SpringServlet.class));
                    });
        } else {
            System.out.print(DATE_TIME_FORMATTER.format(LocalDateTime.now()) + " INFO  [Console] [1/main] open http server port("+ serverPort +") \r\n");
            initialHttpServer(serverPort);
        }

        return transform;
    }

    /**
     * 初始化http
     */
    private static void initialHttpServer(int serverPort) {
        SimpleHttpServer server = new SimpleHttpServer(getStringValue(HTTP_HOST, DEFAULT_HOST), serverPort);
        server.start();
    }

    /**
     * 是否存在变量
     *
     * @param key 变量
     * @return 是否存在变量
     */
    private static boolean hasValue(String key) {
        return null != PARAMETER.get(key);
    }

    /**
     * 初始化环境
     */
    private static void initialEnv() {
        if (!printEnv()) {
            return;
        }

        for (Map.Entry<String, Object> entry : PARAMETER.entrySet()) {
            Agent.log(Level.INFO, "{} : {}", entry.getKey(), entry.getValue());
        }
    }

    /**
     * 是否输出环境
     *
     * @return 是否输出环境
     */
    private static boolean printEnv() {
        return "true".equals(PARAMETER.getOrDefault(ENV_LOG, true).toString());
    }

    /**
     * 输出日志
     *
     * @param level   级别
     * @param message 消息
     * @param args    参数
     */
    private static void printLog(Level level, String message, Object... args) {
        logger.log(level, StringUtils.format(message, args));
    }

    /**
     * 初始化配置
     *
     * @param agentArguments 参数
     */
    private static void initialConfig(String agentArguments) {
        if (agentArguments == null) {
            return;
        }

        for (String t : agentArguments.split(",")) {
            String[] split1 = t.split("=", 2);
            if (split1.length == 2) {
                PARAMETER.put(split1[0], split1[1]);
                continue;
            }
            PARAMETER.put(split1[0], true);
        }

        LOG_LEVEL_LEVEL = Level.parse(getStringValue(LOG_LEVEL, "SEVERE"));

    }

    /**
     * 获取插件
     *
     * @param type 类型
     * @param <T>  类型
     * @return 结果
     */
    public static <T extends AgentPlugin> List<T> getPlugin(Class<T> type) {
        List<T> rs = new LinkedList<>();
        if (!plugin.isEmpty()) {
            for (String s : plugin) {
                AgentPlugin agentPlugin = plugins.get(s);
                if (null == agentPlugin || !type.isAssignableFrom(agentPlugin.getClass())) {
                    continue;

                }

                rs.add((T) agentPlugin);
            }
        } else {
            for (Map.Entry<String, AgentPlugin> entry : plugins.entrySet()) {
                AgentPlugin agentPlugin = entry.getValue();
                if (null == agentPlugin || !type.isAssignableFrom(agentPlugin.pluginType())) {
                    continue;
                }

                rs.add((T) agentPlugin);

            }
        }
        return rs;
    }


    public static void log(Level level, String message, Object... args) {
        if (LOG_LEVEL_LEVEL.intValue() <= level.intValue()) {
            printLog(level, DATE_TIME_FORMATTER.format(LocalDateTime.now()) + " [" + level.getName() + "] " + message, args);
        }
    }
}
