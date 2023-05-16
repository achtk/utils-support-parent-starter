package com.chua.common.support.context.definition;

import com.chua.common.support.context.enums.Scope;
import com.chua.common.support.context.factory.ConfigurableBeanFactory;
import com.chua.common.support.context.resolver.*;
import com.chua.common.support.context.resolver.factory.AutoInjectHandler;
import com.chua.common.support.context.value.AutoValueHandler;
import com.chua.common.support.reflection.Reflect;
import com.chua.common.support.reflection.craft.MethodCraftTable;
import com.chua.common.support.reflection.describe.MethodDescribe;
import com.chua.common.support.reflection.marker.Marker;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.AnnotationUtils;
import com.chua.common.support.utils.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.*;

import static com.chua.common.support.constant.CommonConstant.EMPTY_STRING_ARRAY;

/**
 * 类型定义
 * @author CH
 */
public class ClassDefinition<T> extends AbstractDelegateDefinition<T>{

    private final Class<T> type;
    protected Scope scope = Scope.SINGLE;


    private final Set<String> interfaces = new LinkedHashSet<>();
    private final Set<Class<?>> interfaceTypes = new LinkedHashSet<>();

    private final Set<String> withSuperType = new LinkedHashSet<>();
    private final Set<Class<?>> withSuperTypes = new LinkedHashSet<>();

    private final Set<String> names = new LinkedHashSet<>();
    private int order;

    protected T object;

    protected boolean isLoaded;
    private boolean isProxy;

    private final MethodCraftTable methodCraftTable;

    public ClassDefinition(Class<T> type, String... name) {
        this.type = type;
        this.names.addAll(Arrays.asList(name));
        this.methodCraftTable = new MethodCraftTable(type);
        afterPropertiesSet();
    }

    @Override
    public String[] getBeanName() {
        return names.toArray(EMPTY_STRING_ARRAY);
    }

    @Override
    public TypeDefinition<T> setObject(T object) {
        if (null == object) {
            return this;
        }

        this.object = object;
        this.isLoaded = true;

        return this;
    }

    @Override
    public TypeDefinition<T> addBeanName(String... name) {
        this.names.addAll(Arrays.asList(name));
        return this;
    }

    @Override
    public boolean isSingle() {
        return scope == Scope.SINGLE;
    }

    @Override
    public TypeDefinition<T> setProxy(boolean isProxy) {
        if (isProxy != this.isProxy) {
            this.isLoaded = false;
            this.object = null;
        }
        this.isProxy = isProxy;
        return this;
    }

    @Override
    public boolean isProxy() {
        return isProxy;
    }

    @Override
    public String[] annotationTypes() {
        Set<Annotation> rs = AnnotationUtils.getAllAnnotations(type);
        String[] rs1 = new String[rs.size()];
        int count = 0;
        for (Annotation annotation : rs) {
            rs1[count++] = annotation.annotationType().getTypeName();
        }

        return rs1;
    }

    @Override
    public int order() {
        return order;
    }

    @Override
    public TypeDefinition<T> order(int order) {
        this.order = order;
        return this;
    }

    @Override
    public ClassLoader getClassLoader() {
        return type.getClassLoader();
    }


    @Override
    public boolean isAssignableFrom(Class<?> type) {
        return type == Object.class ? true : interfaceTypes.contains(type);
    }

    @Override
    public boolean isAssignableFrom(String type) {
        return interfaces.contains(type);
    }

    @Override
    public Class<?>[] getTypes() {
        List<Class<?>> tpl = new ArrayList<>(withSuperTypes);
        tpl.addAll(Arrays.asList(super.getTypes()));
        return tpl.toArray(new Class[0]);
    }

    @Override
    public Class<?>[] getSuperTypes() {
        return withSuperTypes.toArray(new Class[0]);
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public T getObject(Object... args) {
        if (args.length != 0 || Scope.SINGLE != scope) {
            isLoaded = false;
            object = null;
        }

        if (!isLoaded && null == object) {
            synchronized (this) {
                if (!isLoaded && null == object) {
                    isLoaded = true;
                    return (object = create(Reflect.create(type).getObjectValue(args).getValue()));
                }
            }
        }

        return create(Reflect.create(type).getObjectValue(args).getValue());
    }

    @Override
    public T getObject(ConfigurableBeanFactory context) {
        if (Scope.SINGLE != scope) {
            return newInstance(context);
        }

        if (!isLoaded && null == object) {
            synchronized (this) {
                if (!isLoaded && null == object) {
                    isLoaded = true;
                    T tpl = newInstance(context);
                    return (object = tpl);
                }
            }
        }

        return object;

    }

    @Override
    public boolean hasMethodAnnotation(Class<? extends Annotation> annotationType) {
        return methodCraftTable.hasMethodAnnotation(annotationType);
    }

    @Override
    public MethodDescribe createMethodDefinition(String name, Class<?>... type) {
        return methodCraftTable.get(name, ClassUtils.toTypeName(type));
    }

    @Override
    public boolean hasMethodByParameterType(Class<?>[] type) {
        return methodCraftTable.hasMethodByParameterType(type);
    }

    @Override
    public void addAnnotation(Annotation annotation) {

    }

    /**
     * 实例化
     *
     * @param context 上下文
     * @return 实例
     */
    private T newInstance(ConfigurableBeanFactory context) {
        T tpl = null;
        Constructor<?>[] declaredConstructors = type.getDeclaredConstructors();
        for (Constructor<?> declaredConstructor : declaredConstructors) {
            tpl = newInstance(declaredConstructor, context);
            if (null != tpl) {
                break;
            }
        }

        if(!isProxy) {
            return inject(tpl, context);
        }
        T inject = Marker.proxy(type, tpl);
        inject(tpl, context);
        return inject(inject, context);
    }

    /**
     * 注入
     *
     * @param tpl     对象
     * @param context 上下文
     * @return 对象
     */
    private T inject(T tpl, ConfigurableBeanFactory context) {
        if (null == tpl) {
            return null;
        }

        Class<?> aClass = type;
        ClassUtils.doWithFields(aClass, field -> {
            if (Modifier.isStatic(field.getModifiers())) {
                return;
            }
            new AutoValueHandler(field, tpl, context);
            new AutoInjectHandler(field, tpl, context).afterPropertiesSet();
        });

        ClassUtils.doWithMethods(aClass, method -> {
            if (Modifier.isStatic(method.getModifiers())) {
                return;
            }

            Class<?> returnType = method.getReturnType();
            boolean exp = method.getName().startsWith("set") && (void.class == returnType || Void.class == returnType) && method.getParameterCount() == 1;
            if (exp) {
                Class<?> paramType = method.getParameterTypes()[0];
                Object bean = context.getBean(paramType);
                if (null == bean) {
                    return;
                }

                method.setAccessible(true);
                ClassUtils.invokeMethod(method, tpl, bean);
            }

        });


        return tpl;
    }

    /**
     * 实例化
     *
     * @param declaredConstructor 构造
     * @param context             上下文
     * @return 实例
     */
    private T newInstance(Constructor<?> declaredConstructor, ConfigurableBeanFactory context) {
        Class<?>[] parameterTypes = declaredConstructor.getParameterTypes();
        Object[] objects = createArgs(parameterTypes, context);
        if (null == objects) {
            return null;
        }

        try {
            declaredConstructor.setAccessible(true);
            return (T) declaredConstructor.newInstance(objects);
        } catch (Exception ignore) {
        }

        return null;
    }

    /**
     * 获取参数
     *
     * @param parameterTypes 类型
     * @param context        上下文
     * @return 参数
     */
    private Object[] createArgs(Class<?>[] parameterTypes, ConfigurableBeanFactory context) {
        Object[] rs = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            Object object1 = context.getBean(parameterType);
            if (null == object1) {
                return null;
            }
            rs[i] = object1;
        }

        return rs;
    }

    /**
     * 创建对象
     *
     * @param object 对象
     * @return 结果
     */
    private T create(T object) {
        if (!isProxy) {
            return object;
        }

        return Marker.proxy(type);
    }

    @Override
    public void afterPropertiesSet() {
        if (null == type) {
            return;
        }

        ServiceProvider.of(ScopeResolver.class).forEach((k, v) -> {
            this.scope = v.scope(type);
        });

        ServiceProvider.of(NamedResolver.class).forEach((k, v) -> {
            String[] named = v.resolve(NamePair.builder().type(type).build());
            names.addAll(Arrays.asList(named));
        });

        ServiceProvider.of(OrderResolver.class).forEach((k, v) -> {
            order = v.resolve(NamePair.builder().type(type).build());
        });


        if(!Modifier.isFinal(type.getModifiers())) {
            ServiceProvider.of(ProxyResolver.class).forEach((k, v) -> {
                isProxy = v.isProxy(type);
            });
        }


        ClassUtils.withInterface(type, it -> {
            interfaceTypes.add(it);
            interfaces.add(it.getTypeName());
        });

        ClassUtils.withSuperType(type, it -> {
            withSuperTypes.add(it);
            withSuperType.add(it.getTypeName());
        });

        withSuperTypes.add(type);
        withSuperType.add(type.getTypeName());

    }
}
