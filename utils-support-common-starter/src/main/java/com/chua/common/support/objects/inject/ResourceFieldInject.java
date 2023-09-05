package com.chua.common.support.objects.inject;

import com.chua.common.support.objects.definition.element.AnnotationDescribe;
import com.chua.common.support.objects.definition.element.FieldDescribe;
import com.chua.common.support.objects.provider.ObjectProvider;
import com.chua.common.support.objects.source.TypeDefinitionSourceFactory;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.MapUtils;
import com.chua.common.support.utils.StringUtils;

import java.util.Map;

import static com.chua.common.support.constant.ContextConstant.RESOURCE_TYPE;

/**
 * 字段注入器
 * @author CH
 */
public class ResourceFieldInject implements FieldInject{

    @Override
    public boolean inject(TypeDefinitionSourceFactory typeDefinitionSourceFactory, FieldDescribe fieldDescribe, Object bean) throws Exception {
        AnnotationDescribe annotationDescribe = fieldDescribe.getAnnotationDescribe(RESOURCE_TYPE);
        if(null == annotationDescribe) {
            return false;
        }

        Map<String, Object> value1 = annotationDescribe.value();
        String name = MapUtils.getString(value1, "name");
        Class<?> type = (Class<?>) MapUtils.getObject(value1, "type");
        if(ClassUtils.isObject(type)) {
            type = fieldDescribe.getType();
        }

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
