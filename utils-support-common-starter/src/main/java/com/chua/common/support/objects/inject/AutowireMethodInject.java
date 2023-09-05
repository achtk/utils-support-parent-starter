package com.chua.common.support.objects.inject;

import com.chua.common.support.objects.definition.element.AnnotationDescribe;
import com.chua.common.support.objects.definition.element.FieldDescribe;
import com.chua.common.support.objects.definition.element.MethodDescribe;
import com.chua.common.support.objects.provider.ObjectProvider;
import com.chua.common.support.objects.source.TypeDefinitionSourceFactory;

import static com.chua.common.support.constant.ContextConstant.AUTOWIRE;

/**
 * 字段注入器
 * @author CH
 */
public class AutowireMethodInject implements MethodInject{

    @Override
    public boolean inject(TypeDefinitionSourceFactory typeDefinitionSourceFactory, FieldDescribe fieldDescribe, MethodDescribe methodDescribe, Object bean) throws Exception {
        AnnotationDescribe annotationDescribe = fieldDescribe.getAnnotationDescribe(AUTOWIRE);
        if(null == annotationDescribe) {
            return false;
        }

        Class<?> type = fieldDescribe.getType();
        ObjectProvider<?> objectProvider = typeDefinitionSourceFactory.getBean(type);
        if(!objectProvider.isEmpty()) {
            Object value = objectProvider.get();
            methodDescribe.execute(bean, new Object[]{value});
            return true;
        }

        return false;
    }
}
