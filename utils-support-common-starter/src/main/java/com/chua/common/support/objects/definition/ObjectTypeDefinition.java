package com.chua.common.support.objects.definition;

import com.chua.common.support.objects.ObjectContext;
import com.chua.common.support.objects.source.TypeDefinitionSourceFactory;
import com.chua.common.support.utils.ClassUtils;

/**
 * 定义
 *
 * @author CH
 */
public class ObjectTypeDefinition extends ClassTypeDefinition {


    private final String name;
    private final Object bean;

    public ObjectTypeDefinition(Object bean) {
        this(ClassUtils.toType(bean).getTypeName(), bean);
    }

    public ObjectTypeDefinition(String name, Object bean) {
        super(ClassUtils.toType(bean));
        this.name = name;
        this.bean = bean;
    }

    public ObjectTypeDefinition(String name, Object bean, ObjectContext context) {
        super(ClassUtils.toType(bean), context);
        this.name = name;
        this.bean = bean;
    }

    @Override
    public String[] getName() {
        return new String[]{name};
    }

    @Override
    public Object getObject() {
        return bean;
    }


    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public <T> T newInstance(TypeDefinitionSourceFactory typeDefinitionSourceFactory) {
        return (T) bean;
    }
}
