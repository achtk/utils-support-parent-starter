package com.chua.common.support.objects.definition.element;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

/**
 * 节点
 *
 * @author CH
 * @since 2023/09/01
 */
public interface ElementDescribe {

    /**
     * 名称
     *
     * @return {@link String}
     */
    String name();


    /**
     * get类型
     *
     * @return {@link Class}<{@link ?}>
     */
    Class<?> getType();

    /**
     * 参数
     *
     * @return {@link List}<{@link ParameterDescribe}>
     */
    Map<String, ParameterDescribe> parameters();


    /**
     * 注释
     *
     * @return {@link List}<{@link AnnotationDescribe}>
     */
    Map<String, AnnotationDescribe> annotations();

    /**
     * 返回类型
     *
     * @return {@link String}
     */
    String returnType();

    /**
     * 异常类型
     *
     * @return {@link String}
     */
    List<String> exceptionType();

    /**
     * 值
     *
     * @return 值
     */
    Map<String, Object> value();


    /**
     * 获取索引
     *
     * @return int
     */
    default int getIndex() {
        return 0;
    }

    /**
     * 添加bean名称
     *
     * @param name 名称
     */
    void addBeanName(String name);

    /**
     * 是否有注解
     *
     * @param annotationType 注解类型
     * @return boolean
     */
   default boolean hasAnnotation(Class<? extends Annotation> annotationType) {
       return hasAnnotation(annotationType.getTypeName());
   }
    /**
     * 是否有注解
     *
     * @param annotationType 注解类型
     * @return boolean
     */
   boolean hasAnnotation(String annotationType);
    /**
     * 是否有任意注解
     *
     * @param annotationType 注解类型
     * @return boolean
     */
    default boolean hasAnnotation(String[] annotationType) {
        for (String s : annotationType) {
            if(hasAnnotation(s)) {
                return true;
            }
        }

        return false;
    }
    /**
     * 是否有任意注解
     *
     * @param annotationType 注解类型
     * @return boolean
     */
    default boolean hasAnnotation(Class<?>[] annotationType) {
        for (Class<?> s : annotationType) {
            if(hasAnnotation(s.getTypeName())) {
                return true;
            }
        }

        return false;
    }
    /**
     * 注解
     *
     * @param annotationType 注解类型
     * @return {@link T}
     */
    @SuppressWarnings("ALL")
    default <T extends Annotation>T getAnnotation(Class<T> annotationType) {
        return (T) getAnnotation(annotationType.getTypeName());
    }

    /**
     * 注解
     *
     * @param annotationType 注解类型
     * @return {@link Annotation}
     */
    Annotation getAnnotation(String annotationType);

    /**
     * 注解描述
     *
     * @param annotationType 注解类型
     * @return {@link AnnotationDescribe}
     */
    default AnnotationDescribe getAnnotationDescribe(Class<? extends Annotation> annotationType) {
        return null == annotationType ? null : getAnnotationDescribe(annotationType.getTypeName());
    }

    /**
     * 注解描述
     *
     * @param annotationType 注解类型
     * @return {@link AnnotationDescribe}
     */
    AnnotationDescribe getAnnotationDescribe(String annotationType);
}
