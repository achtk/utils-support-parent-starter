package com.chua.common.support.utils;

import com.chua.common.support.collection.AnnotationAttributes;
import com.chua.common.support.placeholder.PropertyResolver;
import com.chua.common.support.reflection.FieldStation;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

import static java.lang.String.valueOf;
import static java.util.Arrays.asList;

/**
 * 注解工具
 *
 * @author CH
 */
public class AnnotationUtils {
    private static final String JAVA = "java.lang.";

    private static final String ALIAS_FOR = "org.springframework.core.annotation.AliasFor";
    public static final String ANNOTATED_ELEMENT_UTILS_CLASS_NAME = "org.springframework.core.annotation.AnnotatedElementUtils";

    private static final String MEMBER_VALUES = "memberValues";
    private static final Class<?>[] IGNORED = new Class<?>[]{
            Documented.class,
            Retention.class,
            Target.class,
            Repeatable.class,
            Inherited.class,
            Native.class,
            Inherited.class
    };

    /**
     * 获取注解
     *
     * @param implType 类型
     * @param spiClass 注解
     * @return 注解
     */
    public static <T extends Annotation> Map<Field, T> getAnnotations(Class<?> implType, Class<T> spiClass) {
        Map<Field, T> rs = new LinkedHashMap<>();
        ClassUtils.doWithFields(implType, field -> {
            T ann = null;
            if ((ann = field.getDeclaredAnnotation(spiClass)) == null) {
                return;
            }
            field.setAccessible(true);
            rs.put(field, ann);
        });

        return rs;
    }

    /**
     * 注解转Map
     *
     * @param annotation 注解
     * @return 注解值
     */
    public static Map<String, Object> asMap(Annotation annotation) {
        if (null == annotation) {
            return Collections.emptyMap();
        }

        InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotation);
        Field field = ClassUtils.findField(invocationHandler.getClass(), MEMBER_VALUES);
        if (null == field) {
            return Collections.emptyMap();
        }
        Map<String, Object> memberValues = (Map<String, Object>) ClassUtils.invoke(invocationHandler, field);
        Map<String, Object> stringObjectMap = asAnnotationMap(annotation);
        return MapUtils.merge(memberValues, stringObjectMap);
    }

    /**
     * 注解下的注解
     *
     * @param annotation 注解
     * @return Map
     */
    private static Map<String, Object> asAnnotationMap(Annotation annotation) {
        Map<String, Object> result = new HashMap<>(16);

        Class<? extends Annotation> aClass = annotation.annotationType();
        Annotation[] annotations = aClass.getDeclaredAnnotations();
        for (Annotation annotation1 : annotations) {
            if (ArrayUtils.contains(IGNORED, annotation1.annotationType())) {
                continue;
            }
            result.putAll(asMap(annotation1));
        }
        return result;
    }

    /**
     * Get the required attribute value
     *
     * @param attributes    {@link Map the annotation attributes} or {@link AnnotationAttributes}
     * @param attributeName the name of attribute
     * @param <T>           the type of attribute value
     * @return the attribute value if found
     * @throws IllegalStateException if attribute value can't be found
     * @since 1.0.6
     */
    public static <T> T getRequiredAttribute(Map<String, Object> attributes, String attributeName) {
        return getAttribute(attributes, attributeName, true);
    }

    /**
     * Get the attribute value the will
     *
     * @param attributes    {@link Map the annotation attributes} or {@link AnnotationAttributes}
     * @param attributeName the name of attribute
     * @param required      the required attribute or not
     * @param <T>           the type of attribute value
     * @return the attribute value if found
     * @throws IllegalStateException if attribute value can't be found
     * @since 1.0.6
     */
    public static <T> T getAttribute(Map<String, Object> attributes, String attributeName, boolean required) {
        T value = getAttribute(attributes, attributeName, null);
        if (required && value == null) {
            throw new IllegalStateException("The attribute['" + attributeName + "] is required!");
        }
        return value;
    }


    /**
     * Get the attribute value with default value
     *
     * @param attributes    {@link Map the annotation attributes} or {@link AnnotationAttributes}
     * @param attributeName the name of attribute
     * @param defaultValue  the default value of attribute
     * @param <T>           the type of attribute value
     * @return the attribute value if found
     * @since 1.0.6
     */
    public static <T> T getAttribute(Map<String, Object> attributes, String attributeName, T defaultValue) {
        T value = (T) attributes.get(attributeName);
        return value == null ? defaultValue : value;
    }

    @SuppressWarnings("ALL")
    public static AnnotationAttributes getAnnotationAttributes(Object type, Class<? extends Annotation> annotationType) {
        if(type instanceof Class) {
            return (AnnotationAttributes) getAnnotationAttributes(((Class<?>) type).getDeclaredAnnotation(annotationType), false);
        }

        if(type instanceof Method) {
            return (AnnotationAttributes) getAnnotationAttributes(((Method) type).getDeclaredAnnotation(annotationType), false);
        }

        if(type instanceof Field) {
            return (AnnotationAttributes) getAnnotationAttributes(((Field) type).getDeclaredAnnotation(annotationType), false);
        }

        if(type instanceof Constructor) {
            return (AnnotationAttributes) getAnnotationAttributes(((Constructor) type).getDeclaredAnnotation(annotationType), false);
        }
        return (AnnotationAttributes) getAnnotationAttributes(type.getClass().getDeclaredAnnotation(annotationType), false);
    }
    /**
     * Get the {@link AnnotationAttributes}
     *
     * @param annotation           specified {@link Annotation}
     * @param ignoreDefaultValue   whether ignore default value or not
     * @param ignoreAttributeNames the attribute names of annotation should be ignored
     * @return non-null
     * @see #getAnnotationAttributes(Annotation, PropertyResolver, boolean, String...)
     * @since 1.0.3
     */
    public static AnnotationAttributes getAnnotationAttributes(Annotation annotation, boolean ignoreDefaultValue,
                                                               String... ignoreAttributeNames) {
        return getAnnotationAttributes(annotation, null, ignoreDefaultValue, ignoreAttributeNames);
    }

    /**
     * Get the {@link AnnotationAttributes}
     *
     * @param annotation           specified {@link Annotation}
     * @param propertyResolver     {@link PropertyResolver} instance
     * @param ignoreDefaultValue   whether ignore default value or not
     * @param ignoreAttributeNames the attribute names of annotation should be ignored
     * @return non-null
     * @see #getAttributes(Annotation, PropertyResolver, boolean, String...)
     * @see #getAnnotationAttributes(AnnotatedElement, Class, PropertyResolver, boolean, String...)
     * @since 1.0.3
     */
    public static AnnotationAttributes getAnnotationAttributes(Annotation annotation, PropertyResolver propertyResolver,
                                                               boolean ignoreDefaultValue, String... ignoreAttributeNames) {
        return AnnotationAttributes.fromMap(getAttributes(annotation, propertyResolver, ignoreDefaultValue, ignoreAttributeNames));
    }

    /**
     * Get the {@link Annotation} attributes
     *
     * @param annotation           specified {@link Annotation}
     * @param propertyResolver     {@link PropertyResolver} instance
     * @param ignoreDefaultValue   whether ignore default value or not
     * @param ignoreAttributeNames the attribute names of annotation should be ignored
     * @return non-null
     * @since 1.0.2
     */
    public static Map<String, Object> getAttributes(Annotation annotation, PropertyResolver propertyResolver,
                                                    boolean ignoreDefaultValue, String... ignoreAttributeNames) {

        Map<String, Object> annotationAttributes = AnnotationUtils.getAnnotationAttributes(annotation);

        String[] actualIgnoreAttributeNames = ignoreAttributeNames;

        if (ignoreDefaultValue && !MapUtils.isEmpty(annotationAttributes)) {

            List<String> attributeNamesToIgnore = new LinkedList<String>(asList(ignoreAttributeNames));

            for (Map.Entry<String, Object> annotationAttribute : annotationAttributes.entrySet()) {
                String attributeName = annotationAttribute.getKey();
                Object attributeValue = annotationAttribute.getValue();
            }
            // extends the ignored list
            actualIgnoreAttributeNames = attributeNamesToIgnore.toArray(new String[attributeNamesToIgnore.size()]);
        }

        return getAttributes(annotationAttributes, propertyResolver, actualIgnoreAttributeNames);
    }

    /**
     * Get the {@link Annotation} attributes
     *
     * @param annotationAttributes the attributes of specified {@link Annotation}
     * @param propertyResolver     {@link PropertyResolver} instance
     * @param ignoreAttributeNames the attribute names of annotation should be ignored
     * @return non-null
     * @since 1.0.4
     */
    public static Map<String, Object> getAttributes(Map<String, Object> annotationAttributes,
                                                    PropertyResolver propertyResolver, String... ignoreAttributeNames) {

        Set<String> ignoreAttributeNamesSet = new HashSet<String>((Collection<? extends String>) arrayToList(ignoreAttributeNames));

        Map<String, Object> actualAttributes = new LinkedHashMap<String, Object>();

        for (Map.Entry<String, Object> annotationAttribute : annotationAttributes.entrySet()) {

            String attributeName = annotationAttribute.getKey();
            Object attributeValue = annotationAttribute.getValue();

            // ignore attribute name
            if (ignoreAttributeNamesSet.contains(attributeName)) {
                continue;
            }

            if (attributeValue instanceof String) {
                attributeValue = resolvePlaceholders(valueOf(attributeValue), propertyResolver);
            } else if (attributeValue instanceof String[]) {
                String[] values = (String[]) attributeValue;
                for (int i = 0; i < values.length; i++) {
                    values[i] = resolvePlaceholders(values[i], propertyResolver);
                }
                attributeValue = values;
            }
            actualAttributes.put(attributeName, attributeValue);
        }
        return actualAttributes;
    }

    private static String resolvePlaceholders(String attributeValue, PropertyResolver propertyResolver) {
        String resolvedValue = attributeValue;
        if (propertyResolver != null) {
            resolvedValue = propertyResolver.resolvePlaceholders(resolvedValue);
            resolvedValue = StringUtils.trimWhitespace(resolvedValue);
        }
        return resolvedValue;
    }

    /**
     * 获取注解
     *
     * @param annotation 注解
     * @return 集合
     */
    public static Map<String, Object> getAnnotationAttributes(Annotation annotation) {
        if (null == annotation) {
            return Collections.emptyMap();
        }
        Map<String, Object> result = new HashMap<>(AnnotationUtils.asMap(annotation));
        Class aClass = ClassUtils.forName(ALIAS_FOR);
        if (null != aClass) {
            List<Method> methods = ClassUtils.getMethods(annotation);
            methods.forEach(method -> {
                String name = method.getName();
                Annotation annotation2 = method.getDeclaredAnnotation(aClass);
                if (null != annotation2) {
                    Map<String, Object> annotationValue = getAnnotationValue(annotation2);
                    for (Map.Entry<String, Object> entry : annotationValue.entrySet()) {
                        String name1 = entry.getKey();
                        if (!result.containsKey(name1)) {
                            result.put(name1, entry.getValue());
                        }
                    }
                }
            });
        }
        return result;
    }


    /**
     * 获取注解的值
     *
     * @param annotation 注解
     * @return 注解值
     */
    public static Map<String, Object> getAnnotationValue(Annotation annotation) {
        if (null == annotation) {
            return Collections.emptyMap();
        }
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotation);

        FieldStation fieldStation = FieldStation.of(invocationHandler);
        return (Map<String, Object>) fieldStation.getValue(MEMBER_VALUES);
    }

    /**
     * Get the {@link AnnotationAttributes}
     *
     * @param annotatedElement     {@link AnnotatedElement the annotated element}
     * @param annotationType       the {@link Class tyoe} pf {@link Annotation annotation}
     * @param propertyResolver     {@link PropertyResolver} instance
     * @param ignoreDefaultValue   whether ignore default value or not
     * @param ignoreAttributeNames the attribute names of annotation should be ignored
     * @return if <code>annotatedElement</code> can't be found in <code>annotatedElement</code>, return <code>null</code>
     * @since 1.0.3
     */
    public static AnnotationAttributes getAnnotationAttributes(AnnotatedElement annotatedElement,
                                                               Class<? extends Annotation> annotationType,
                                                               PropertyResolver propertyResolver,
                                                               boolean ignoreDefaultValue,
                                                               String... ignoreAttributeNames) {
        Annotation annotation = annotatedElement.getAnnotation(annotationType);
        return annotation == null ? null : getAnnotationAttributes(annotation, propertyResolver, ignoreDefaultValue, ignoreAttributeNames);
    }

    /**
     * Get the {@link AnnotationAttributes}, if the argument <code>tryMergedAnnotation</code> is <code>true</code>,
     * the {@link AnnotationAttributes} will be got from
     * {@link #tryGetMergedAnnotationAttributes(AnnotatedElement, Class, PropertyResolver, boolean, String...) merged annotation} first,
     * if failed, and then to get from
     * {@link #getAnnotationAttributes(AnnotatedElement, Class, PropertyResolver, boolean, boolean, String...) normal one}
     *
     * @param annotatedElement     {@link AnnotatedElement the annotated element}
     * @param annotationType       the {@link Class tyoe} pf {@link Annotation annotation}
     * @param propertyResolver     {@link PropertyResolver} instance
     * @param ignoreDefaultValue   whether ignore default value or not
     * @param tryMergedAnnotation  whether try merged annotation or not
     * @param ignoreAttributeNames the attribute names of annotation should be ignored
     * @return if <code>annotatedElement</code> can't be found in <code>annotatedElement</code>, return <code>null</code>
     * @since 1.0.3
     */
    public static AnnotationAttributes getAnnotationAttributes(AnnotatedElement annotatedElement,
                                                               Class<? extends Annotation> annotationType,
                                                               PropertyResolver propertyResolver,
                                                               boolean ignoreDefaultValue,
                                                               boolean tryMergedAnnotation,
                                                               String... ignoreAttributeNames) {
        AnnotationAttributes attributes = null;

        if (tryMergedAnnotation) {
            attributes = tryGetMergedAnnotationAttributes(annotatedElement, annotationType, propertyResolver, ignoreDefaultValue, ignoreAttributeNames);
        }

        if (attributes == null) {
            attributes = getAnnotationAttributes(annotatedElement, annotationType, propertyResolver, ignoreDefaultValue, ignoreAttributeNames);
        }

        return attributes;
    }

    /**
     * Try to get the merged {@link Annotation annotation}
     *
     * @param annotatedElement {@link AnnotatedElement the annotated element}
     * @param annotationType   the {@link Class tyoe} pf {@link Annotation annotation}
     * @return If current version of Spring Framework is below 4.2, return <code>null</code>
     * @since 1.0.3
     */
    public static Annotation tryGetMergedAnnotation(AnnotatedElement annotatedElement,
                                                    Class<? extends Annotation> annotationType) {

        Annotation mergedAnnotation = null;

        ClassLoader classLoader = annotationType.getClassLoader();

        if (ClassUtils.isPresent(ANNOTATED_ELEMENT_UTILS_CLASS_NAME, classLoader)) {
            Class<?> annotatedElementUtilsClass = ClassUtils.resolveClassName(ANNOTATED_ELEMENT_UTILS_CLASS_NAME, classLoader);
            // getMergedAnnotation method appears in the Spring Framework 4.2
            Method getMergedAnnotationMethod = ClassUtils.findMethod(annotatedElementUtilsClass, "getMergedAnnotation", AnnotatedElement.class, Class.class);
            if (getMergedAnnotationMethod != null) {
                mergedAnnotation = (Annotation) ClassUtils.invokeMethod(getMergedAnnotationMethod, null, annotatedElement, annotationType);
            }
        }

        return mergedAnnotation;
    }

    /**
     * Try to get {@link AnnotationAttributes the annotation attributes} after merging and resolving the placeholders
     *
     * @param annotatedElement     {@link AnnotatedElement the annotated element}
     * @param annotationType       the {@link Class tyoe} pf {@link Annotation annotation}
     * @param propertyResolver     {@link PropertyResolver} instance
     * @param ignoreDefaultValue   whether ignore default value or not
     * @param ignoreAttributeNames the attribute names of annotation should be ignored
     * @return If the specified annotation type is not found, return <code>null</code>
     * @since 1.0.3
     */
    public static AnnotationAttributes tryGetMergedAnnotationAttributes(AnnotatedElement annotatedElement,
                                                                        Class<? extends Annotation> annotationType,
                                                                        PropertyResolver propertyResolver,
                                                                        boolean ignoreDefaultValue,
                                                                        String... ignoreAttributeNames) {
        Annotation annotation = tryGetMergedAnnotation(annotatedElement, annotationType);
        return annotation == null ? null : getAnnotationAttributes(annotation, propertyResolver, ignoreDefaultValue, ignoreAttributeNames);
    }

    /**
     * Convert the supplied array into a List. A primitive array gets converted
     * into a List of the appropriate wrapper type.
     * <p><b>NOTE:</b> Generally prefer the standard {@link Arrays#asList} method.
     * This {@code arrayToList} method is just meant to deal with an incoming Object
     * value that might be an {@code Object[]} or a primitive array at runtime.
     * <p>A {@code null} source value will be converted to an empty List.
     *
     * @param source the (potentially primitive) array
     * @return the converted List result
     * @see Arrays#asList(Object[])
     */
    public static List<?> arrayToList(Object source) {
        return Arrays.asList(ObjectUtils.toObjectArray(source));
    }


    /**
     * 获取所有注解
     *
     * @param type 类型
     * @return 注解
     */
    public static Set<Annotation> getAllAnnotations(Class<?> type) {
        Set<Annotation> rs = new LinkedHashSet<>();
        Annotation[] annotations = type.getDeclaredAnnotations();
        List<Annotation> clear = clear(annotations);
        rs.addAll(clear);
        doAnalysisDeepAnnotation(clear, rs);

        return rs;
    }

    /**
     * 深度扫描注解
     *
     * @param annotations 注解
     * @param rs          结果集
     */
    private static void doAnalysisDeepAnnotation(List<Annotation> annotations, Set<Annotation> rs) {
        for (Annotation annotation : annotations) {
            Annotation[] annotations1 = annotation.getClass().getDeclaredAnnotations();
            List<Annotation> realAnnotations = clear(annotations1);
            if (realAnnotations.isEmpty()) {
                continue;
            }
            rs.addAll(realAnnotations);
            doAnalysisDeepAnnotation(realAnnotations, rs);
        }
    }

    /**
     * 清除无效注解
     *
     * @param annotations 注解
     * @return 注解
     */
    private static List<Annotation> clear(Annotation[] annotations) {
        List<Annotation> list = new LinkedList<>();
        for (Annotation annotation : annotations) {
            if (annotation.getClass().getTypeName().startsWith(JAVA)) {
                continue;
            }

            list.add(annotation);
        }
        return list;
    }

    /**
     * 获取注解
     *
     * @param annotation 注解
     * @return Annotation
     */
    @SuppressWarnings("ALL")
    public static Class<? extends Annotation> getProxyRealAnnotation(Annotation annotation) {
        InvocationHandler invocationHandler = (InvocationHandler) FieldStation.of(annotation).getFieldValue("h");
        return (Class<? extends Annotation>) FieldStation.of(invocationHandler).getFieldValue("type");
    }

    /**
     * 具有注解
     *
     * @param type     类
     * @param annotationType 注释
     * @return boolean
     */
    public static <T extends Annotation> boolean hasAnnotation(Object type, Class<T> annotationType) {
        if(type instanceof Class<?>) {
            return ((Class<?>) type).isAnnotationPresent(annotationType);
        }

        if(type instanceof Field) {
            return ((Field) type).isAnnotationPresent(annotationType);
        }

        if(type instanceof Method) {
            return ((Method) type).isAnnotationPresent(annotationType);
        }


        return false;
    }
}
