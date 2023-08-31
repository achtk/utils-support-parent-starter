package com.chua.common.support.extra.el.baseutil.bytecode.support;

import com.chua.common.support.extra.el.baseutil.bytecode.annotation.AnnotationMetadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;

/**
 * 基础类
 *
 * @author CH
 */
public interface AnnotationContext {
    /**
     * 是否满足要求的注解
     *
     * @param annotationType 注解
     * @return 是否存在
     */
    boolean isAnnotationPresent(Class<? extends Annotation> annotationType);

    /**
     * 获取注解
     *
     * @param annotationType 注解类
     * @param <E>            类型
     * @return 注解
     */

    <E extends Annotation> E getAnnotation(Class<E> annotationType);

    /**
     * 获取注解
     *
     * @param annotationType 注解类
     * @param <E>            类型
     * @return 注解
     */

    <E extends Annotation> List<E> getAnnotations(Class<E> annotationType);

    /**
     * 获取注解
     *
     * @param annotationType 注解类
     * @return 注解
     */

    AnnotationMetadata getAnnotationMetadata(Class<? extends Annotation> annotationType);

    /**
     * 获取注解
     *
     * @param annotationType 注解类
     * @return 注解
     */

    List<AnnotationMetadata> listAnnotationMetadata(Class<? extends Annotation> annotationType);

    SupportOverrideAttributeAnnotationContextFactoryAbstract ANNOTATION_CONTEXT_FACTORY = new SupportOverrideAttributeAnnotationContextFactoryAbstract();

    /**
     * 获取上下文
     *
     * @param element 元素
     * @return 上下文
     */
    static AnnotationContext getInstanceOn(AnnotatedElement element) {
        return ANNOTATION_CONTEXT_FACTORY.get(element);
    }

    /**
     * 是否满足要求的注解
     *
     * @param annotationType 注解
     * @param element 元素
     * @return 是否存在
     */
    static boolean isAnnotationPresent(Class<? extends Annotation> annotationType, AnnotatedElement element) {
        AnnotationContext annotationContext = ANNOTATION_CONTEXT_FACTORY.get(element);
        return annotationContext.isAnnotationPresent(annotationType);
    }

    /**
     * 获取注解
     *
     * @param annotationType 注解类
     * @param element        元素
     * @param <E>            类型
     * @return 注解
     */
    static <E extends Annotation> E getAnnotation(Class<E> annotationType, AnnotatedElement element) {
        return ANNOTATION_CONTEXT_FACTORY.get(element).getAnnotation(annotationType);
    }
    /**
     * 获取注解
     *
     * @param annotationType 注解类
     * @param element        元素
     * @param <E>            类型
     * @return 注解
     */
    static <E extends Annotation> List<E> getAnnotations(Class<E> annotationType, AnnotatedElement element) {
        return ANNOTATION_CONTEXT_FACTORY.get(element).getAnnotations(annotationType);
    }
    /**
     * 获取注解
     *
     * @param annotationType 注解类
     * @param element        元素
     * @return 注解
     */
    static AnnotationMetadata getAnnotationMetadata(Class<? extends Annotation> annotationType, AnnotatedElement element) {
        return ANNOTATION_CONTEXT_FACTORY.get(element).getAnnotationMetadata(annotationType);
    }
    /**
     * 获取注解
     *
     * @param annotationType 注解类
     * @param annotatedElement        元素
     * @return 注解
     */
    static List<AnnotationMetadata> listAnnotationMetadata(Class<? extends Annotation> annotationType, AnnotatedElement annotatedElement) {
        return ANNOTATION_CONTEXT_FACTORY.get(annotatedElement).listAnnotationMetadata(annotationType);
    }
    /**
     * 获取注解
     *
     * @param resourceName 名称
     * @return 注解
     */
    static AnnotationContext get(String resourceName) {
        return ANNOTATION_CONTEXT_FACTORY.get(resourceName);
    }
}
