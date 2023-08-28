package com.chua.common.support.extra.el.baseutil.bytecode.annotation;

import java.lang.annotation.Annotation;
import java.util.List;
/**
 * 基础类
 * @author CH
 */
public class ClassNotExistAnnotationMetadata implements AnnotationMetadata
{
    private String type;

    public ClassNotExistAnnotationMetadata(String type)
    {
        this.type = type;
    }

    @Override
    public boolean shouldIgnore()
    {
        return true;
    }

    @Override
    public ValuePair getAttribyte(String name)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<?> annotationType()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAnnotation(String name)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String type()
    {
        return type;
    }

    @Override
    public List<AnnotationMetadata> getPresentAnnotations()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Annotation annotation()
    {
        throw new UnsupportedOperationException();
    }
}
