package com.chua.common.support.objects.definition;

import com.chua.common.support.objects.ObjectContext;
import com.chua.common.support.utils.ClassUtils;

/**
 * 定义
 * @author CH
 */
public class ObjectTypeDefinition extends ClassTypeDefinition{


    private final Object bean;
    private final ObjectContext context;

    public ObjectTypeDefinition(Object bean, ObjectContext context) {
        super(ClassUtils.toType(bean), context);
        this.bean = bean;
        this.context = context;
    }


    @Override
    public Object getObject() {
        return bean;
    }
}
