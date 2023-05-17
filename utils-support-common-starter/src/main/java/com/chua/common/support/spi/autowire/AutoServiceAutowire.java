package com.chua.common.support.spi.autowire;

import com.chua.common.support.reflection.describe.TypeDescribe;
import com.chua.common.support.reflection.describe.provider.MethodDescribeProvider;
import com.chua.common.support.utils.ClassUtils;

/**
 * IOC
 * @author CH
 */
public class AutoServiceAutowire implements ServiceAutowire{

    public static ServiceAutowire INSTANCE = new AutoServiceAutowire();
    static final String APPLICATION_CONTEXT = "org.springframework.context.ApplicationContext";
    static final String UTILS = "com.chua.common.support.extra.spring.SpringUtils";
    private static MethodDescribeProvider methodDescribe;

    static {
        if(ClassUtils.isPresent(APPLICATION_CONTEXT) && ClassUtils.isPresent(UTILS)) {
            TypeDescribe typeDescribe = new TypeDescribe(UTILS);
            methodDescribe = typeDescribe.getMethodDescribe("getApplicationContext")
                    .isChain()
                    .getMethodDescribe("getAutowireCapableBeanFactory")
                    .isChain()
                    .getMethodDescribe("autowireBean");

        }
    }

    @Override
    public Object autowire(Object object) {
        if(null == object) {
            return null;
        }

        if(null != methodDescribe) {
            methodDescribe.executeThis(object);
        }
        return object;
    }
}
