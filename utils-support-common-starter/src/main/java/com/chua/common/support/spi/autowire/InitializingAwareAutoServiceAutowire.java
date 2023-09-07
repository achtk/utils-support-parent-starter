package com.chua.common.support.spi.autowire;

import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.objects.definition.element.TypeDescribe;
import com.chua.common.support.utils.ClassUtils;

/**
 * initial
 *
 * @author CH
 */
public class InitializingAwareAutoServiceAutowire implements ServiceAutowire {

    private static final String SPRING = "org.springframework.beans.factory.InitializingBean";
    private static Class<?> SPRING_TYPE;

    static {
        if (ClassUtils.isPresent(SPRING)) {
            SPRING_TYPE = ClassUtils.forName(SPRING);
        }
    }

    @Override
    public Object autowire(Object object) {
        if (object instanceof InitializingAware) {
            ((InitializingAware) object).afterPropertiesSet();
        }

        if (null != SPRING_TYPE && SPRING_TYPE.isAssignableFrom(object.getClass())) {
            TypeDescribe typeDescribe = TypeDescribe.create(object);
            typeDescribe.getMethodDescribe("afterPropertiesSet").executeSelf();
        }
        return null;
    }

    @Override
    public Object createBean(Class<?> implClass) {
        return null;
    }
}
