package com.chua.common.support.spi.autowire;

import com.chua.common.support.extra.spring.SpringUtils;
import com.chua.common.support.reflection.describe.MethodDescribe;
import com.chua.common.support.reflection.describe.TypeDescribe;
import com.chua.common.support.reflection.describe.provider.MethodDescribeProvider;
import com.chua.common.support.utils.ClassUtils;

/**
 * IOC
 * @author CH
 */
public class AutoServiceAutowire implements ServiceAutowire{

    public static final ServiceAutowire INSTANCE = new AutoServiceAutowire();
    static final String APPLICATION_CONTEXT = "org.springframework.context.ApplicationContext";
    private static MethodDescribeProvider methodDescribe;

    static {
        if(ClassUtils.isPresent(APPLICATION_CONTEXT)) {
            TypeDescribe typeDescribe = new TypeDescribe(SpringUtils.class);
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
