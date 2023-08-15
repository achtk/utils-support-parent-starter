package com.chua.agent.support.store;

import com.chua.agent.support.constant.Constant;
import com.chua.agent.support.plugin.Plugin;
import com.chua.agent.support.span.Span;
import com.chua.agent.support.utils.ClassUtils;
import com.chua.agent.support.utils.StringUtils;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Proxy;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import static com.chua.agent.support.store.AgentStore.instrumentation;
import static com.chua.agent.support.store.AgentStore.log;
import static com.chua.agent.support.store.ServerStore.installServer;
import static com.chua.agent.support.store.ServerStore.preServer;

/**
 * 插件缓存器
 *
 * @author CH
 */
public class PluginStore implements Constant {
    public static final Map<String, Plugin> PLUGINS = new ConcurrentHashMap<>();
    /**
     * 启动的插件
     */
    private static List<String> plugin = new LinkedList<>();

    /**
     * 初始化插件
     */
    public static void installPlugins() {
        List<Class<?>> classes = ClassUtils.getClasses("com.chua.agent.support.plugin");

        for (Class<?> aClass : classes) {
            if (aClass.isInterface()) {
                continue;
            }
            try {
                Class<?>[] type = new Class[]{Plugin.class};
                Object newInstance = aClass.newInstance();
                Plugin plugin = (Plugin) Proxy.newProxyInstance(
                        Thread.currentThread().getContextClassLoader(), type, (proxy, method, args) -> {
                            method.setAccessible(true);
                            return method.invoke(newInstance, args);
                        });
                String name = plugin.name();
                if (StringUtils.isNullOrEmpty(name)) {
                    continue;
                }
                PLUGINS.put(name, plugin);
                log(Level.INFO, "安装插件{}", name);
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * 实例化
     */
    public static void prePlugins() {
        AgentBuilder.Default agentBuilder = new AgentBuilder.Default();
        AgentBuilder builder = agentBuilder
                .ignore(ElementMatchers.isSubTypeOf(Span.class))
                .ignore(ElementMatchers.nameStartsWith("com.chua.agent.support"))
                .ignore(ElementMatchers.nameContainsIgnoreCase("GeneratedMethodAccessor"))
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .with(AgentBuilder.TypeStrategy.Default.REBASE)
                .with(new AgentBuilder.InitializationStrategy.SelfInjection.Eager());
        AgentBuilder.Identified.Extendable transform = null;

        for (Plugin plugin : PLUGINS.values()) {
            String name = plugin.name();
            if (null == name) {
                continue;
            }
            if (isMatch(name)) {
                ElementMatcher<? super TypeDescription> type = plugin.type();
                if (null != type) {
                    if (null == transform) {
                        transform = builder.type(type).transform((builder1, typeDescription, classLoader, module, protectionDomain) -> plugin.transform(builder1));
                    } else {
                        transform = transform.type(type).transform((builder12, typeDescription, classLoader, module, protectionDomain) -> plugin.transform(builder12));
                    }
                }
                transform = plugin.transforms(transform);

            }
        }
        if (null == transform) {
            return;
        }
        transform = installServer(transform);
        preServer();
        transform.installOn(instrumentation);
    }


    private static boolean isMatch(String name) {
        try {
            Thread.currentThread().getContextClassLoader().loadClass(LOG4J);
            return plugin.contains(name) || plugin.isEmpty();
        } catch (ClassNotFoundException e) {
            return (plugin.contains(name) || plugin.isEmpty()) && !"vm".equalsIgnoreCase(name);
        }
    }

    /**
     * 获取插件
     *
     * @param type 类型
     * @param <T>  类型
     * @return 结果
     */
    @SuppressWarnings("ALL")
    public static <T extends Plugin> List<T> getPlugin(Class<T> type) {
        List<T> rs = new LinkedList<>();
        if (!plugin.isEmpty()) {
            for (String s : plugin) {
                Plugin Plugin = PLUGINS.get(s);
                if (null == Plugin || !type.isAssignableFrom(Plugin.getClass())) {
                    continue;

                }

                rs.add((T) Plugin);
            }
        } else {
            for (Map.Entry<String, Plugin> entry : PLUGINS.entrySet()) {
                Plugin Plugin = entry.getValue();
                if (null == Plugin || !type.isAssignableFrom(Plugin.pluginType())) {
                    continue;
                }

                rs.add((T) Plugin);

            }
        }
        return rs;
    }

    public static boolean isEmpty() {
        return PLUGINS.isEmpty();
    }
}
