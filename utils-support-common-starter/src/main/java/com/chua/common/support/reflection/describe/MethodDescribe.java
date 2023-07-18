package com.chua.common.support.reflection.describe;

import com.chua.common.support.converter.Converter;
import com.chua.common.support.json.Json;
import com.chua.common.support.reflection.describe.factory.MethodAnnotationFactory;
import com.chua.common.support.reflection.describe.provider.FieldDescribeProvider;
import com.chua.common.support.reflection.describe.provider.MethodDescribeProvider;
import com.chua.common.support.unit.name.NamingCase;
import com.chua.common.support.utils.ArrayUtils;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.CollectionUtils;
import com.chua.common.support.utils.MapUtils;
import com.chua.common.support.value.NullValue;
import com.chua.common.support.value.Value;
import lombok.Data;
import lombok.experimental.Accessors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

import static com.chua.common.support.constant.CommonConstant.*;
import static com.chua.common.support.spi.autowire.AutoServiceAutowire.UTILS;


/**
 * 方法描述
 *
 * @author CH
 */
@Data
@Accessors(fluent = true)
public class MethodDescribe implements MemberDescribe<MethodDescribe> {
    public static final MethodDescribe INSTANCE = new MethodDescribe();
    /**
     * 名称
     */
    private String name;
    /**
     * 是否创建的
     */
    private boolean isCreate;
    /**
     * 类型
     */
    private String returnType;
    /**
     * 参数类型
     */
    private String[] parameterTypes;
    /**
     * 参数类型
     */
    private Class<?>[] parameterClassTypes;
    /**
     * 异常类型
     */
    private String[] exceptionTypes;
    /**
     * 字段信息
     */
    private ParameterDescribe[] parameterDescribes;
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
    private Method method;
    /**
     * 方法体
     */
    private String body;
    /**
     * 额外信息
     */
    private String ext;
    /**
     * 所属实体
     */
    private Object entity;

    /**
     * 解析方法体
     * <p>public String value()</p>
     * <p>String value()</p>
     * <p>value()</p>
     *
     * @param body 方法体
     * @return 方法体
     */
    public static MethodDescribe of(String body) {
        MethodDescribe methodDescribe = new MethodDescribe();
        String nameBefore = body.substring(0, body.indexOf("("));
        String[] split = nameBefore.split("\\s+", 3);
        if (split.length == 3) {
            methodDescribe.modifiers(createModifiers(split[0].toUpperCase()));
            methodDescribe.returnType(split[1]);
            methodDescribe.name(split[2]);
        } else if (split.length == 2) {
            methodDescribe.modifiers(Modifier.PRIVATE);
            methodDescribe.returnType(split[0]);
            methodDescribe.name(split[1]);
        } else {
            methodDescribe.modifiers(Modifier.PUBLIC);
            methodDescribe.name(split[0]);
        }

        methodDescribe.ext(body.substring(body.indexOf("(") + 1, body.length() - 1));
        return methodDescribe;
    }

    private static int createModifiers(String s) {
        if ("PUBLIC".equals(s)) {
            return Modifier.PUBLIC;
        }

        if ("PRIVATE".equals(s)) {
            return Modifier.PRIVATE;
        }
        return Modifier.PROTECTED;
    }

    public void setBody(String body) {
        modify = true;
        this.body = body;
    }

    /**
     * 初始化
     *
     * @param returnType      字段类型
     * @param name            字段名称
     * @param parameterTypes  参数类型
     * @param exceptions      异常类型
     * @param body            内容
     * @param annotationTypes 注解
     * @return 名称
     */
    public static MethodDescribe of(
            Class<?> returnType,
            String name,
            Class<?>[] parameterTypes,
            Class<?>[] exceptions,
            String body,
            AnnotationDescribe[] annotationTypes) {
        return of(Modifier.PUBLIC, returnType, name, parameterTypes, exceptions, body, annotationTypes);
    }

    /**
     * 初始化
     *
     * @param modifiers       作用范围
     * @param returnType      字段类型
     * @param name            字段名称
     * @param parameterTypes  参数类型
     * @param exceptions      异常类型
     * @param body            内容
     * @param annotationTypes 注解
     * @return 名称
     */
    public static MethodDescribe of(int modifiers,
                                    Class<?> returnType,
                                    String name,
                                    Class<?>[] parameterTypes,
                                    Class<?>[] exceptions,
                                    String body,
                                    AnnotationDescribe[] annotationTypes) {

        return of(modifiers,
                returnType.getTypeName(),
                name,
                ClassUtils.toTypeName(parameterTypes),
                ClassUtils.toTypeName(exceptions),
                body,
                annotationTypes);
    }

    /**
     * 初始化
     *
     * @param returnType      字段类型
     * @param name            字段名称
     * @param parameterTypes  参数类型
     * @param exceptions      异常类型
     * @param body            内容
     * @param annotationTypes 注解
     * @return 名称
     */
    public static MethodDescribe of(String returnType,
                                    String name,
                                    String[] parameterTypes,
                                    String[] exceptions,
                                    String body,
                                    AnnotationDescribe[] annotationTypes) {
        return of(Modifier.PUBLIC, returnType, name, parameterTypes, exceptions, body, annotationTypes);
    }

    /**
     * 初始化
     *
     * @param returnType      字段类型
     * @param name            字段名称
     * @param parameterTypes  参数类型
     * @param exceptions      异常类型
     * @param body            内容
     * @param annotationTypes 注解
     * @return 名称
     */
    public static MethodDescribe of(
            int modifiers,
            String returnType,
            String name,
            String[] parameterTypes,
            String[] exceptions,
            String body,
            AnnotationDescribe[] annotationTypes) {
        return MethodDescribe.builder()
                .modifiers(modifiers)
                .name(name)
                .returnType(returnType)
                .parameterTypes(parameterTypes)
                .exceptionTypes(exceptions)
                .body(body)
                .annotationTypes(annotationTypes).build();
    }

    /**
     * 初始化
     *
     * @param method 字段
     * @return 名称
     */
    public static MethodDescribe of(Method method) {
        method.setAccessible(true);

        Annotation[] declaredAnnotations = method.getDeclaredAnnotations();
        AnnotationDescribe[] annotationDescribes = new AnnotationDescribe[declaredAnnotations.length];
        for (int i = 0; i < declaredAnnotations.length; i++) {
            Annotation declaredAnnotation = declaredAnnotations[i];
            annotationDescribes[i] = AnnotationDescribe.of(declaredAnnotation);
        }


        MethodDescribe methodDescribe = of(method.getModifiers(),
                method.getReturnType().getTypeName(),
                method.getName(),
                ClassUtils.toTypeName(method.getParameterTypes()),
                ClassUtils.toTypeName(method.getExceptionTypes()),
                null, annotationDescribes
        );
        methodDescribe.method(method);
        Parameter[] parameters = method.getParameters();
        List<ParameterDescribe> rs = new LinkedList<>();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            rs.add(ParameterDescribe.of(parameter).index(i));
        }
        methodDescribe.parameterDescribes(rs.toArray(new ParameterDescribe[0]));
        checkJsr(annotationDescribes);
        return methodDescribe;
    }

    /**
     * 处理jsr
     *
     * @param annotationDescribes 描述
     */
    private static void checkJsr(AnnotationDescribe[] annotationDescribes) {

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
    public boolean isMethod() {
        return null != method;
    }

    /**
     * 执行方法
     *
     * @param entity  实体
     * @param args    参数
     * @param plugins 插件
     */
    public Value<Object> invoke(Object entity, Object[] args, Object... plugins) {
        if (method != null) {
            return Value.of(MethodAnnotationFactory.create(this, plugins).execute(entity, args));
        }

        return NullValue.INSTANCE;
    }

    /**
     * 是否存在实体
     *
     * @return 是否存在实体
     */
    public boolean hasMember() {
        return null != method;
    }

    /**
     * 构造器
     *
     * @return methodDescribeBuilder
     */
    public static MethodDescribeBuilder builder() {
        return new MethodDescribeBuilder();
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
     * 获取注解
     *
     * @param annotationType 注解类型
     * @return 注解
     */
    public <A> A getAnnotationValue(String annotationType) {
        for (AnnotationDescribe describe : annotationTypes) {
            if (describe.getName().equals(annotationType)) {
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
     * 是否包含注解
     *
     * @param annotationType 注解
     * @return 结果
     */
    public boolean hasAnnotation(Class<? extends Annotation> annotationType) {
        return hasAnnotation(annotationType.getTypeName());
    }

    @Override
    public boolean hasMethodByParameterType(Class<?>[] type) {
        return ArrayUtils.isEquals(type, parameterClassTypes);
    }

    @Override
    public void addAnnotation(Annotation annotation) {
        if (null == this.annotationTypes) {
            annotationTypes = new AnnotationDescribe[0];
        }
        ArrayUtils.addElement(this.annotationTypes, AnnotationDescribe.of(annotation));
    }

    @Override
    public MethodDescribe doChainSelf(Object... args) {
        invoke(entity, args);
        return this;
    }

    @Override
    public MethodDescribe doChain(Object... args) {
        invoke(null, args);
        return this;
    }

    @Override
    public MethodDescribe doChain(Object bean, Object... args) {
        invoke(bean, args);
        return this;
    }

    @Override
    public TypeDescribe isChainSelf() {
        return new TypeDescribe(entity);
    }

    @Override
    public TypeDescribe isChainStatic(Object... args) {
        return new TypeDescribe(invoke(null, args).getValue());
    }

    @Override
    public TypeDescribe isChain(Object bean, Object... args) {
        return new TypeDescribe(invoke(bean, args).getValue());
    }

    @Override
    public FieldDescribe getFieldDescribe(String name) {
        return null;
    }

    @Override
    public FieldDescribeProvider getFieldDescribeProvider(String name) {
        return null;
    }

    @Override
    public MethodDescribe getMethodDescribe(String name, String[] parameterTypes) {
        return this.name.equals(name) && ArrayUtils.isEquals(parameterTypes, this.parameterTypes) ? this : null;
    }

    @Override
    public MethodDescribeProvider getMethodDescribe(String name) {
        return new MethodDescribeProvider().addChains(this.name.equals(name) && ArrayUtils.isEquals(parameterTypes, this.parameterTypes) ? Collections.singletonList(this) : Collections.emptyList());
    }

    @Override
    public MethodDescribeProvider getMethodDescribeByAnnotation(String name) {
        return new MethodDescribeProvider().addChains(Arrays.stream(annotationTypes).anyMatch(it -> it.getName().equals(name)) ? Collections.singletonList(this) : Collections.emptyList());
    }

    @Override
    public List<FieldDescribe> getFieldDescribeByAnnotation(String name) {
        return Collections.emptyList();
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

    /**
     * 是否包含注解
     *
     * @param annotationType 注解
     * @return 结果
     */
    @Override
    public boolean hasAnnotation(String annotationType) {
        for (AnnotationDescribe annotationDescribe : annotationTypes) {
            if (annotationDescribe.getName().equals(annotationType)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 获取参数
     *
     * @param subList 参数
     * @return 结果
     */
    public Object[] getParam(List<String> subList) {
        if (parameterDescribes.length == 0) {
            return EMPTY_OBJECT;
        }
        Object[] objects = new Object[parameterDescribes.length];
        for (int i = 0; i < parameterDescribes.length; i++) {
            ParameterDescribe parameterClassType = parameterDescribes[i];
            objects[i] = guess(subList, parameterClassType);
        }

        return objects;
    }

    private static final MethodDescribeProvider methodDescribe;

    static {
        TypeDescribe typeDescribe = new TypeDescribe(UTILS);
        methodDescribe = typeDescribe.getMethodDescribe("getApplicationContext")
                .isChain().getMethodDescribe("getBeansOfType");
    }

    private Object guess(List<String> subList, ParameterDescribe parameterClassType) {
        if (subList.isEmpty()) {
            Object forObject = ClassUtils.forObject(parameterClassType.returnClassType());
            if (null != forObject) {
                return forObject;
            }

            Map map = methodDescribe.executeSelf(Map.class, parameterClassType.returnClassType());
            return MapUtils.getFirst(map).getValue();
        }
        int index = parameterClassType.index();
        String s = CollectionUtils.find(subList, index);
        if (null != s) {
            Object value = guessConverter(s, parameterClassType.returnClassType());
            if (null != value) {
                return value;
            }
        }

        for (String s1 : subList) {
            Object o = guessConverter(s1, parameterClassType.returnClassType());
            if (null != o) {
                return o;
            }
        }

        return null;
    }

    private Object guessConverter(String s, Class<?> returnClassType) {
        Object convertIfNecessary = Converter.convertIfNecessary(s, returnClassType);
        if (null != convertIfNecessary) {
            return convertIfNecessary;
        }

        try {
            Object fromJson = Json.fromJson(s, returnClassType);
            if (null != fromJson) {
                return fromJson;
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    /**
     * 构造器
     */
    public static class MethodDescribeBuilder {

        private final MethodDescribe methodDescribe = new MethodDescribe();
        private final List<String> parameterTypes = new LinkedList<>();
        private final List<Class<?>> parameterClassTypes = new LinkedList<>();
        private final List<String> exceptionTypes = new LinkedList<>();
        private final List<AnnotationDescribe> annotationDescribes = new LinkedList<>();
        private String body;
        private Method method;

        /**
         * 范围
         *
         * @param modifiers 范围
         * @return this
         */
        public MethodDescribeBuilder modifiers(int modifiers) {
            methodDescribe.modifiers(modifiers);
            return this;
        }

        /**
         * 名称
         *
         * @param name 名称
         * @return this
         */
        public MethodDescribeBuilder name(String name) {
            methodDescribe.name(name);
            return this;
        }

        /**
         * 名称
         *
         * @param name 名称
         * @return this
         */
        public MethodDescribeBuilder getter(String name) {
            methodDescribe.name(METHOD_GETTER + NamingCase.toFirstUpperCase(name));
            return this;
        }

        /**
         * 名称
         *
         * @param name 名称
         * @return this
         */
        public MethodDescribeBuilder setter(String name) {
            methodDescribe.name(METHOD_SETTER + NamingCase.toFirstUpperCase(name));
            return this;
        }

        /**
         * 返回值
         *
         * @param returnType 返回值
         * @return this
         */
        public MethodDescribeBuilder returnType(String returnType) {
            methodDescribe.returnType(returnType);
            return this;
        }

        /**
         * 返回值
         *
         * @param returnType 返回值
         * @return this
         */
        public MethodDescribeBuilder returnType(Class<?> returnType) {
            methodDescribe.returnType(returnType.getTypeName());
            return this;
        }

        /**
         * 参数类型
         *
         * @param parameterTypes 参数类型
         * @return this
         */
        public MethodDescribeBuilder parameterTypes(String... parameterTypes) {
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
        public MethodDescribeBuilder parameterClassTypes(Class<?>... parameterTypes) {
            if (ArrayUtils.isEmpty(parameterTypes)) {
                return this;
            }

            this.parameterClassTypes.clear();
            this.parameterClassTypes.addAll(Arrays.asList(parameterTypes));

            return parameterTypes(Arrays.stream(parameterTypes).map(Class::getTypeName).toArray(String[]::new));
        }

        /**
         * 参数类型
         *
         * @param parameterTypes 参数类型
         * @return this
         */
        public MethodDescribeBuilder parameterTypes(Class<?>... parameterTypes) {
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
        public MethodDescribeBuilder withParameterTypes(String... parameterTypes) {
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
        public MethodDescribeBuilder withParameterTypes(Class<?>... parameterTypes) {
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
        public MethodDescribeBuilder exceptionTypes(String... exceptionTypes) {
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
        public MethodDescribeBuilder exceptionTypes(Class<?>... exceptionTypes) {
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
        public MethodDescribeBuilder withExceptionTypes(String... exceptionTypes) {
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
        public MethodDescribeBuilder withExceptionTypes(Class<?>... exceptionTypes) {
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
        public MethodDescribeBuilder annotationTypes(AnnotationDescribe... annotationDescribes) {
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
        public MethodDescribeBuilder withAnnotationType(AnnotationDescribe... annotationDescribes) {
            this.annotationDescribes.addAll(Arrays.asList(annotationDescribes));
            return this;
        }

        /**
         * body
         *
         * @param body body
         * @return this
         */
        public MethodDescribeBuilder body(String body) {
            this.methodDescribe.body(body);
            return this;
        }

        /**
         * body
         *
         * @param bodyDescribe body
         * @return this
         */
        public MethodDescribeBuilder body(BodyDescribe bodyDescribe) {
            this.methodDescribe.body(bodyDescribe.toString());
            return this;
        }

        /**
         * method
         *
         * @param method method
         * @return this
         */
        public MethodDescribeBuilder method(Method method) {
            MethodDescribe methodDescribe = of(method);
            name(methodDescribe.name());
            withAnnotationType(methodDescribe.annotationTypes());
            parameterClassTypes(method.getParameterTypes());
            methodDescribe.method(method);
            withParameterTypes(methodDescribe.parameterTypes());
            withExceptionTypes(methodDescribe.exceptionTypes());
            returnType(method.getReturnType());
            return this;
        }

        /**
         * 注解
         *
         * @param annotationTypes 注解
         * @return this
         */
        public MethodDescribeBuilder withAnnotationType(Class<?>... annotationTypes) {
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
        public MethodDescribe build() {
            methodDescribe
                    .parameterClassTypes(parameterClassTypes.toArray(EMPTY_CLASS))
                    .parameterTypes(parameterTypes.toArray(EMPTY_ARRAY))
                    .exceptionTypes(exceptionTypes.toArray(EMPTY_ARRAY))
                    .annotationTypes(annotationDescribes.toArray(new AnnotationDescribe[0]));
            return methodDescribe;
        }

    }
}
