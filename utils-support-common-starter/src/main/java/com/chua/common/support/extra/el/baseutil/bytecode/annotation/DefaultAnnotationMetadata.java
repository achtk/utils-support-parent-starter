package com.chua.common.support.extra.el.baseutil.bytecode.annotation;

import com.chua.common.support.extra.el.baseutil.bytecode.util.BytecodeUtil;

import java.util.List;
import java.util.Map;
/**
 * 基础类
 * @author CH
 */
public class DefaultAnnotationMetadata extends AbstractAnnotationMetadata
{

    public DefaultAnnotationMetadata(String typeName, Map<String, ValuePair> attributes, ClassLoader loader)
    {
        super(typeName, attributes, loader);
    }

    @Override
    public List<AnnotationMetadata> getPresentAnnotations()
    {
        if (presentAnnotations == null)
        {
            presentAnnotations = BytecodeUtil.findAnnotationsOnClass(type(), this.getClass().getClassLoader());
        }
        return presentAnnotations;
    }

    @Override
    public String toString()
    {
        return "DefaultAnnotationMetadata{" + "resourceName='" + resourceName + '\'' + ", attributes=" + attributes + '}';
    }
}
