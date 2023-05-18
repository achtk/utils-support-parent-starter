package com.chua.agent.support.plugin;

import com.alibaba.json.JSONObject;
import com.chua.agent.support.Agent;
import com.chua.agent.support.annotation.Path;
import com.chua.agent.support.plugin.apm.Interceptor;
import com.chua.agent.support.utils.ClassUtils;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatcher;

import java.util.List;
import java.util.logging.Level;

/**
 * 链路
 *
 * @author CH
 */
public class TraceAgentPlugin implements HtmlAgentPlugin {

    @Path("trace")
    public String html() {
        return "trace.html";
    }

    @Override
    public String name() {
        return "trace";
    }

    @Override
    public DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<?> transform(DynamicType.Builder<?> builder) {
        return null;
    }

    @Override
    public AgentBuilder.Identified.Extendable transforms(AgentBuilder.Identified.Extendable transform) {
        List<Class<?>> classes = ClassUtils.getClasses("com.chua.agent.support.plugin.apm");
//        classes.add(ApacheHttpInterceptor.class);
//        classes.add(CatalinaInterceptor.class);
//        classes.add(DispatcherInterceptor.class);
//        classes.add(DubboContextProxyInterceptor.class);
//        classes.add(DubboInvokerInterceptor.class);
//        classes.add(DubboProxyInterceptor.class);
//        classes.add(FilterInterceptor.class);
//        classes.add(HttpClient3xInterceptor.class);
//        classes.add(HttpClient4xInterceptor.class);
//        classes.add(IbatisPlusInterceptor.class);
//        classes.add(IbatisSessionInterceptor.class);
//        classes.add(OkHttp3xInterceptor.class);
//        classes.add(ServletInvocableHandlerMethodInterceptor.class);
//        classes.add(SpringInterceptor.class);

        for (Class<?> aClass : classes) {
            if (Interceptor.class.isAssignableFrom(aClass)) {
                try {
                    Interceptor instance = (Interceptor) aClass.newInstance();
                    Agent.log(Level.INFO, "注册插件: " + instance.getClass().getTypeName());
                    transform = transform.type(instance.type())
                            .transform((builder, typeDescription, classLoader, javaModule) -> {
                                return instance.transform(builder);
                            });

                } catch (Exception ignored) {
                }
            }
        }
        return transform;
    }

    @Override
    public ElementMatcher<? super TypeDescription> type() {
        return null;
    }


    @Override
    public void setAddress(String address) {

    }

    @Override
    public void setParameter(JSONObject parameter) {

    }
}
