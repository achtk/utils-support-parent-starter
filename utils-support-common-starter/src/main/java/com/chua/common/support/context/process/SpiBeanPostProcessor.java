package com.chua.common.support.context.process;

import com.chua.common.support.context.definition.ObjectDefinition;
import com.chua.common.support.context.definition.TypeDefinition;
import com.chua.common.support.context.factory.ApplicationContextConfiguration;
import com.chua.common.support.spi.ServiceProvider;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 类型注解器
 *
 * @author CH
 */
@SuppressWarnings("ALL")
public class SpiBeanPostProcessor extends TypeBeanPostProcessor {

    public SpiBeanPostProcessor(ApplicationContextConfiguration contextConfiguration) {
        super(contextConfiguration);
    }

    @Override
    public <T> List<TypeDefinition<T>> postProcessInstantiation(String bean, Class<T> targetType) {
        List rs = new LinkedList<>();
        T extension = ServiceProvider.of(targetType).getExtension(bean);
        if(null != extension) {
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
        List rs = new LinkedList<>();
        Map<String, T> list = ServiceProvider.of(targetType).list();
        for (Map.Entry<String, T> entry : list.entrySet()) {
            rs.add(ObjectDefinition.of(entry.getValue()).order(Integer.MIN_VALUE));
        }
        return rs;
    }

}
