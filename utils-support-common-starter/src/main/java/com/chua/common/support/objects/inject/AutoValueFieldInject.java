package com.chua.common.support.objects.inject;

import com.chua.common.support.converter.Converter;
import com.chua.common.support.objects.definition.element.FieldDescribe;
import com.chua.common.support.objects.scanner.annotations.AutoValue;
import com.chua.common.support.objects.source.TypeDefinitionSourceFactory;
import com.chua.common.support.utils.StringUtils;

/**
 * 字段注入器
 * @author CH
 */
public class AutoValueFieldInject implements FieldInject{

    @Override
    public boolean inject(TypeDefinitionSourceFactory typeDefinitionSourceFactory, FieldDescribe fieldDescribe, Object bean) throws Exception {
        AutoValue autoValue = fieldDescribe.getAnnotation(AutoValue.class);
        if(null == autoValue) {
            return false;
        }

        String name = autoValue.value();
        if(StringUtils.isNotBlank(name)) {
            Object value = typeDefinitionSourceFactory.getEnvironment().get(name);
            fieldDescribe.setValue(Converter.convertIfNecessary(value, fieldDescribe.getType()), bean);
            return true;

        }

        return false;
    }
}
