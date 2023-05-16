package com.chua.common.support.reflection;

import com.chua.common.support.reflection.craft.ConstructCraftTable;
import com.chua.common.support.unit.name.NamingCase;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.MapUtils;
import lombok.Setter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * 构造集合
 *
 * @author CH
 * @version 1.0.0
 */
public class ConstructorStation<T> extends ConstructCraftTable {

    private static final Map<Class<?>, Object> TIME_MAP = new ConcurrentHashMap<>();
    private static final Map<Object, ConstructorStation> CACHE_SELF = new ConcurrentHashMap<>();
    private static final ConstructorStation INSTANCE = new ConstructorStation<>(null);
    private final Map<String, Object> params = new HashMap<>();
    private final List<ClassLoader> loaders = new LinkedList<>();
    private FieldStation fieldStation;
    private MethodStation methodStation;
    private Class<?> type;
    @Setter
    private T entity;

    /**
     * 初始化
     *
     * @param entity 实体
     */
    protected ConstructorStation(T entity) {
        super(ClassUtils.toType(entity));
        this.entity = entity;
        if (null != entity) {
            this.type = ClassUtils.toType(entity);
            this.fieldStation = FieldStation.of(entity);
            this.methodStation = MethodStation.of(entity);
        }
    }

    /**
     * 添加类加载器
     *
     * @param loaders 类加载器
     * @return this
     */
    public ConstructorStation<T> addClassLoader(List<ClassLoader> loaders) {
        if (entity != null) {
            this.loaders.addAll(loaders);
        }
        return this;
    }

    /**
     * 添加类加载器
     *
     * @param loaders 类加载器
     * @return this
     */
    public ConstructorStation<T> addClassLoader(ClassLoader[] loaders) {
        if (entity != null) {
            this.loaders.addAll(Arrays.asList(loaders));
        }
        return this;
    }

    /**
     * 添加类加载器
     *
     * @param loaders 类加载器
     * @return this
     */
    public ConstructorStation<T> addClassLoader(ClassLoader loaders) {
        if (entity != null) {
            this.loaders.add(loaders);
        }
        return this;
    }

    /**
     * 添加参数
     *
     * @param params 参数
     * @return this
     */
    public ConstructorStation<T> addParams(Map<String, Object> params) {
        if (entity != null) {
            params.forEach(this::addParams);
        }
        return this;
    }

    /**
     * 添加参数
     *
     * @param paramKey   索引
     * @param paramValue 值
     * @return this
     */
    public ConstructorStation<T> addParams(String paramKey, Object paramValue) {
        if (entity != null) {
            this.params.put(NamingCase.toHyphenLowerCamel(paramKey), paramValue);
        }
        return this;
    }

    /**
     * 遍历构造
     */
    public void doWith(Consumer<Constructor<?>> consumer) {
        for (Constructor<?> constructor : type.getDeclaredConstructors()) {
            consumer.accept(constructor);
        }
    }

    /**
     * 获取所有构造
     *
     * @return 构造
     */
    public List<Constructor<?>> getConstructors() {
        List<Constructor<?>> result = new ArrayList<>();
        doWith(result::add);
        return result;
    }

    /**
     * 是否存在无参构造
     *
     * @return 无参构造
     */
    public boolean hasNoArgs() {
        List<Constructor<?>> result = new ArrayList<>();
        doWith(constructor -> {
            if (constructor.getParameterCount() == 0 && Modifier.isPublic(constructor.getModifiers())) {
                result.add(constructor);
            }
        });
        return !result.isEmpty();
    }

    /**
     * 是否包含静态的对象
     *
     * @return 是否包含静态的对象
     */
    public boolean hasStaticInstance() {
        return (long) fieldStation.listLocalFields(field -> Modifier.isStatic(field.getModifiers()) && field.getType().isAssignableFrom(type)).size() > 0;
    }

    /**
     * 是否包含静态无参函数
     *
     * @return 是否包含静态无参函数
     */
    public boolean hasStaticNoArgsMethod() {
        return (long) methodStation.listLocalMethods(method -> method.getParameterCount() == 0 && Modifier.isStatic(method.getModifiers()) && method.getReturnType().isAssignableFrom(type)).size() > 0;
    }

    /**
     * 实例化
     *
     * @param params 参数
     * @param <R>    类型
     * @return 实例化
     */
    public <R> R newInstance(Object... params) {
        for (Object param : params) {
            addParams(param.getClass().getSimpleName(), param);
        }
        return newInstance(false);
    }

    /**
     * 实例化
     *
     * @param <R> 类型
     * @return 对象
     */
    public <R> R newInstance() {
        return newInstance(type);
    }

    /**
     * 实例化
     *
     * @param inCache 是否加载缓存
     * @param <R>     类型
     * @return 对象
     */
    public <R> R newInstance(boolean inCache) {
        if (entity == null) {
            return null;
        }

        if (List.class == type) {
            return (R) new ArrayList();
        }

        if (Set.class == type) {
            return (R) new HashSet<>();
        }
        if (TIME_MAP.containsKey(type) && inCache) {
            return (R) TIME_MAP.get(type);
        }

        if (loaders.isEmpty()) {
            loaders.add(ClassUtils.getDefaultClassLoader());
        }

        if (hasNoArgs()) {
            if (inCache) {
                return (R) MapUtils.getComputeIfAbsent(TIME_MAP, type, createParameterisedConstruction());
            } else {
                return createParameterisedConstruction();
            }
        }
        if (inCache) {
            return (R) MapUtils.getComputeIfAbsent(TIME_MAP, type, createHasParameterStructure());
        } else {
            return createHasParameterStructure();
        }
    }

    @Override
    public String toString() {
        return "ConstructorStation{" +
                "entity=" + entity +
                '}';
    }

    /**
     * 实例化
     *
     * @param constructor 构造
     * @param params      参数
     * @return 实例化
     */
    public static <T> T newInstance(Constructor<T> constructor, Object... params) {
        try {
            return null == constructor ? null : constructor.newInstance(params);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 初始化
     *
     * @param t   对象
     * @param <T> 类型
     * @return this
     */
    public static <T> ConstructorStation<T> of(T t) {
        if (null == t) {
            return INSTANCE;
        }
        ConstructorStation<T> constructorStation;
        if (CACHE_SELF.containsKey(t)) {
            constructorStation = CACHE_SELF.get(t);
            constructorStation.setEntity(t);
        } else {
            constructorStation = new ConstructorStation<>(t);
        }
        return constructorStation;
    }

    /**
     * 有参构造
     *
     * @param <R> 类型
     * @return 对象
     */
    private <R> R createHasParameterStructure() {
        List<Constructor<?>> constructors = getConstructors();
        for (Constructor<?> constructor : constructors) {
            for (Object[] p : makeParams(constructor.getParameterTypes())) {
                Object invoke = invoke(constructor, p);
                if (null != invoke) {
                    return (R) invoke;
                }
            }
        }
        return null;
    }

    /**
     * 执行方法
     *
     * @param constructor 构造
     * @param p           参数
     * @return 对象
     */
    private Object invoke(Constructor<?> constructor, Object[] p) {
        constructor.setAccessible(true);
        try {
            return constructor.newInstance(p);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 无参构造
     *
     * @param <R> 类型
     * @return 实体
     */
    private <R> R createParameterisedConstruction() {
        try {
            return (R) type.newInstance();
        } catch (Exception ignored) {

        }
        for (ClassLoader loader : loaders) {
            try {
                return (R) Class.forName(type.getName(), true, loader).newInstance();
            } catch (Exception ignored) {
            }
        }
        List<Method> methods1 = new ArrayList<>();
        List<Method> methods2 = new ArrayList<>();
        methodStation.doLocalWith(method -> {
            if (Modifier.isPublic(method.getModifiers()) &&
                    Modifier.isStatic(method.getModifiers()) &&
                    type.isAssignableFrom(method.getReturnType())) {
                if (method.getParameterCount() == 0) {
                    methods1.add(method);
                } else {
                    methods2.add(method);
                }
            }
        });

        if (!methods1.isEmpty()) {
            Optional<Object> optional = methods1.stream().map(it -> MethodStation.invoke(null, it, new Object[0])).filter(Objects::nonNull).findFirst();
            if (optional.isPresent()) {
                return (R) optional.get();
            }
        }

        if (!methods2.isEmpty()) {
            for (Method method : methods2) {
                for (Object[] p : makeParams(method.getParameterTypes())) {
                    Object invoke = MethodStation.invoke(null, method, p);
                    if (null != invoke) {
                        return (R) invoke;
                    }
                }
            }
        }
        List<Field> field1 = new ArrayList<>();
        fieldStation.doLocalWith(field -> {
            if (type.isAssignableFrom(field.getType()) &&
                    Modifier.isStatic(field.getModifiers())
            ) {
                field1.add(field);
            }
        });

        for (Field field : field1) {
            field.setAccessible(true);
            try {
                return (R) field.get(null);
            } catch (IllegalAccessException ignored) {
            }
        }

        return null;
    }

    /**
     * 构建参数
     *
     * @param parameterTypes 参数类型
     * @return 参数
     */
    private List<Object[]> makeParams(Class<?>[] parameterTypes) {
        List<Object[]> result = new ArrayList<>();
        return result;
    }
}
