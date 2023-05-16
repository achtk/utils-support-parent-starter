package com.chua.common.support.context.environment;

import com.chua.common.support.constant.Action;
import com.chua.common.support.converter.Converter;

import java.lang.reflect.Field;

/**
 * 监听
 * @author CH
 */
public class FieldEnvironmentListener implements EnvironmentListener{
    private final Field field;
    private final Object tpl;
    private final String expression;

    public FieldEnvironmentListener(Field field, Object tpl, String expression) {
        this.field = field;
        this.tpl = tpl;
        this.expression = expression;
    }


    @Override
    public void doListener(String value, Action action) {
        if(action == Action.UPDATE) {
            try {
                field.set(tpl, Converter.convertIfNecessary(value, field.getType()));
            } catch (IllegalAccessException ignored) {
            }
        }
    }

    @Override
    public String getExpression() {
        return expression;
    }
}
