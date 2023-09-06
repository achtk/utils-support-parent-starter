package com.chua.agent.support.http;

import com.chua.agent.support.annotation.Path;
import com.chua.agent.support.plugin.HtmlPlugin;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * spring servlet
 *
 * @author CH
 */
public abstract class AbstractServlet<Req, Res> implements RequestHandler<Req, Res> {

    protected final Map<String, Method> methodMap = new ConcurrentHashMap<>();
    protected final List<Method> event = new ArrayList<>();

    protected final HtmlPlugin plugin;

    public AbstractServlet(HtmlPlugin plugin) {
        this.plugin = plugin;
        Class<?> aClass = plugin.pluginType();
        Method[] declaredMethods = aClass.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            Path path = declaredMethod.getDeclaredAnnotation(Path.class);
            if (null == path) {
                continue;
            }

            declaredMethod.setAccessible(true);
            methodMap.put(path.value(), declaredMethod);
            if ("event".equalsIgnoreCase(path.type())) {
                event.add(declaredMethod);
            }
        }
    }

    @Override
    public String[] getPath() {
        return methodMap.keySet().toArray(new String[0]);
    }


}
