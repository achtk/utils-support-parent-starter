package com.chua.common.support.extra.el.baseutil.bytecode.support;

import com.chua.common.support.extra.el.baseutil.bytecode.annotation.AnnotationMetadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;

public interface AnnotationContext
{
    boolean isAnnotationPresent(Class<? extends Annotation> ckass);

    <E extends Annotation> E getAnnotation(Class<E> ckass);

    <E extends Annotation> List<E> getAnnotations(Class<E> ckass);

    AnnotationMetadata getAnnotationMetadata(Class<? extends Annotation> ckass);

    List<AnnotationMetadata> getAnnotationMetadatas(Class<? extends Annotation> ckass);

    SupportOverrideAttributeAnnotationContextFactory ANNOTATION_CONTEXT_FACTORY = new SupportOverrideAttributeAnnotationContextFactory();

    static AnnotationContext getInstanceOn(AnnotatedElement element)
    {
        return ANNOTATION_CONTEXT_FACTORY.get(element);
    }

    static boolean isAnnotationPresent(Class<? extends Annotation> ckass, AnnotatedElement element)
    {
        AnnotationContext annotationContext = ANNOTATION_CONTEXT_FACTORY.get(element);
        return annotationContext.isAnnotationPresent(ckass);
    }

    static <E extends Annotation> E getAnnotation(Class<E> ckass, AnnotatedElement element)
    {
        return ANNOTATION_CONTEXT_FACTORY.get(element).getAnnotation(ckass);
    }

    static <E extends Annotation> List<E> getAnnotations(Class<E> ckass, AnnotatedElement element)
    {
        return ANNOTATION_CONTEXT_FACTORY.get(element).getAnnotations(ckass);
    }

    static AnnotationMetadata getAnnotationMetadata(Class<? extends Annotation> ckass, AnnotatedElement element)
    {
        return ANNOTATION_CONTEXT_FACTORY.get(element).getAnnotationMetadata(ckass);
    }

    static List<AnnotationMetadata> getAnnotationMetadatas(Class<? extends Annotation> ckass, AnnotatedElement annotatedElement)
    {
        return ANNOTATION_CONTEXT_FACTORY.get(annotatedElement).getAnnotationMetadatas(ckass);
    }

    static AnnotationContext get(String resourceName)
    {
        return ANNOTATION_CONTEXT_FACTORY.get(resourceName);
    }
}
