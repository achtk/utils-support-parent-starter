package com.chua.common.support.reflection.describe;

import com.chua.common.support.utils.ArrayUtils;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.value.Value;
import lombok.Data;
import lombok.experimental.Accessors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.chua.common.support.constant.CommonConstant.EMPTY_ARRAY;

/**
 * 构造描述
 *
 * @author CH
 */
@Data
@Accessors(fluent = true)
public class ConstructDescribe {
    /**
     * 名称
     */
    private String name;

    /**
     * 参数类型
     */
    private String[] parameterTypes;
    /**
     * 异常类型
     */
    private String[] exceptionTypes;
    /**
     * 注解类型
     */
    private AnnotationDescribe[] annotationTypes;
    /**
     * 是否被修改
     */
    private boolean modify;
    /**
     * 作用范围
     */
    private int modifiers;
    /**
     * 方法
     */
    private Constructor<?> constructor;
    /**
     * 参数
     */
    private ParameterDescribe[] parameterDescribes;
    /**
     * 方法体
     */
    private String body;

    public void setBody(String body) {
        modify = true;
        this.body = body;
    }

    /**
     * 初始化
     *
     * @param name            字段名称
     * @param parameterTypes  参数类型
     * @param exceptions      异常类型
     * @param body            内容
     * @param annotationTypes 注解
     * @return 名称
     */
    public static ConstructDescribe of(
            String name,
            Class<?>[] parameterTypes,
            Class<?>[] exceptions,
            String body,
            AnnotationDescribe[] annotationTypes) {
        return of(Modifier.PUBLIC, name, parameterTypes, exceptions, body, annotationTypes);
    }

    /**
     * 初始化
     *
     * @param modifiers       作用范围
     * @param name            字段名称
     * @param parameterTypes  参数类型
     * @param exceptions      异常类型
     * @param body            内容
     * @param annotationTypes 注解
     * @return 名称
     */
    public static ConstructDescribe of(int modifiers,
                                       String name,
                                       Class<?>[] parameterTypes,
                                       Class<?>[] exceptions,
                                       String body,
                                       AnnotationDescribe[] annotationTypes) {

        return of(modifiers,
                name,
                ClassUtils.toTypeName(parameterTypes),
                ClassUtils.toTypeName(exceptions),
                body,
                annotationTypes);
    }

    /**
     * 初始化
     *
     * @param name            字段名称
     * @param parameterTypes  参数类型
     * @param exceptions      异常类型
     * @param body            内容
     * @param annotationTypes 注解
     * @return 名称
     */
    public static ConstructDescribe of(
            String name,
            String[] parameterTypes,
            String[] exceptions,
            String body,
            AnnotationDescribe[] annotationTypes) {
        return of(Modifier.PUBLIC, name, parameterTypes, exceptions, body, annotationTypes);
    }

    /**
     * 初始化
     *
     * @param name            字段名称
     * @param parameterTypes  参数类型
     * @param exceptions      异常类型
     * @param body            内容
     * @param annotationTypes 注解
     * @return 名称
     */
    public static ConstructDescribe of(
            int modifiers,
            String name,
            String[] parameterTypes,
            String[] exceptions,
            String body,
            AnnotationDescribe[] annotationTypes) {
        return ConstructDescribe.builder()
                .modifiers(modifiers)
                .name(name)
                .parameterTypes(parameterTypes)
                .exceptionTypes(exceptions)
                .body(body)
                .annotationTypes(annotationTypes).build();
    }

    /**
     * 初始化
     *
     * @param constructor 字段
     * @return 名称
     */
    public static ConstructDescribe of(Constructor constructor) {
        Annotation[] declaredAnnotations = constructor.getDeclaredAnnotations();
        AnnotationDescribe[] annotationDescribes = new AnnotationDescribe[declaredAnnotations.length];
        for (int i = 0; i < declaredAnnotations.length; i++) {
            Annotation declaredAnnotation = declaredAnnotations[i];
            annotationDescribes[i] = AnnotationDescribe.of(declaredAnnotation);
        }

        ConstructDescribe constructDescribe = of(constructor.getModifiers(),
                constructor.getName(),
                ClassUtils.toTypeName(constructor.getParameterTypes()),
                ClassUtils.toTypeName(constructor.getExceptionTypes()),
                null, annotationDescribes
        );
        Parameter[] parameters = constructor.getParameters();
        ParameterDescribe[] rs = new ParameterDescribe[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            rs[i] = ParameterDescribe.of(parameter).index(i);
        }

        constructDescribe.constructor(constructor);
        return constructDescribe;
    }

    /**
     * 是否匹配
     *
     * @param name           名称
     * @param parameterTypes 类型
     * @return 是否匹配
     */
    public boolean isMatch(String name, String[] parameterTypes) {
        return this.name.equals(name) && ArrayUtils.isEquals(this.parameterTypes, parameterTypes);
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
    public boolean getConstructor() {
        return null != constructor;
    }

    /**
     * 执行方法
     *
     * @param args 参数
     */
    public Value<Object> invoke(Object... args) {
        if (constructor != null) {
            try {
                return Value.of(constructor.newInstance(args));
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    /**
     * 是否存在实体
     *
     * @return 是否存在实体
     */
    public boolean hasMember() {
        return null != constructor;
    }

    /**
     * 构造器
     *
     * @return methodDescribeBuilder
     */
    public static ConstructDescribeBuilder builder() {
        return new ConstructDescribeBuilder();
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
     * 唯一索引
     *
     * @return 唯一索引
     */
    public String uniqueKey() {
        return name + Arrays.hashCode(parameterTypes);
    }

    /**
     * 构造器
     */
    public static class ConstructDescribeBuilder {

        private final ConstructDescribe constructDescribe = new ConstructDescribe();
        private final List<String> parameterTypes = new LinkedList<>();
        private final List<String> exceptionTypes = new LinkedList<>();
        private final List<AnnotationDescribe> annotationDescribes = new LinkedList<>();
        private String body;
        private Constructor<?> constructor1;

        /**
         * 范围
         *
         * @param modifiers 范围
         * @return this
         */
        public ConstructDescribeBuilder modifiers(int modifiers) {
            constructDescribe.modifiers(modifiers);
            return this;
        }

        /**
         * 名称
         *
         * @param name 名称
         * @return this
         */
        public ConstructDescribeBuilder name(String name) {
            constructDescribe.name(name);
            return this;
        }

        /**
         * 参数类型
         *
         * @param parameterTypes 参数类型
         * @return this
         */
        public ConstructDescribeBuilder parameterTypes(String... parameterTypes) {
            if (ArrayUtils.isEmpty(parameterTypes)) {
                return this;
            }
            this.parameterTypes.clear();
            this.parameterTypes.addAll(Arrays.asList(parameterTypes));
            return this;
        }

        /**
         * 参数类型
         *
         * @param parameterTypes 参数类型
         * @return this
         */
        public ConstructDescribeBuilder parameterTypes(Class<?>... parameterTypes) {
            if (ArrayUtils.isEmpty(parameterTypes)) {
                return this;
            }
            this.parameterTypes.clear();
            this.parameterTypes.addAll(Arrays.asList(ClassUtils.toTypeName(parameterTypes)));
            return this;
        }

        /**
         * 参数类型
         *
         * @param parameterTypes 参数类型
         * @return this
         */
        public ConstructDescribeBuilder withParameterTypes(String... parameterTypes) {
            if (ArrayUtils.isEmpty(parameterTypes)) {
                return this;
            }
            this.parameterTypes.addAll(Arrays.asList(parameterTypes));
            return this;
        }

        /**
         * 参数类型
         *
         * @param parameterTypes 参数类型
         * @return this
         */
        public ConstructDescribeBuilder withParameterTypes(Class<?>... parameterTypes) {
            if (ArrayUtils.isEmpty(parameterTypes)) {
                return this;
            }
            this.parameterTypes.addAll(Arrays.asList(ClassUtils.toTypeName(parameterTypes)));
            return this;
        }

        /**
         * 异常
         *
         * @param exceptionTypes 异常
         * @return this
         */
        public ConstructDescribeBuilder exceptionTypes(String... exceptionTypes) {
            if (ArrayUtils.isEmpty(exceptionTypes)) {
                return this;
            }
            this.exceptionTypes.clear();
            this.exceptionTypes.addAll(Arrays.asList(exceptionTypes));
            return this;
        }

        /**
         * 异常
         *
         * @param exceptionTypes 异常
         * @return this
         */
        public ConstructDescribeBuilder exceptionTypes(Class<?>... exceptionTypes) {
            if (ArrayUtils.isEmpty(exceptionTypes)) {
                return this;
            }

            this.exceptionTypes.clear();
            this.exceptionTypes.addAll(Arrays.asList(ClassUtils.toTypeName(exceptionTypes)));
            return this;
        }

        /**
         * 异常
         *
         * @param exceptionTypes 异常
         * @return this
         */
        public ConstructDescribeBuilder withExceptionTypes(String... exceptionTypes) {
            if (ArrayUtils.isEmpty(exceptionTypes)) {
                return this;
            }
            this.exceptionTypes.addAll(Arrays.asList(exceptionTypes));
            return this;
        }

        /**
         * 异常
         *
         * @param exceptionTypes 异常
         * @return this
         */
        public ConstructDescribeBuilder withExceptionTypes(Class<?>... exceptionTypes) {
            if (ArrayUtils.isEmpty(exceptionTypes)) {
                return this;
            }
            this.exceptionTypes.addAll(Arrays.asList(ClassUtils.toTypeName(exceptionTypes)));
            return this;
        }

        /**
         * 注解
         *
         * @param annotationDescribes 注解
         * @return this
         */
        public ConstructDescribeBuilder annotationTypes(AnnotationDescribe... annotationDescribes) {
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
        public ConstructDescribeBuilder withAnnotationType(AnnotationDescribe... annotationDescribes) {
            this.annotationDescribes.addAll(Arrays.asList(annotationDescribes));
            return this;
        }

        /**
         * body
         *
         * @param body body
         * @return this
         */
        public ConstructDescribeBuilder body(String body) {
            this.constructDescribe.body(body);
            return this;
        }

        /**
         * body
         *
         * @param bodyDescribe body
         * @return this
         */
        public ConstructDescribeBuilder body(BodyDescribe bodyDescribe) {
            this.constructDescribe.body(bodyDescribe.toString());
            return this;
        }

        /**
         * method
         *
         * @param constructor Constructor
         * @return this
         */
        public ConstructDescribeBuilder method(Constructor<?> constructor) {
            ConstructDescribe methodDescribe = of(constructor);
            name(methodDescribe.name());
            withAnnotationType(methodDescribe.annotationTypes());
            method(constructor);
            withParameterTypes(methodDescribe.parameterTypes());
            withExceptionTypes(methodDescribe.exceptionTypes());
            return this;
        }

        /**
         * 注解
         *
         * @param annotationTypes 注解
         * @return this
         */
        public ConstructDescribeBuilder withAnnotationType(Class<?>... annotationTypes) {
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
        public ConstructDescribe build() {
            constructDescribe
                    .parameterTypes(parameterTypes.toArray(EMPTY_ARRAY))
                    .exceptionTypes(exceptionTypes.toArray(EMPTY_ARRAY))
                    .annotationTypes(annotationDescribes.toArray(new AnnotationDescribe[0]));
            return constructDescribe;
        }

    }

    /**
     * void
     */
    public static final class VoidConstructDescribe extends ConstructDescribe {

    }
}
