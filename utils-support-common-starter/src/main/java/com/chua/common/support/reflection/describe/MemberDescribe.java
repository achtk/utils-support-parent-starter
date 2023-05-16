package com.chua.common.support.reflection.describe;

import com.chua.common.support.reflection.describe.provider.MethodDescribeProvider;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * 描述
 *
 * @author CH
 */
public interface MemberDescribe {
    /**
     * 获取属性
     *
     * @param name 属性名称
     * @return 属性
     */
    FieldDescribe getFieldDescribe(String name);

    /**
     * 获取方法
     *
     * @param name           方法名
     * @param parameterTypes 字段类型
     * @return 获取方法
     */
    MethodDescribe getMethodDescribe(String name, String[] parameterTypes);

    /**
     * 获取方法
     *
     * @param name 方法名
     * @return 获取方法
     */
    MethodDescribeProvider getMethodDescribe(String name);

    /**
     * 包含注解的方法描述
     *
     * @param name 注解名称
     * @return 包含注解的方法描述
     */
    MethodDescribeProvider getMethodDescribeByAnnotation(String name);

    /**
     * 包含注解的方法描述
     *
     * @param aClass 注解名称
     * @return 包含注解的方法描述
     */
    default MethodDescribeProvider getMethodDescribeByAnnotation(Class<? extends Annotation> aClass) {
        return getMethodDescribeByAnnotation(aClass.getTypeName());
    }

    /**
     * 包含注解的方法描述
     *
     * @param name 注解名称
     * @return 包含注解的方法描述
     */
    List<FieldDescribe> getFieldDescribeByAnnotation(String name);

    /**
     * 包含注解的方法描述
     *
     * @param aClass 注解名称
     * @return 包含注解的方法描述
     */
    default List<FieldDescribe> getFieldDescribeByAnnotation(Class<? extends Annotation> aClass) {
        return getFieldDescribeByAnnotation(aClass.getTypeName());
    }

    /**
     * 是否包含注解
     *
     * @param name 注解名称
     * @return 是否包含注解
     */
    boolean hasAnyAnnotation(String[] name);

    /**
     * 是否包含注解
     *
     * @param name 注解名称
     * @return 是否包含注解
     */
    boolean hasAnnotation(String name);

    /**
     * 是否包含注解
     *
     * @param aClass 注解名称
     * @return 是否包含注解
     */
    default boolean hasAnnotation(Class<? extends Annotation> aClass) {
        return hasAnnotation(aClass.getTypeName());
    }
    /**
     * 方法包含参数
     *
     * @param type 参数
     * @return 方法包含注解
     */
    boolean hasMethodByParameterType(Class<?>[] type);

    /**
     * 添加注解
     * @param annotation 注解
     */
    void addAnnotation(Annotation annotation);
}
