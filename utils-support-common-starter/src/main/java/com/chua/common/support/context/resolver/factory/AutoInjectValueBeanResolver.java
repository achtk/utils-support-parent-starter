package com.chua.common.support.context.resolver.factory;

import com.chua.common.support.context.annotation.AutoInject;
import com.chua.common.support.context.factory.ConfigurableBeanFactory;
import com.chua.common.support.context.resolver.ValueBeanResolver;
import com.chua.common.support.context.value.BeanValue;

import java.lang.reflect.Field;

/**
 * 值适配器
 *
 * @author CH
 */
public class AutoInjectValueBeanResolver implements ValueBeanResolver {
    @Override
    public BeanValue getExpression(Field field, ConfigurableBeanFactory context) {
        AutoInject autoInject = field.getDeclaredAnnotation(AutoInject.class);
        return new BeanValue(null == autoInject ? null : autoInject.value(), null != autoInject);
    }
}
