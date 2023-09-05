package com.chua.common.support.objects.inject;

import com.chua.common.support.objects.definition.element.AnnotationDescribe;
import com.chua.common.support.objects.definition.element.FieldDescribe;
import com.chua.common.support.objects.provider.ObjectProvider;
import com.chua.common.support.objects.source.TypeDefinitionSourceFactory;

import static com.chua.common.support.constant.ContextConstant.AUTOWIRE;

/**
 * 字段注入器
 * @author CH
 */
public class AutowireFieldInject implements FieldInject{

    @Override
    public boolean inject(TypeDefinitionSourceFactory typeDefinitionSourceFactory, FieldDescribe fieldDescribe, Object bean) throws Exception {
        AnnotationDescribe annotationDescribe = fieldDescribe.getAnnotationDescribe(AUTOWIRE);
        if(null == annotationDescribe) {
            return false;
        }

        Class<?> type = fieldDescribe.getType();
        ObjectProvider<?> objectProvider = typeDefinitionSourceFactory.getBean(type);
        if(!objectProvider.isEmpty()) {
            Object value = objectProvider.get();
            fieldDescribe.setValue(value, bean);
            return true;
        }

        return false;
    }
}
