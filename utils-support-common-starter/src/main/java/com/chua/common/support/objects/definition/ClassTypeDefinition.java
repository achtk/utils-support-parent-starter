package com.chua.common.support.objects.definition;

import com.chua.common.support.objects.ObjectContext;

/**
 * 定义
 * @author CH
 */
public class ClassTypeDefinition implements TypeDefinition{


    private final Class<?> type;
    protected final ObjectContext context;

    public ClassTypeDefinition(Class<?> type, ObjectContext context) {
        this.type = type;
        this.context = context;
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public Object getObject() {
        return null;
    }
}
