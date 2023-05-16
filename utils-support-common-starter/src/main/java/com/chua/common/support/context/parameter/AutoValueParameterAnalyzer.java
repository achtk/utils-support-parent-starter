package com.chua.common.support.context.parameter;

import com.chua.common.support.collection.ConfigureAttributes;
import com.chua.common.support.context.annotation.AutoValue;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.reflection.describe.ParameterDescribe;

/**
 * 注入
 * @see com.chua.common.support.context.annotation.AutoValue
 * @author CH
 */
public class AutoValueParameterAnalyzer implements ParameterAnalyzer{
    @Override
    public Object analyzer(ParameterDescribe parameterDescribe, ConfigureAttributes configureAttributes) {
        AutoValue value = parameterDescribe.getAnnotationValue(AutoValue.class);
        if (null != value) {
            return Converter.convertIfNecessary(configureAttributes.getOrDefault(value.value(), value.defaultValue()), parameterDescribe.returnClassType());
        }
        return null;
    }
}
