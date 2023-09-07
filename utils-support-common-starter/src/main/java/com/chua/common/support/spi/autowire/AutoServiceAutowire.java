package com.chua.common.support.spi.autowire;

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
    public static final String UTILS = "com.chua.starter.common.support.configuration.SpringBeanUtils";

    private static final String SPRING_AUTO = "com.chua.starter.common.support.spi.SpringServiceAutowire";

    public static final List<ServiceAutowire> AUTOWIRES = new LinkedList<>();


    private static ServiceAutowire springServiceAutowire;

    static {
        if (ClassUtils.isPresent(SPRING_AUTO)) {
            springServiceAutowire = ClassUtils.forObject(SPRING_AUTO);
        }
        AUTOWIRES.add(new InitializingAwareAutoServiceAutowire());
    }

    @Override
    public Object autowire(Object object) {
        if (null == object) {
            return null;
        }

        if (null != springServiceAutowire) {
            springServiceAutowire.autowire(object);
        }

        for (ServiceAutowire serviceAutowire : AUTOWIRES) {
            serviceAutowire.autowire(object);
        }
        return object;
    }

    @Override
    public Object createBean(Class<?> implClass) {
        return null;
    }
}
