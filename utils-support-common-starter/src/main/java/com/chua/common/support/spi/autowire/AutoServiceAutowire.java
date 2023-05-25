package com.chua.common.support.spi.autowire;

import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.reflection.describe.MethodDescribe;
import com.chua.common.support.reflection.describe.TypeDescribe;
import com.chua.common.support.reflection.describe.provider.MethodDescribeProvider;
import com.chua.common.support.utils.ClassUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * IOC
 *
 * @author CH
 */
public class AutoServiceAutowire implements ServiceAutowire {

    public static ServiceAutowire INSTANCE = new AutoServiceAutowire();
    static final String APPLICATION_CONTEXT = "org.springframework.context.ApplicationContext";
    static final String UTILS = "com.chua.starter.common.support.configuration.SpringBeanUtils";
    private static MethodDescribeProvider methodDescribe;

    public static final List<ServiceAutowire> AUTOWIRES = new LinkedList<>();

    static {
        AUTOWIRES.add(new InitializingAwareAutoServiceAutowire());
        if (ClassUtils.isPresent(APPLICATION_CONTEXT) && ClassUtils.isPresent(UTILS)) {
            TypeDescribe typeDescribe = new TypeDescribe(UTILS);
            methodDescribe = typeDescribe.getMethodDescribe("getApplicationContext")
                    .isChain()
                    .getMethodDescribe("getAutowireCapableBeanFactory")
                    .isChainSelf()
                    .getMethodDescribe("autowireBean");

        }
    }

    @Override
    public Object autowire(Object object) {
        if (null == object) {
            return null;
        }

        if (null != methodDescribe) {
            methodDescribe.executeSelf(object);
        }

        for (ServiceAutowire serviceAutowire : AUTOWIRES) {
            serviceAutowire.autowire(object);
        }
        return object;
    }
}
