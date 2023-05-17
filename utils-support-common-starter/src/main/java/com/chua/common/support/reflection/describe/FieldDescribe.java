package com.chua.common.support.reflection.describe;

import com.chua.common.support.reflection.describe.provider.FieldDescribeProvider;
import com.chua.common.support.reflection.describe.provider.MethodDescribeProvider;
import com.chua.common.support.utils.ArrayUtils;
import com.chua.common.support.value.NullValue;
import com.chua.common.support.value.Value;
import lombok.Data;
import lombok.experimental.Accessors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 字段描述
 *
 * @author CH
 */
@Data
@Accessors(fluent = true)
public class FieldDescribe implements MemberDescribe {
    /**
     * 名称
     */
    private String name;
    /**
     * 是否创建的
     */
    private boolean isCreate;
    /**
     * 默认值
     */
    private Object defaultValue;

    /**
     * 类型
     */
    private String returnType;
    /**
     * 字段
     */
    private Field field;
    /**
     * 注解
     */
    private AnnotationDescribe[] annotationTypes;

    /**
     * 所属实体
     */
    private Object entity;

    /**
     * 初始化
     *
     * @param name 字段名称
     * @param type 字段类型
     * @return 名称
     */
    public static FieldDescribe of(String name, Class<?> type) {
        return of(type.getTypeName(), name, null, new AnnotationDescribe[0]);
    }

    /**
     * 初始化
     *
     * @param name         字段名称
     * @param type         字段类型
     * @param defaultValue 默认
     * @return 名称
     */
    public static FieldDescribe of(String name, Class<?> type, Object defaultValue) {
        return of(type.getTypeName(), name, defaultValue, new AnnotationDescribe[0]);
    }

    /**
     * 初始化
     *
     * @param name            字段名称
     * @param type            字段类型
     * @param defaultValue    默认
     * @param annotationTypes 注解
     * @return 名称
     */
    public static FieldDescribe of(String name, Class<?> type, Object defaultValue, AnnotationDescribe[] annotationTypes) {
        return of(type.getTypeName(), name, defaultValue, annotationTypes);
    }

    /**
     * 初始化
     *
     * @param name            字段名称
     * @param type            字段类型
     * @param annotationTypes 注解
     * @return 名称
     */
    public static FieldDescribe of(String name, Class<?> type, AnnotationDescribe[] annotationTypes) {
        return of(type.getTypeName(), name, null, annotationTypes);
    }

    /**
     * 初始化
     *
     * @param name            字段名称
     * @param type            字段类型
     * @param defaultValue    默认
     * @param annotationTypes 注解
     * @return 名称
     */
    public static FieldDescribe of(String type, String name, Object defaultValue, AnnotationDescribe[] annotationTypes) {
        return FieldDescribe.builder()
                .name(name)
                .defaultValue(defaultValue)
                .annotationTypes(annotationTypes)
                .returnType(type).build();
    }

    /**
     * 初始化
     *
     * @param field 字段
     * @return 名称
     */
    public static FieldDescribe of(Field field) {
        field.setAccessible(true);
        FieldDescribe fieldDescribe = of(field.getName(), field.getType());
        fieldDescribe.field(field);
        Annotation[] declaredAnnotations = field.getDeclaredAnnotations();
        AnnotationDescribe[] annotationDescribes = new AnnotationDescribe[declaredAnnotations.length];
        for (int i = 0; i < declaredAnnotations.length; i++) {
            Annotation declaredAnnotation = declaredAnnotations[i];
            annotationDescribes[i] = AnnotationDescribe.of(declaredAnnotation);
        }
        fieldDescribe.annotationTypes(annotationDescribes);
        return fieldDescribe;
    }

    /**
     * 执行
     *
     * @param entity 实体
     * @return 结果
     */
    public Value<Object> get(Object entity) {
        if (null == field) {
            return Value.of(null);
        }

        try {
            return Value.of(field.get(entity));
        } catch (Exception e) {
            return Value.of(null, e);
        }
    }

    /**
     * 执行
     *
     * @param entity 实体
     * @param args   参数
     */
    public Value<Object> set(Object entity, Object args) {
        try {
            field.set(entity, args);
            return NullValue.INSTANCE;
        } catch (IllegalAccessException e) {
            return Value.of(e);
        }
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
     * 是否存在实体
     *
     * @return 是否存在实体
     */
    public boolean hasMember() {
        return null != field;
    }


    /**
     * 构造器
     *
     * @return FieldDescribeBuilder
     */
    public static FieldDescribeBuilder builder() {
        return new FieldDescribeBuilder();
    }

    @Override
    public FieldDescribe getFieldDescribe(String name) {
        return this.name.equals(name) ? this : null;
    }

    @Override
    public FieldDescribeProvider getFieldDescribeProvider(String name) {
        return this.name.equals(name) ? new FieldDescribeProvider().addChain(this) : null;
    }

    @Override
    public MethodDescribe getMethodDescribe(String name, String[] parameterTypes) {
        return null;
    }

    @Override
    public MethodDescribeProvider getMethodDescribe(String name) {
        return MethodDescribeProvider.empty();
    }

    @Override
    public MethodDescribeProvider getMethodDescribeByAnnotation(String name) {
        return MethodDescribeProvider.empty();
    }

    @Override
    public List<FieldDescribe> getFieldDescribeByAnnotation(String name) {
        return Arrays.stream(annotationTypes).anyMatch(it -> it.getName().equals(name)) ? Collections.singletonList(this) : Collections.emptyList();
    }

    @Override
    public boolean hasAnyAnnotation(String[] name) {
        for (String s : name) {
            if (hasAnnotation(s)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasAnnotation(String name) {
        return Arrays.stream(annotationTypes).anyMatch(it -> it.getName().equals(name));
    }

    @Override
    public boolean hasMethodByParameterType(Class<?>[] type) {
        return false;
    }

    @Override
    public void addAnnotation(Annotation annotation) {
        if(null == this.annotationTypes) {
            annotationTypes = new AnnotationDescribe[0];
        }
        ArrayUtils.addElement(this.annotationTypes, AnnotationDescribe.of(annotation));
    }

    /**
     * 获取注解描述
     *
     * @param annotationType 注解类型
     * @return 注解
     */
    public <T> T getAnnotationType(Class<T> annotationType) {
        for (AnnotationDescribe type : annotationTypes) {
            if (annotationType.isAssignableFrom(type.getAnnotation().annotationType())) {
                return (T) type.getAnnotation();
            }
        }

        return null;
    }

    /**
     * 获取注解描述
     *
     * @param annotationType 注解类型
     * @return 注解
     */
    public <T> AnnotationDescribe getAnnotation(Class<T> annotationType) {
        for (AnnotationDescribe type : annotationTypes) {
            if (annotationType.isAssignableFrom(type.getAnnotation().annotationType())) {
                return type;
            }
        }

        return null;
    }

    /**
     * 构造器
     */
    public static class FieldDescribeBuilder {

        private final FieldDescribe fieldDescribe = new FieldDescribe();
        private final List<AnnotationDescribe> annotationDescribes = new LinkedList<>();

        /**
         * 名称
         *
         * @param name 名称
         * @return this
         */
        public FieldDescribeBuilder name(String name) {
            fieldDescribe.name(name);
            return this;
        }

        /**
         * 字段
         *
         * @param field 字段
         * @return this
         */
        public FieldDescribeBuilder field(Field field) {
            fieldDescribe.field(field);
            return this;
        }

        /**
         * 默认值
         *
         * @param defaultValue 默认值
         * @return this
         */
        public FieldDescribeBuilder defaultValue(Object defaultValue) {
            fieldDescribe.defaultValue(defaultValue);
            return this;
        }

        /**
         * 返回值
         *
         * @param returnType 返回值
         * @return this
         */
        public FieldDescribeBuilder returnType(String returnType) {
            fieldDescribe.returnType(returnType);
            return this;
        }

        /**
         * 返回值
         *
         * @param returnType 返回值
         * @return this
         */
        public FieldDescribeBuilder returnType(Class<?> returnType) {
            fieldDescribe.returnType(returnType.getTypeName());
            return this;
        }

        /**
         * 注解
         *
         * @param annotationDescribes 注解
         * @return this
         */
        public FieldDescribeBuilder annotationTypes(AnnotationDescribe... annotationDescribes) {
            this.annotationDescribes.clear();
            this.annotationDescribes.addAll(Arrays.asList(annotationDescribes));
            return this;
        }

        /**
         * 注解
         *
         * @param annotationDescribes 注解
         * @return this
         */
        public FieldDescribeBuilder withAnnotationType(AnnotationDescribe... annotationDescribes) {
            this.annotationDescribes.addAll(Arrays.asList(annotationDescribes));
            return this;
        }

        /**
         * 注解
         *
         * @param annotationTypes 注解
         * @return this
         */
        public FieldDescribeBuilder withAnnotationType(Class<?>... annotationTypes) {
            this.annotationDescribes.addAll(Arrays.stream(annotationTypes).map(it -> {
                AnnotationDescribe annotationDescribe = new AnnotationDescribe();
                annotationDescribe.setName(it.getTypeName());
                return annotationDescribe;
            }).collect(Collectors.toList()));
            return this;
        }

        /**
         * 构造
         *
         * @return 构造
         */
        public FieldDescribe build() {
            fieldDescribe.annotationTypes(annotationDescribes.toArray(new AnnotationDescribe[0]));
            return fieldDescribe;
        }

    }
}
