package com.chua.common.support.reflection.describe;

import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.reflection.MethodStation;
import com.chua.common.support.reflection.describe.provider.FieldDescribeProvider;
import com.chua.common.support.reflection.describe.provider.MethodDescribeProvider;
import com.chua.common.support.utils.ArrayUtils;
import com.chua.common.support.utils.ClassUtils;
import lombok.Data;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 类描述
 *
 * @author CH
 */
@Data
public class TypeDescribe implements MemberDescribe<TypeDescribe>, InitializingAware {
    /**
     * 对象
     */
    protected Object object;
    /**
     * 类型
     */
    private final Class<?> beanClass;
    private boolean isDeep;
    /**
     * 名称
     */
    private String name;

    /**
     * 注解
     */
    private AnnotationDescribe[] annotationTypes;
    /**
     * 方法描述
     */
    private List<MethodDescribe> methodDescribes;
    /**
     * 字段描述
     */
    private List<FieldDescribe> fieldDescribes;
    private List<FieldDescribe> fieldAllDescribes = new LinkedList<>();
    private List<MethodDescribe> methodAllDescribes = new LinkedList<>();
    final Set<Class<?>> cache = new LinkedHashSet<>();
    final Set<Class<?>> methodCache = new LinkedHashSet<>();

    public TypeDescribe(Class<?> beanClass) {
        this.object = null;
        this.beanClass = beanClass;
        afterPropertiesSet();
    }

    public TypeDescribe(Object object, boolean isDeep) {
        this.object = object;
        this.beanClass = ClassUtils.toType(object);
        this.isDeep = isDeep;
        afterPropertiesSet();
    }

    public TypeDescribe(Object object) {
        this(object, false);
    }

    public TypeDescribe(String beanName) {
        this(ClassUtils.forName(beanName));
        this.object = ClassUtils.forObject(beanName);
        afterPropertiesSet();
    }

    public boolean isPresent() {
        return object instanceof String && ClassUtils.isPresent(object.toString());
    }

    /**
     * 类型描述
     *
     * @param beanClass 类型
     * @return this
     */
    public static TypeDescribe create(Class<?> beanClass) {
        return new TypeDescribe(beanClass);
    }

    /**
     * 类型描述
     *
     * @param bean bean
     * @return this
     */
    public static TypeDescribe create(Object bean) {
        return new TypeDescribe(bean);
    }

    /**
     * 类型描述
     *
     * @param bean bean
     * @param deep 深度解析
     * @return this
     */
    public static TypeDescribe create(Object bean, boolean deep) {
        return new TypeDescribe(bean, deep);
    }

    @Override
    public FieldDescribe getFieldDescribe(String name) {
        Optional<FieldDescribe> first = fieldDescribes.stream().filter(it -> it.name().equals(name))
                .findFirst();
        FieldDescribe fieldDescribe = first.map(it -> it.entity(object)).orElse(FieldDescribe.INSTANCE);
        if (fieldDescribe == FieldDescribe.INSTANCE) {
            return fieldAllDescribes.stream().filter(it -> it.name().equals(name))
                    .findFirst().orElse(FieldDescribe.INSTANCE);
        }
        return fieldDescribe;
    }

    @Override
    public FieldDescribeProvider getFieldDescribeProvider(String name) {
        return new FieldDescribeProvider().addChain(getFieldDescribe(name));
    }

    @Override
    public MethodDescribe getMethodDescribe(String name, String[] parameterTypes) {
        Optional<MethodDescribe> first = methodDescribes.stream().filter(it -> {
            return it.name().equals(name) && ArrayUtils.isEquals(Arrays.stream(it.parameterDescribes()).map(ParameterDescribe::returnType).toArray(String[]::new), parameterTypes);
        }).findFirst();
        MethodDescribe methodDescribe1 = first.map(methodDescribe -> methodDescribe.entity(object)).orElse(MethodDescribe.INSTANCE);
        if(methodDescribe1 != MethodDescribe.INSTANCE) {
            return methodDescribe1;
        }

        return methodAllDescribes.stream().filter(it -> {
            return it.name().equals(name) && ArrayUtils.isEquals(Arrays.stream(it.parameterDescribes()).map(ParameterDescribe::returnType).toArray(String[]::new), parameterTypes);
        }).findFirst().map(methodDescribe -> methodDescribe.entity(object)).orElse(MethodDescribe.INSTANCE);
    }

    @Override
    public MethodDescribeProvider getMethodDescribe(String name) {
       return new MethodDescribeProvider()
                .addChains(methodDescribes.stream().filter(it -> it.name().equals(name)).collect(Collectors.toList()));
    }

    @Override
    public MethodDescribeProvider getMethodDescribeByAnnotation(String name) {
        return new MethodDescribeProvider()
                .addChains(methodDescribes.stream().filter(it -> Arrays.stream(it.annotationTypes()).anyMatch(it1 -> name.equals(it1.getName()))).collect(Collectors.toList()));
    }

    @Override
    public List<FieldDescribe> getFieldDescribeByAnnotation(String name) {
        return fieldDescribes.stream().filter(it -> Arrays.stream(it.annotationTypes()).anyMatch(it1 -> name.equals(it1.getName()))).collect(Collectors.toList());
    }

    @Override
    public boolean hasAnyAnnotation(String[] name) {
        for (String s : name) {
            if (Arrays.stream(annotationTypes).map(AnnotationDescribe::getName).anyMatch(it -> it.equals(s)) ||
                    methodDescribes.stream().anyMatch(it -> it.hasAnnotation(s)) ||
                    fieldDescribes.stream().anyMatch(it -> it.hasAnnotation(s))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasAnnotation(String name) {
        return Arrays.stream(annotationTypes).map(AnnotationDescribe::getName).anyMatch(it -> it.equals(name));
    }

    @Override
    public boolean hasMethodByParameterType(Class<?>[] type) {
        return false;
    }

    @Override
    public void addAnnotation(Annotation annotation) {
        if (null == this.annotationTypes) {
            annotationTypes = new AnnotationDescribe[0];
        }
        ArrayUtils.addElement(this.annotationTypes, AnnotationDescribe.of(annotation));
    }

    @Override
    public TypeDescribe doChainSelf(Object... args) {
        return this;
    }

    @Override
    public TypeDescribe doChain(Object... args) {
        return this;
    }

    @Override
    public TypeDescribe doChain(Object bean, Object... args) {
        return this;
    }

    @Override
    public TypeDescribe isChainSelf() {
        return this;
    }

    @Override
    public TypeDescribe isChainStatic(Object... args) {
        return this;
    }

    @Override
    public TypeDescribe isChain(Object bean, Object... args) {
        return new TypeDescribe(bean);
    }

    /**
     * 获取值
     *
     * @param name 名称
     * @return 值
     */
    public TypeAttribute getFieldAttribute(String name) {
        return TypeAttribute.create(getFieldDescribe(name).get(object).getValue());
    }

    /**
     * 获取值
     *
     * @param name 名称
     * @return 值
     */
    public Object getFieldValue(String name) {
        return getFieldValue(name, Object.class);
    }

    /**
     * 获取值
     *
     * @param name   名称
     * @param target 类型
     * @return 值
     */
    public <T> T getFieldValue(String name, Class<T> target) {
        return getFieldDescribe(name).get(object).getValue(target);
    }

    /**
     * 获取MethodStation
     *
     * @return MethodStation
     */
    public MethodStation getMethodStation() {
        return MethodStation.of(object);
    }

    @Override
    public void afterPropertiesSet() {
        this.name = beanClass.getName();
        this.annotationTypes = Arrays.stream(beanClass.getDeclaredAnnotations())
                .map(AnnotationDescribe::of)
                .toArray(AnnotationDescribe[]::new);
        this.methodDescribes = ClassUtils.getMethods(beanClass).stream()
                .map(MethodDescribe::of)
                .map(it -> it.entity(object))
                .collect(Collectors.toList());
        this.fieldDescribes = ClassUtils.getFields(beanClass).stream()
                .map(FieldDescribe::of)
                .map(it -> it.entity(object))
                .collect(Collectors.toList());
        if(isDeep) {
            doField(beanClass, object);
//            doMethod(beanClass, object);
        }
    }

    private void doMethod(Class<?> beanClass, Object bean) {
        if (methodCache.contains(beanClass)) {
            return;
        }

        methodCache.add(beanClass);

        ClassUtils.doWithMethods(beanClass, method -> {
            this.methodAllDescribes.add(MethodDescribe.of(method)
                    .entity(bean)
            );
            method.setAccessible(true);
            Object o = null;
            try {
                o = method.invoke(bean);
            } catch (Exception ignored) {
                return;
            }

            if (null == o) {
                return;
            }
            Class<?> type = o.getClass();
            if (type.isInterface()) {
                return;
            }

            if (ClassUtils.isJavaType(type)) {
                return;
            }

            if (Object.class == type) {
                return;
            }

            doMethod(type, o);
        });
    }



    private void doField(Class<?> beanClass, Object bean) {
        if (cache.contains(beanClass)) {
            return;
        }

        cache.add(beanClass);

        ClassUtils.doWithFields(beanClass, field -> {
            this.fieldAllDescribes.add(FieldDescribe.of(field)
                    .entity(bean)
            );
            field.setAccessible(true);
            Object o = null;
            try {
                o = field.get(bean);
            } catch (IllegalAccessException ignored) {
                return;
            }

            if (null == o) {
                return;
            }
            Class<?> type = o.getClass();
            if (type.isInterface()) {
                return;
            }

            if (ClassUtils.isJavaType(type)) {
                return;
            }

            if (Object.class == type) {
                return;
            }

            doField(type, o);
        });
    }

    public GenericDescribe getActualTypeArguments() {
        return new GenericDescribe(beanClass);
    }

    public Object getBean() {
        return object;
    }


}
