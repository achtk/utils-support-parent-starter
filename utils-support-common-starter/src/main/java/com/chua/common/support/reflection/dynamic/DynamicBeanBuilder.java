package com.chua.common.support.reflection.dynamic;


import com.chua.common.support.reflection.dynamic.attribute.AnnotationAttribute;
import com.chua.common.support.reflection.dynamic.attribute.ConstructAttribute;
import com.chua.common.support.reflection.dynamic.attribute.FieldAttribute;
import com.chua.common.support.reflection.dynamic.attribute.MethodAttribute;

import java.lang.reflect.Method;

/**
 * 构造器
 *
 * @author ch
 */
public interface DynamicBeanBuilder<T> {

    /**
     * 渲染类
     *
     * @return this
     */
    DynamicBeanBuilder<T> isClass();

    /**
     * 名称
     *
     * @param name 名称
     * @return this
     */
    DynamicBeanBuilder<T> name(String name);

    /**
     * source
     *
     * @param source 名称
     * @return this
     */
    DynamicBeanBuilder<T> source(String source);

    /**
     * 注解名称
     *
     * @param annotations 名称
     * @return this
     */
    DynamicBeanBuilder<T> annotations(AnnotationAttribute... annotations);

    /**
     * 注解名称
     *
     * @param name 名称
     * @return this
     */
    DynamicBeanBuilder<T> packages(String... name);

    /**
     * 注解名称
     *
     * @param name 名称
     * @return this
     */
    default DynamicBeanBuilder<T> packages(Class<?> name) {
        return packages(name.getPackage().getName());
    }

    /**
     * 接口名称
     *
     * @param name 名称
     * @return this
     */
    DynamicBeanBuilder<T> setInterfaces(String... name);

    /**
     * 接口名称
     *
     * @param name 名称
     * @return this
     */
    DynamicBeanBuilder<T> interfaces(String... name);


    /**
     * 超类
     *
     * @param name 名称
     * @return this
     */
    DynamicBeanBuilder<T> superType(String name);

    /**
     * 字段名称
     *
     * @param fieldAttribute 字段
     * @return this
     */
    DynamicBeanBuilder<T> field(FieldAttribute fieldAttribute);

    /**
     * 字段名称
     *
     * @param name 名称
     * @param type 类型
     * @return this
     */
    default DynamicBeanBuilder<T> field(String name, Class<?> type) {
        return field(FieldAttribute.builder().name(name).type(type.getTypeName()).build());
    }

    ;

    /**
     * 字段名称
     *
     * @param name 名称
     * @param type 类型
     * @return this
     */
    default DynamicBeanBuilder<T> field(String name, String type) {
        return field(FieldAttribute.builder().name(name).type(type).build());
    }

    /**
     * 字段名称
     *
     * @param constructAttribute 构造
     * @return this
     */
    DynamicBeanBuilder<T> constructor(ConstructAttribute constructAttribute);

    /**
     * 字段名称
     *
     * @param argTypes 参数类型
     * @return this
     */
    default DynamicBeanBuilder<T> constructor(String[] argTypes) {
        return constructor(ConstructAttribute.builder().argTypes(argTypes).build());
    }

    /**
     * 字段名称
     *
     * @return this
     */
    default DynamicBeanBuilder<T> defaultConstructor() {
        return constructor(ConstructAttribute.builder().build());
    }

    /**
     * 方法
     *
     * @param methodAttribute 方法
     * @return this
     */
    DynamicBeanBuilder<T> method(MethodAttribute methodAttribute);

    /**
     * 构建对象
     *
     * @return 对象
     */
    DynamicBean build();

    /**
     * 获取方法
     *
     * @param source 待获取方法的类
     * @param method 获取的方法参数
     * @return 方法
     */
    default Method getMethod(Class<?> source, Method method) {
        try {
            return source.getDeclaredMethod(method.getName(), method.getParameterTypes());
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * 返回值
     *
     * @param declaredMethod 方法
     * @return 返回值
     */
    default String createBody(Method declaredMethod) {
        Class<?> returnType = declaredMethod.getReturnType();
        if (returnType.isPrimitive()) {
            if (boolean.class.isAssignableFrom(returnType)) {
                return "{return false;}";
            }

            if (int.class.isAssignableFrom(returnType)) {
                return "{return 0;}";
            }

            if (float.class.isAssignableFrom(returnType)) {
                return "{return 0f;}";
            }

            if (double.class.isAssignableFrom(returnType)) {
                return "{return 0d;}";
            }

            if (long.class.isAssignableFrom(returnType)) {
                return "{return 0L;}";
            }

            if (short.class.isAssignableFrom(returnType)) {
                return "{return (short)0;}";
            }

            if (char.class.isAssignableFrom(returnType)) {
                return "{return (char)0;}";
            }

            if (byte.class.isAssignableFrom(returnType)) {
                return "{return (byte)0;}";
            }
        }

        if (void.class.isAssignableFrom(returnType) || Void.class.isAssignableFrom(returnType)) {
            return "{return;}";
        }

        return "{return null;}";
    }

    /**
     * 返回值
     *
     * @param declaredMethod 方法
     * @param name           实体名称
     * @return 返回值
     */
    default String createBody(Method declaredMethod, String name) {
        Class<?> returnType = declaredMethod.getReturnType();
        if (void.class.isAssignableFrom(returnType) || Void.class.isAssignableFrom(returnType)) {
            return "{" + name + "." + declaredMethod.getName() + "($$);}";
        }

        return "{return " + name + "." + declaredMethod.getName() + "($$);}";
    }


}
