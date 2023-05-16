package com.chua.common.support.reflection.describe.processor.impl;

import com.chua.common.support.annotations.DefaultValue;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.reflection.describe.ParameterDescribe;

/**
 * 字段注解注解扫描
 *
 * @author CH
 */
public class DefaultValueMethodAnnotationPostProcessor extends AbstractParameterAnnotationPostProcessor<DefaultValue> {

    @Override
    public Object proxy(int index, ParameterDescribe parameterDescribe, Object arg) {
        DefaultValue annotationValue = getAnnotationValue(parameterDescribe);
        return null == arg ? Converter.convertIfNecessary(annotationValue.value(), parameterDescribe.returnClassType()) : arg;
    }

    @Override
    public Class<DefaultValue> getAnnotationType() {
        return DefaultValue.class;
    }
}
