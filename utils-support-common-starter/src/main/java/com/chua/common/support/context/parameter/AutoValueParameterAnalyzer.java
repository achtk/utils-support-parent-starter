package com.chua.common.support.context.parameter;

import com.chua.common.support.context.annotation.AutoValue;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.lang.profile.Profile;
import com.chua.common.support.reflection.describe.ParameterDescribe;

/**
 * 注入
 * @see com.chua.common.support.context.annotation.AutoValue
 * @author CH
 */
public class AutoValueParameterAnalyzer implements ParameterAnalyzer{
    @Override
    public Object analyzer(ParameterDescribe parameterDescribe, Profile profile) {
        AutoValue value = parameterDescribe.getAnnotationValue(AutoValue.class);
        if (null != value) {
            return Converter.convertIfNecessary(profile.getObject(value.value(), value.defaultValue()), parameterDescribe.returnClassType());
        }
        return null;
    }
}
