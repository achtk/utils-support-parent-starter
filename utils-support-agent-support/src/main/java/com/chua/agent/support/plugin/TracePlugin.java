package com.chua.agent.support.plugin;

import com.chua.agent.support.plugin.apm.Interceptor;
import com.chua.agent.support.utils.ClassUtils;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatcher;

import java.util.List;
import java.util.logging.Level;

import static com.chua.agent.support.store.AgentStore.log;

/**
 * 链路追踪
 * @author CH
 */
public class TracePlugin implements Plugin{
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
        for (Class<?> aClass : classes) {
            if (Interceptor.class.isAssignableFrom(aClass)) {
                try {
                    Interceptor instance = (Interceptor) aClass.newInstance();
                    log(Level.INFO, "注册插件: " + instance.getClass().getTypeName());
                    transform = transform.type(instance.type())
                            .transform((builder, typeDescription, classLoader, javaModule, a) -> {
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
}
