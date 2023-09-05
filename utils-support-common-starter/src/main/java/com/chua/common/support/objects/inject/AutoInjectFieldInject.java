package com.chua.common.support.objects.inject;

import com.chua.common.support.objects.definition.element.FieldDescribe;
import com.chua.common.support.objects.provider.ObjectProvider;
import com.chua.common.support.objects.scanner.annotations.AutoInject;
import com.chua.common.support.objects.source.TypeDefinitionSourceFactory;
import com.chua.common.support.utils.StringUtils;

/**
 * 字段注入器
 * @author CH
 */
public class AutoInjectFieldInject implements FieldInject{

    @Override
    public boolean inject(TypeDefinitionSourceFactory typeDefinitionSourceFactory, FieldDescribe fieldDescribe, Object bean) throws Exception {
        AutoInject autoInject = fieldDescribe.getAnnotation(AutoInject.class);
        if(null == autoInject) {
            return false;
        }
        Class<?> type = fieldDescribe.getType();
        String name = autoInject.value();
        if(StringUtils.isNotBlank(name)) {
            Object value = typeDefinitionSourceFactory.getBean(name, type);
            fieldDescribe.setValue(value, bean);
            return true;
        }

        ObjectProvider<?> objectProvider = typeDefinitionSourceFactory.getBean(type);
        if(!objectProvider.isEmpty()) {
            Object value = objectProvider.get();
            fieldDescribe.setValue(value, bean);
            return true;
        }

        return false;
    }
}
