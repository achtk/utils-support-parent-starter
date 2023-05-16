package com.chua.common.support.context.value;


import com.chua.common.support.context.annotation.AutoInject;
import com.chua.common.support.context.annotation.AutoValue;
import com.chua.common.support.context.factory.ConfigurableBeanFactory;
import com.chua.common.support.context.resolver.ValueBeanResolver;

import java.lang.reflect.Field;

/**
 * 注入
 *
 * @author CH
 * @see AutoValue
 */
public class AutoValueBeanResolver implements ValueBeanResolver {

    @Override
    public BeanValue getExpression(Field field, ConfigurableBeanFactory context) {
        AutoInject autoValue = field.getDeclaredAnnotation(AutoInject.class);
        return null == autoValue ? null : new BeanValue(autoValue.value());
    }
}
