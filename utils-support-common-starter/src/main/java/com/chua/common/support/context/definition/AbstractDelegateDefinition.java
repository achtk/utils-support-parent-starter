package com.chua.common.support.context.definition;

import com.chua.common.support.bean.BeanUtils;
import com.chua.common.support.context.factory.ConfigurableBeanFactory;
import com.chua.common.support.context.resolver.factory.AutoInjectHandler;
import com.chua.common.support.context.value.AutoValueHandler;
import com.chua.common.support.function.DisposableAware;
import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.lang.proxy.BridgingMethodIntercept;
import com.chua.common.support.lang.proxy.DelegateMethodIntercept;
import com.chua.common.support.lang.proxy.ProxyUtils;
import com.chua.common.support.lang.proxy.VoidMethodIntercept;
import com.chua.common.support.reflection.Reflect;
import com.chua.common.support.utils.ClassUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 定义
 *
 * @author CH
 */
public abstract class AbstractDelegateDefinition<T> implements DelegateDefinition<T>, InitializingAware, DisposableAware {
    /**
     * 命名
     */
    private final Set<String> names = new LinkedHashSet<>();
    private final Set<String> interfaceNames = new LinkedHashSet<>();
    private final Set<Class<?>> interfaceTypes = new LinkedHashSet<>();

    private final Set<String> withSuperNames = new LinkedHashSet<>();
    private final Set<Class<?>> withSuperTypes = new LinkedHashSet<>();
    private int order;
    private boolean isProxy;
    private boolean isSingle = true;
    private Class<?> type;
    private ClassLoader classLoader;
    protected T object;

    protected boolean isLoaded;
    protected boolean isInjectLoaded;
    @Override
    public TypeDefinition<T> order(int order) {
        this.order = order;
        return this;
    }

    @Override
    public TypeDefinition<T> setProxy(boolean proxy) {
        this.isProxy = proxy;
        return this;
    }

    @Override
    public void single(boolean single) {
        this.isSingle = single;
    }

    /**
     * 添加类型
     * @param type 类型
     */
    protected void setType(Class<?> type) {
        this.type = type;
    }

    /**
     * 添加加载器
     * @param classLoader 加载器
     */
    protected void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * 添加类型
     * @param name 名称
     */
    @Override
    public TypeDefinition<T> addBeanName(String... name) {
        this.names.addAll(Arrays.asList(name));
        return this;
    }
    /**
     * 添加类型
     * @param name 名称
     */
    protected void addSuperType(Class<?>... name) {
        this.withSuperTypes.addAll(Arrays.asList(name));
        this.withSuperNames.addAll(Arrays.stream(name).map(Class::getTypeName).collect(Collectors.toList()));
    }

    /**
     * 添加类型
     * @param name 名称
     */
    protected void addInterfaceType(Class<?>... name) {
        this.interfaceTypes.addAll(Arrays.asList(name));
        this.interfaceNames.addAll(Arrays.stream(name).map(Class::getTypeName).collect(Collectors.toList()));
    }

    @Override
    public String[] getBeanName() {
        return names.toArray(new String[0]);
    }

    @Override
    public boolean isSingle() {
        return isSingle;
    }

    @Override
    public boolean isProxy() {
        return isProxy;
    }


    @Override
    public int order() {
        return order;
    }

    @Override
    public Class<?> getType() {
        return type;
    }


    @Override
    public Class<?>[] getSuperTypes() {
        return withSuperTypes.toArray(new Class[0]);
    }

    @Override
    public boolean isAssignableFrom(Class<?> type) {
        return type == Object.class || interfaceTypes.contains(type);
    }

    @Override
    public boolean isAssignableFrom(String type) {
        return interfaceNames.contains(type);
    }

    @Override
    public Class<?>[] getTypes() {
        Set<Class<?>> types = new LinkedHashSet<>();
        types.addAll(interfaceTypes);
        types.addAll(withSuperTypes);
        if (null != type) {
            types.add(type);
        }
        return types.toArray(new Class[0]);
    }


    @Override
    public T getObject(Object... args) {
        if (args.length != 0 || !isSingle) {
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

        return object;
    }

    @Override
    public T getObject(ConfigurableBeanFactory context, boolean reload) {
        if (!isSingle) {
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

        if(!isInjectLoaded && reload) {
            object = injectBean(object, context);
        }
        return object;

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

        tpl = injectBean(tpl, context);
        return tpl;
    }

    public T injectBean(T tpl, ConfigurableBeanFactory context) {
        try {
            if(!isInjectLoaded) {
                T inject = inject(tpl, context);
                if (isProxy) {
                    tpl = (T) ProxyUtils.proxy(type, getClassLoader(), new BridgingMethodIntercept(type, inject));
                    if(null != tpl) {
                        BeanUtils.copyProperties(inject, tpl);
                        inject = tpl;
                    }
                }
                return inject;
            }
            return tpl;
        } finally {
            isInjectLoaded = true;
        }
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

        Map<String, AutoValueHandler> map = new LinkedHashMap<>();
        Class<?> aClass = type;
        ClassUtils.doWithFields(aClass, field -> {
            if (Modifier.isStatic(field.getModifiers())) {
                return;
            }
            map.put(field.getName(), new AutoValueHandler(field, tpl, context));
            new AutoInjectHandler(field, tpl, context);
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


    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    /**
     * 创建对象
     *
     * @param object 对象
     * @return 结果
     */
    @SuppressWarnings("ALL")
    private T create(Object object) {
        if (!isProxy) {
            return (T) object;
        }

        return (T) ProxyUtils.proxy(type, getClassLoader(), new VoidMethodIntercept<>());
    }

    @Override
    public TypeDefinition<T> setObject(T obj) {
        this.object = obj;
        return this;
    }

    @Override
    public void destroy() {

    }
}
