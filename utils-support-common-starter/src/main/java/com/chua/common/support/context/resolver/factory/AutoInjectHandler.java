package com.chua.common.support.context.resolver.factory;

import com.chua.common.support.context.factory.ConfigurableBeanFactory;
import com.chua.common.support.context.resolver.ValueBeanResolver;
import com.chua.common.support.context.value.BeanValue;
import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.StringUtils;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * 自动转配解释器
 *
 * @author CH
 */
public class AutoInjectHandler implements InitializingAware {
    private final Field field;
    private final Object tpl;
    private final ConfigurableBeanFactory context;

    public <T> AutoInjectHandler(Field field, T tpl, ConfigurableBeanFactory context) {
        this.field = field;
        this.tpl = tpl;
        this.context = context;
    }

    public void refresh() {
        String value = null;
        boolean exist = false;
        Map<String, ValueBeanResolver> list = ServiceProvider.of(ValueBeanResolver.class).list();
        for (ValueBeanResolver resolver : list.values()) {
            BeanValue beanValue = resolver.getExpression(field, context);
            if (null == beanValue) {
                continue;
            }


            if (!beanValue.isExsit()) {
                continue;
            }

            exist = beanValue.isExsit();
            value = beanValue.getValue();
            break;
        }

        if (!exist) {
            return;
        }

        Object bean = null;
        if (!StringUtils.isNullOrEmpty(value)) {
            bean = context.getBean(value, field.getType());
        } else {
            bean = context.getBean(field.getType());
        }

        if (null == bean) {
            return;
        }

        try {
            field.set(tpl, bean);
        } catch (IllegalAccessException ignored) {
        }
    }


    @Override
    public void afterPropertiesSet() {
        refresh();
    }
}
