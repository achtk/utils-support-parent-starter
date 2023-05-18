package com.chua.agent.support.plugin;

import com.alibaba.json.JSONArray;
import com.alibaba.json.JSONObject;
import com.chua.agent.support.annotation.Path;
import com.chua.agent.support.reflectasm.MethodAccess;
import com.chua.agent.support.utils.ClassUtils;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.*;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * SpringBeanPlugin
 * @author CH
 */
public class SpringMappingPlugin implements HtmlAgentPlugin{

    private static final String[] HTTP_METHOD = new String[]{"GET", "HEAD", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "TRACE"};
    private static MethodAccess methodAccess;
    private static Object beanFactory;
    protected JSONArray jsonArray;

    @Path("spring-mapping")
    public String springBean() {
        return "spring-mapping.html";
    }

    private static AtomicBoolean status = new AtomicBoolean(false);

    @Path("spring-mapping-data")
    public String springBeanData() {
        JSONArray rs = new JSONArray();
        if (null == methodAccess) {
            refresh();
            return rs.toJSONString();
        }

        if (null == beanFactory) {
            return rs.toJSONString();
        }

        Map getHandlerMethods = (Map) methodAccess.invoke(beanFactory, "getHandlerMethods");
        if (null == getHandlerMethods) {
            return rs.toJSONString();
        }
        getHandlerMethods.forEach((k, v) -> {
            JSONObject jsonObject = new JSONObject();
            String url = k.toString()
                    .replaceAll("[{}(\\[)[\\],]{2}]", "")
                    .replace("produces", " -produces ")
                    .replace("consumer", " -consumer ");

            boolean isContains = false;
            for (String s : HTTP_METHOD) {
                if (url.contains(s)) {
                    isContains = true;
                    break;
                }
            }


            if (!isContains) {
                url = "GET " + url;
            }

            jsonObject.put("url", url);
            jsonObject.put("mapping", v.toString());

            rs.add(jsonObject);
        });
        return rs.toJSONString();
    }

    private void refresh() {
        if (status.get()) {
            return;
        }
        status.set(true);
        try {
            beanFactory = ClassUtils.invoke("getBean", SpringBeanPlugin.beanFactory, Thread.currentThread().getContextClassLoader().loadClass("org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping"));
            methodAccess = MethodAccess.get(beanFactory.getClass());
        } catch (ClassNotFoundException ignored) {
        }
    }


    @RuntimeType
    public static Object before(@Origin Method method,
                                @This Object obj,
                                @AllArguments Object[] allArguments,
                                @SuperCall Callable<?> callable) throws Exception {

        return register(method, obj, allArguments, callable);
    }

    private static Object register(Method method, Object obj, Object[] allArguments, Callable<?> callable) throws Exception {
        Object call = callable.call();
        if (null == beanFactory) {
            Class<?> aClass = obj.getClass();
            methodAccess = MethodAccess.get(aClass);
            beanFactory = obj;
        }
        return call;
    }


    @Override
    public void setAddress(String address) {

    }

    @Override
    public void setParameter(JSONObject parameter) {

    }

    @Override
    public String name() {
        return "spring-mapping";
    }

    @Override
    public DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<?> transform(DynamicType.Builder<?> builder) {
        return builder.method(
                ElementMatchers.named("registerMapping")
                        .or(ElementMatchers.named("registerHandlerMethod"))
        ).intercept(MethodDelegation.to(SpringMappingPlugin.class));
    }

    @Override
    public ElementMatcher<? super TypeDescription> type() {
        return ElementMatchers.named("org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping")
                .or(ElementMatchers.hasSuperType(
                        ElementMatchers.named("org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping")));
    }
}
