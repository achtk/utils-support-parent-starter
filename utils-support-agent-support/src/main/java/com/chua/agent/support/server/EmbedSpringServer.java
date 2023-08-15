package com.chua.agent.support.server;

import com.chua.agent.support.http.IndexRequestHandler;
import com.chua.agent.support.http.RequestHandler;
import com.chua.agent.support.http.ResourceRequestHandler;
import com.chua.agent.support.http.SimpleHttpHandler;
import com.chua.agent.support.plugin.HtmlPlugin;
import com.chua.agent.support.store.PluginStore;
import com.chua.agent.support.utils.ClassUtils;
import javassist.*;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.*;
import net.bytebuddy.matcher.ElementMatchers;

import java.io.IOException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * http
 *
 * @author CH
 */
public class EmbedSpringServer implements EmbedServer {

    private static final String NAME = "org.springframework.context.ConfigurableApplicationContext";
    protected static final String BEAN = "org.springframework.boot.web.servlet.ServletRegistrationBean";
    private static final String BUILDER = "org.springframework.beans.factory.support.BeanDefinitionBuilder";
    private static final AtomicBoolean ATOMIC_BOOLEAN = new AtomicBoolean(false);
    protected static Class<?> ANN;
    private static Class<?> BEAN_ANN;
    protected static Class<?> BUILDER_ANN;

    private static final Map<String, RequestHandler> HANDLER_MAP = new ConcurrentHashMap<>();

    static {
        HANDLER_MAP.put("resource", new ResourceRequestHandler());
        HANDLER_MAP.put("index", new IndexRequestHandler());
        for (HtmlPlugin plugin : PluginStore.getPlugin(HtmlPlugin.class)) {
            SimpleHttpHandler handler = new SimpleHttpHandler(plugin);
            String[] paths = handler.getPath();
            if (null == paths) {
                continue;
            }

            for (String path : paths) {
                HANDLER_MAP.put(path, handler);
            }
        }
    }

    private static Object entity;

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    /**
     * 注入
     *
     * @param transform 交换器
     * @return 交换器
     */
    public AgentBuilder.Identified.Extendable inject(AgentBuilder.Identified.Extendable transform) {
        System.out.print(DATE_TIME_FORMATTER.format(LocalDateTime.now()) + " INFO  [Console] [1/main] open spring server port \r\n");
        transform = transform.type(ElementMatchers.hasSuperType(ElementMatchers.named(
                        "org.springframework.context.ApplicationContextAware"))).or(ElementMatchers.named("org.springframework.beans.factory.support.RootBeanDefinition"))
                .transform((builder, typeDescription, classLoader, module, protectionDomain) -> builder.method(ElementMatchers.named("setApplicationContext").or(ElementMatchers.named("setTargetType")))
                        .intercept(MethodDelegation.to(EmbedSpringServer.class)));

        return transform;
    }


    @RuntimeType
    public static Object before(@Origin Method method,
                                @This Object obj,
                                @AllArguments Object[] allArguments,
                                @SuperCall Callable<?> callable) throws Exception {
        if (ATOMIC_BOOLEAN.get()) {
            return callable.call();
        }

        Class<?> aClass1 = null;
        try {
            aClass1 = Thread.currentThread().getContextClassLoader().loadClass(BUILDER);
        } catch (Exception ignore) {
        }
        if (null != aClass1) {
            return doBuilder(aClass1, callable, allArguments);
        }
        return callable.call();
    }

    private static Object doBuilder(Class<?> aClass1, Callable<?> callable, Object[] allArguments) throws Exception {
        Object param = allArguments[0];
        if (!ATOMIC_BOOLEAN.get() && "org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext".equals(param.getClass().getTypeName())) {
            Method method1 = null;
            try {
                method1 = param.getClass().getMethod("getBeanFactory");
            } catch (Exception ignored) {
            }
            if (null != method1) {
                method1.setAccessible(true);
                Object invoke = null;
                try {
                    invoke = method1.invoke(param);
                } catch (Exception ignored) {
                }
                if (null != invoke) {
                    Optional<Method> registerSingleton1 = Arrays.stream(invoke.getClass().getDeclaredMethods()).filter(it -> "registerBeanDefinition".equals(it.getName())).findAny();
                    if (registerSingleton1.isPresent()) {
                        Method method2 = registerSingleton1.get();
                        method2.setAccessible(true);
                        try {
                            Method genericBeanDefinition1 = aClass1.getDeclaredMethod("genericBeanDefinition", String.class);
                            genericBeanDefinition1.setAccessible(true);
                            Object genericBeanDefinition = genericBeanDefinition1.invoke(null, BEAN);
                            Class<?> aClass = genericBeanDefinition.getClass();
                            Method addConstructorArgValue = aClass.getDeclaredMethod("addConstructorArgValue", Object.class);
                            Method getRawBeanDefinition = aClass.getDeclaredMethod("getRawBeanDefinition");
                            addConstructorArgValue.setAccessible(true);
                            getRawBeanDefinition.setAccessible(true);

                            Object bean = createAgentServlet();
                            if (null == bean) {
                                ATOMIC_BOOLEAN.set(true);
                                return callable.call();
                            }
                            addConstructorArgValue.invoke(genericBeanDefinition, bean);
                            addConstructorArgValue.invoke(genericBeanDefinition, new String[]{DEFAULT_CONTEXT + "/*"});

                            Method registerBeanDefinition = invoke.getClass().getDeclaredMethod("registerBeanDefinition", String.class, Thread.currentThread().getContextClassLoader().loadClass("org.springframework.beans.factory.config.BeanDefinition"));
                            registerBeanDefinition.setAccessible(true);
                            registerBeanDefinition.invoke(invoke, "http-agent-servlet", getRawBeanDefinition.invoke(genericBeanDefinition));
                            ATOMIC_BOOLEAN.set(true);
                        } catch (Exception ignored) {

                        }
                    }
                }
            }
        }
        return callable.call();
    }

    private static Object createAgentServlet() throws Exception {
        if (null != entity) {
            return entity;
        }
        ClassPool classPool = ClassPool.getDefault();
        classPool.appendClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));
        String s = AgentServlet.class.getTypeName() + "$1";
        CtClass ctClass = classPool.makeClass(s);
        ctClass.setSuperclass(classPool.get("javax.servlet.http.HttpServlet"));
        ctClass.addField(new CtField(classPool.get(AgentServlet.class.getTypeName()), "o", ctClass));
        ctClass.addMethod(CtNewMethod.make(
                Modifier.PROTECTED,
                classPool.get(void.class.getTypeName()),
                "service",
                new CtClass[]{classPool.get("javax.servlet.http.HttpServletRequest"), classPool.get("javax.servlet.http.HttpServletResponse")},
                new CtClass[]{classPool.get("javax.servlet.ServletException"), classPool.get("java.io.IOException")},
                "{try{" +
                        "o.service($1, $2);" +
                        " } catch (Exception ignored) {" +
                        "                      ignored.printStackTrace(); }" +
                        "}",
                ctClass
        ));

        Class<?> aClass = ctClass.toClass();
        Object newInstance = aClass.newInstance();
        ClassUtils.setField(aClass, newInstance, "o", new AgentServlet());
        return (entity = newInstance);
    }

    /**
     * servlet
     */
    //@WebServlet(loadOnStartup = 1, name = "javaAgentServlet", urlPatterns = "/agent/*", asyncSupported = true)
    public static class AgentServlet {

        public void service(Object req, Object resp) throws IOException {
            String uri = ClassUtils.invoke("getRequestURI", req).toString();
            String contentPath = DEFAULT_CONTEXT;
            int index = uri.indexOf(contentPath);
            if (index == -1) {
                uri = uri.replace(contentPath, "").substring(1);
            } else {
                uri = uri.substring(index).replace(contentPath, "");
                if (uri.length() != 0) {
                    uri = uri.substring(1);
                }
            }
            RequestHandler requestHandler = HANDLER_MAP.get(uri);

            if (null == requestHandler) {
                for (Map.Entry<String, RequestHandler> entry : HANDLER_MAP.entrySet()) {
                    if (uri.startsWith(entry.getKey())) {
                        requestHandler = entry.getValue();
                        break;
                    }
                }

            }

            if (null != requestHandler) {
                requestHandler.handle(req, resp);
                return;
            }
        }
    }

}
