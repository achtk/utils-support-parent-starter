package com.chua.common.support.context.value;

import com.chua.common.support.context.environment.Environment;
import com.chua.common.support.context.environment.FieldEnvironmentListener;
import com.chua.common.support.context.factory.ConfigurableBeanFactory;
import com.chua.common.support.context.resolver.ValueExpressionResolver;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.StringUtils;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * 注入值
 *
 * @author CH
 */
public class AutoValueHandler implements InitializingAware {
    private final Field field;
    private final Object tpl;
    private final ConfigurableBeanFactory context;

    public <T> AutoValueHandler(Field field, T tpl, ConfigurableBeanFactory context) {
        this.field = field;
        this.field.setAccessible(true);
        this.tpl = tpl;
        this.context = context;
    }

    /**
     * 刷新
     */
    public void refresh() {
        Object value = null;
        String expression = null;
        Environment environment = context.getEnvironment();
        Map<String, ValueExpressionResolver> list = ServiceProvider.of(ValueExpressionResolver.class).list();
        for (ValueExpressionResolver resolver : list.values()) {
            expression = resolver.getExpression(field);
            if (StringUtils.isNullOrEmpty(expression)) {
                continue;
            }

            String property = environment.getProperty(expression);
            if (StringUtils.isNullOrEmpty(property)) {
                continue;
            }

            value = property;
        }

        if (null == value || null == expression) {
            return;
        }

        try {
            field.set(tpl, Converter.convertIfNecessary(value, field.getType()));
            environment.addListener(new FieldEnvironmentListener(field, tpl, expression));
        } catch (IllegalAccessException ignored) {
        }
    }

    @Override
    public void afterPropertiesSet() {
        refresh();
    }
}
