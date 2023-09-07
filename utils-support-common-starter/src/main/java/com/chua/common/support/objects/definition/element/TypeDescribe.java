package com.chua.common.support.objects.definition.element;

import com.chua.common.support.objects.definition.resolver.AnnotationResolver;
import com.chua.common.support.objects.definition.resolver.MethodResolver;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.ArrayUtils;
import com.chua.common.support.utils.CollectionUtils;
import com.chua.common.support.utils.ObjectUtils;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 描述
 *
 * @author CH
 */
public class TypeDescribe implements ElementDescribe {
    private final Map<String, AnnotationDescribe> annotationDefinitions;
    protected Map<String, List<MethodDescribe>>  methodDefinitions;

    public static TypeDescribe create(Object data) {
        return new ObjectDescribe(data);
    }

    protected final Class<?> type;

    public TypeDescribe(Class<?> type) {
        this.type = type;
        this.annotationDefinitions = ServiceProvider.of(AnnotationResolver.class).getSpiService().get(type);

    }

    @Override
    public String name() {
        return type.getTypeName();
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public Map<String, ParameterDescribe> parameters() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, AnnotationDescribe> annotations() {
        return annotationDefinitions;
    }

    @Override
    public String returnType() {
        return null;
    }

    @Override
    public List<String> exceptionType() {
        return Collections.emptyList();
    }

    @Override
    public Map<String, Object> value() {
        return Collections.emptyMap();
    }

    @Override
    public void addBeanName(String name) {

    }

    @Override
    public boolean hasAnnotation(String annotationType) {
        return annotationDefinitions.containsKey(annotationType);
    }

    @Override
    public Annotation getAnnotation(String annotationType) {
        return ObjectUtils.withNull(annotationDefinitions.get(annotationType), AnnotationDescribe::getAnnotation);
    }

    @Override
    public AnnotationDescribe getAnnotationDescribe(String annotationType) {
        return annotationDefinitions.get(annotationType);
    }

    /**
     * get方法描述
     *
     * @param methodName 方法名称
     * @return {@link MethodDescribe}
     */
    public MethodDescribe getMethodDescribe(String methodName) {
        if(null == methodDefinitions) {
            loadMethodDefinitions();
        }
        return new MethodDescribeCollection(methodDefinitions.get(methodName), methodName);
    }

    /**
     * get方法描述
     *
     * @param methodName 方法名称
     * @param type       类型
     * @return {@link MethodDescribe}
     */
    public MethodDescribe getMethodDescribe(String methodName, Class<?>[] type) {
        if(null == methodDefinitions) {
            loadMethodDefinitions();
        }
        List<MethodDescribe> methodDescribes = methodDefinitions.get(methodName);
        if(null == methodDescribes) {
            return new MethodDescribeCollection(null, methodName);
        }
        List<MethodDescribe> collect = methodDescribes.stream().filter(it -> ArrayUtils.isEquals(it.method().getParameterTypes(), type)).collect(Collectors.toList());
        return CollectionUtils.isEmpty(collect) || collect.size() > 1 ? new MethodDescribeCollection(collect, methodName) : CollectionUtils.findFirst(collect);
    }

    private void loadMethodDefinitions() {
        methodDefinitions = ServiceProvider.of(MethodResolver.class).getSpiService().get(type);
    }
}
