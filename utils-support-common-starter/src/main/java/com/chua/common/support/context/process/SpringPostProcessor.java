package com.chua.common.support.context.process;

import com.chua.common.support.context.definition.ObjectDefinition;
import com.chua.common.support.context.definition.TypeDefinition;
import com.chua.common.support.context.factory.ApplicationContextConfiguration;
import com.chua.common.support.reflection.describe.MethodDescribe;
import com.chua.common.support.reflection.describe.TypeDescribe;
import com.chua.common.support.reflection.describe.provider.MethodDescribeProvider;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.spi.autowire.InitializingAwareAutoServiceAutowire;
import com.chua.common.support.utils.ClassUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * spring
 *
 * @author CH
 */
public class SpringPostProcessor extends TypeBeanPostProcessor {
    static final String APPLICATION_CONTEXT = "org.springframework.context.ApplicationContext";
    static final String UTILS = "com.chua.starter.common.support.configuration.SpringBeanUtils";
    static TypeDescribe typeDescribe1;

    static {
        if (ClassUtils.isPresent(APPLICATION_CONTEXT) && ClassUtils.isPresent(UTILS)) {
            TypeDescribe typeDescribe = new TypeDescribe(UTILS);
            typeDescribe1 = typeDescribe.getMethodDescribe("getApplicationContext").isChain();

        }
    }

    public SpringPostProcessor(ApplicationContextConfiguration contextConfiguration) {
        super(contextConfiguration);
    }

    @Override
    public <T> List<TypeDefinition<T>> postProcessInstantiation(String bean, Class<T> targetType) {
        if(null == typeDescribe1) {
            return Collections.emptyList();
        }

        List rs = new LinkedList<>();
        MethodDescribe methodDescribe = typeDescribe1.getMethodDescribe("getBean", String.class, Class.class);
        Object extension = methodDescribe.doChainSelf(bean, targetType).entity();
        if (null != extension) {
            rs.add(ObjectDefinition.of(extension).order(Integer.MIN_VALUE));
        }
        return rs;
    }

    @Override
    public boolean isValid(TypeDefinition definition) {
        return false;
    }

    @Override
    public <T> List<TypeDefinition<T>> postProcessInstantiation(Class<T> targetType) {
        if(null == typeDescribe1) {
            return Collections.emptyList();
        }
        List rs = new LinkedList<>();
        MethodDescribe methodDescribe = typeDescribe1.getMethodDescribe("getBeansOfType", Class.class);
        Map<String, T> list = (Map<String, T>) methodDescribe.isChain(methodDescribe.entity(), targetType).getBean();
        if (null == list) {
            return rs;
        }

        for (Map.Entry<String, T> entry : list.entrySet()) {
            rs.add(ObjectDefinition.of(entry.getValue()).order(Integer.MIN_VALUE));
        }
        return rs;
    }

}
