package com.chua.common.support.context.value;


import com.chua.common.support.context.annotation.AutoValue;
import com.chua.common.support.context.resolver.ValueExpressionResolver;

import java.lang.reflect.Field;

/**
 * 注入
 *
 * @author CH
 * @see com.chua.common.support.context.annotation.AutoValue
 */
public class AutoValueValueExpressionResolver implements ValueExpressionResolver {

    @Override
    public String getExpression(Field field) {
        AutoValue autoValue = field.getDeclaredAnnotation(AutoValue.class);
        return null == autoValue ? null : autoValue.value();
    }
}
