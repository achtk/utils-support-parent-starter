package com.chua.common.support.extra.el.baseutil.bytecode.support;

import com.chua.common.support.extra.el.baseutil.bytecode.annotation.AnnotationMetadata;
import com.chua.common.support.extra.el.baseutil.bytecode.annotation.DefaultAnnotationMetadata;
import com.chua.common.support.extra.el.baseutil.bytecode.annotation.SupportOverrideAttributeAnnotationMetadata;
import com.chua.common.support.extra.el.baseutil.bytecode.util.BytecodeUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * 基础类
 *
 * @author CH
 */
public class SupportOverrideAttributeAnnotationContextFactoryAbstract extends AbstractCacheableAnnotationContextFactory {
    protected SupportOverrideAttributeAnnotationContextFactoryAbstract() {
    }

    private AnnotationContext castToSupportOverrideContext(List<AnnotationMetadata> annotationsOnClass) {
        List<AnnotationMetadata> cast = new LinkedList<AnnotationMetadata>();
        for (AnnotationMetadata each : annotationsOnClass) {
            SupportOverrideAttributeAnnotationMetadata castFrom = SupportOverrideAttributeAnnotationMetadata.castFrom((DefaultAnnotationMetadata) each);
            cast.add(castFrom);
        }
        return new DefaultAnnotationContext(cast);
    }

    @Override
    protected AnnotationContext build(Method method, ClassLoader classLoader) {
        List<AnnotationMetadata> annotationsOnMethod = BytecodeUtil.findAnnotationsOnMethod(method, classLoader);
        return castToSupportOverrideContext(annotationsOnMethod);
    }

    @Override
    protected AnnotationContext build(Field field, ClassLoader classLoader) {
        return castToSupportOverrideContext(BytecodeUtil.findAnnotationsOnField(field, classLoader));
    }

    @Override
    protected AnnotationContext build(String resourceName, ClassLoader classLoader) {
        List<AnnotationMetadata> annotationMetadataList = BytecodeUtil.findAnnotationsOnClass(resourceName, classLoader);
        return castToSupportOverrideContext(annotationMetadataList);
    }
}
