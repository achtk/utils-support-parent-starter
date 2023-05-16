package com.chua.common.support.reflection.describe;

import com.chua.common.support.converter.Converter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

/**
 * 字段描述描述
 *
 * @author CH
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(fluent = true)
public class ParameterDescribe {
    /**
     * 索引
     */
    private int index;
    /**
     * 名称
     */
    private String name;

    /**
     * 类型
     */
    private String returnType;
    /**
     * 类型
     */
    private Class<?> returnClassType;
    /**
     * 注解类型
     */
    private AnnotationDescribe[] annotationTypes;
    /**
     * 作用范围
     */
    private int modifiers;
    /**
     * 方法
     */
    private Parameter parameter;


    /**
     * 初始化
     *
     * @param parameter 字段
     * @return 名称
     */
    public static ParameterDescribe of(Parameter parameter) {
        Annotation[] declaredAnnotations = parameter.getDeclaredAnnotations();
        AnnotationDescribe[] annotationDescribes = new AnnotationDescribe[declaredAnnotations.length];
        for (int i = 0; i < declaredAnnotations.length; i++) {
            Annotation declaredAnnotation = declaredAnnotations[i];
            annotationDescribes[i] = AnnotationDescribe.of(declaredAnnotation);
        }

        ParameterDescribe parameterDescribe = of(parameter.getModifiers(),
                parameter.getType().getTypeName(),
                parameter.getName(), annotationDescribes
        );
        parameterDescribe.returnClassType(parameter.getType());
        parameterDescribe.parameter(parameter);
        return parameterDescribe;
    }

    /**
     * 初始化
     *
     * @param modifiers           范围
     * @param name                字段名称
     * @param typeName            参数类型
     * @param annotationDescribes 注解
     * @return 名称
     */
    private static ParameterDescribe of(int modifiers, String typeName, String name, AnnotationDescribe[] annotationDescribes) {
        return new ParameterDescribe(0, name, typeName, null, annotationDescribes, modifiers, null);
    }


    /**
     * 是否匹配
     *
     * @param name 名称
     * @return 是否匹配
     */
    public boolean isMatch(String name) {
        return this.name.equals(name);
    }

    /**
     * 是否是已有的方法
     *
     * @return 是否是已有的方法
     */
    public boolean getParameter() {
        return null != parameter;
    }


    /**
     * 是否存在实体
     *
     * @return 是否存在实体
     */
    public boolean hasMember() {
        return null != parameter;
    }

    /**
     * 获取注解
     *
     * @param annotationType 注解类型
     * @param <A>            类型
     * @return 注解
     */
    public <A> List<AnnotationParameterDescribe> annotation(Class<A> annotationType) {
        for (AnnotationDescribe describe : annotationTypes) {
            if (describe.getName().equals(annotationType.getTypeName())) {
                return describe.getAnnotationParameterDescribes();
            }
        }
        return null;
    }

    /**
     * 获取注解
     *
     * @param annotationType 注解类型
     * @return 注解
     */
    public <A> A getAnnotationValue(Class<A> annotationType) {
        for (AnnotationDescribe describe : annotationTypes) {
            if (describe.getName().equals(annotationType.getTypeName())) {
                return (A) describe.getAnnotation();
            }
        }
        return null;
    }

    /**
     * 是否存在注解
     *
     * @param annotationType 注解
     * @return 是否存在注解
     */
    public boolean hasAnnotation(Class annotationType) {
        for (AnnotationDescribe type : annotationTypes) {
            if (type.getName().equals(annotationType.getTypeName())) {
                return true;
            }
        }

        return false;
    }

    /**
     * 构建值
     *
     * @param arg 参数
     * @return 结果
     */
    public Object createValue(Object arg) {
        return Converter.convertIfNecessary(arg, returnClassType);
    }

    /**
     * 获取值
     * @param typeMapping 值表
     * @return 值
     */
    public Object getValue(Map<Class<?>, Object> typeMapping) {
        Class<?> aClass = returnClassType();
        Object o = typeMapping.get(aClass);
        if(null != o) {
            return o;
        }
        return null;
    }
}
