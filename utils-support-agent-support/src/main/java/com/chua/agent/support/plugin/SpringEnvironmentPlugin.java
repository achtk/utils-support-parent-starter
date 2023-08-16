package com.chua.agent.support.plugin;

import com.chua.agent.support.store.AgentStore;
import com.chua.agent.support.utils.ClassUtils;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.*;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.chua.agent.support.store.AgentStore.*;

/**
 * spring环境
 * @author CH
 */
public class SpringEnvironmentPlugin implements Plugin{
    @Override
    public String name() {
        return "spring-environment";
    }

    private static final AtomicBoolean APPLICATION_STATUS = new AtomicBoolean(false);
    private static final AtomicBoolean UNIFORM_ADDRESS_STATUS = new AtomicBoolean(false);
    private static final AtomicBoolean UNIFORM_OPEN_STATUS = new AtomicBoolean(false);
    private static final AtomicBoolean UNIFORM_PROTOCOL_STATUS = new AtomicBoolean(false);



    @Override
    public DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<?> transform(DynamicType.Builder<?> builder) {
        return builder.method(ElementMatchers.named("postProcessEnvironment"))
                .intercept(MethodDelegation.to(SpringEnvironmentPlugin.class));
    }

    @Override
    public ElementMatcher<? super TypeDescription> type() {
        return ElementMatchers.hasSuperType(ElementMatchers.named(
                "org.springframework.boot.env.EnvironmentPostProcessor"));
    }

    @RuntimeType
    public static Object before(@Origin Method method,
                                @This Object obj,
                                @AllArguments Object[] allArguments,
                                @SuperCall Callable<?> callable) throws Exception {

        Object environment = allArguments[0];
        if(null == AgentStore.APPLICATION_NAME) {
            checkApplicationName(environment);
        }


        if(null == UNIFORM_OPEN) {
            checkUniformOpen(environment);
        }

        if(null != UNIFORM_OPEN) {
            if(null == AgentStore.UNIFORM_ADDRESS) {
                checkUniformAddress(environment);
            }

            if(null == UNIFORM_PROTOCOL) {
                checkUniformProtocol(environment);
            }
        }


        return callable.call();
    }

    private static void checkUniformOpen(Object environment) {
        if(!UNIFORM_OPEN_STATUS.get()) {
            AgentStore.setUniformOpen((Boolean) ClassUtils.invoke("getProperty", environment,
                    "plugin.configuration.uniform.open", Boolean.class, true));
            if(null != UNIFORM_OPEN) {
                UNIFORM_OPEN_STATUS.set(true);
            }
        }
    }

    /**
     * 检测统一管理服务器协议
     * @param environment 环境
     */
    private static void checkUniformProtocol(Object environment) {
        if(!UNIFORM_PROTOCOL_STATUS.get()) {
            UNIFORM_PROTOCOL = (String) ClassUtils.invoke("getProperty", environment,
                    "plugin.configuration.uniform.protocol", "MQ");
            if(null != UNIFORM_PROTOCOL) {
                UNIFORM_PROTOCOL_STATUS.set(true);
            }
        }
    }

    /**
     * 检测统一管理服务器地址
     * @param environment 环境
     */
    private static void checkUniformAddress(Object environment) {
        if(!UNIFORM_ADDRESS_STATUS.get()) {
            setUniformAddress((String)ClassUtils.invoke(
                    "getProperty", environment, "plugin.configuration.uniform.address", "127.0.0.1:23579"));
            if(null != AgentStore.UNIFORM_ADDRESS) {
                UNIFORM_ADDRESS_STATUS.set(true);
            }
        }
    }

    /**
     * 检测应用名称
     * @param environment 应用名称
     */
    private static void checkApplicationName(Object environment) {
        if(!APPLICATION_STATUS.get()) {
            AgentStore.APPLICATION_NAME = (String) ClassUtils.invoke(
                    "getProperty", environment, "spring.application.name");
            if(null != AgentStore.APPLICATION_NAME) {
                APPLICATION_STATUS.set(true);
            }
        }
    }

}
