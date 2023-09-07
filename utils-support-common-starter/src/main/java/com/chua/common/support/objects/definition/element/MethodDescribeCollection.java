package com.chua.common.support.objects.definition.element;

import com.chua.common.support.utils.CollectionUtils;
import com.chua.common.support.utils.ObjectUtils;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 方法描述集合
 *
 * @author CH
 */
public class MethodDescribeCollection extends MethodDescribe implements ElementDescribe {
    private final List<MethodDescribe> methodDescribes;
    private final String methodName;

    public MethodDescribeCollection(List<MethodDescribe> methodDescribes, String methodName) {
        super(ObjectUtils.withNull(CollectionUtils.findFirst(methodDescribes), MethodDescribe::method));
        this.methodDescribes = methodDescribes;
        this.methodName = methodName;
    }

    @Override
    public String name() {
        return methodName;
    }

    @Override
    public Class<?> getType() {
        return null;
    }

    @Override
    public Map<String, ParameterDescribe> parameters() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, AnnotationDescribe> annotations() {
        return Collections.emptyMap();
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
        for (MethodDescribe methodDescribe : methodDescribes) {
            if(methodDescribe.hasAnnotation(annotationType)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Annotation getAnnotation(String annotationType) {
        for (MethodDescribe methodDescribe : methodDescribes) {
            if(methodDescribe.hasAnnotation(annotationType)) {
                return methodDescribe.getAnnotation(annotationType);
            }
        }
        return null;
    }

    @Override
    public AnnotationDescribe getAnnotationDescribe(String annotationType) {
        for (MethodDescribe methodDescribe : methodDescribes) {
            if(methodDescribe.hasAnnotation(annotationType)) {
                return methodDescribe.getAnnotationDescribe(annotationType);
            }
        }
        return null;
    }
}
