package com.chua.common.support.reflection;

import com.chua.common.support.constant.CommonConstant;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.reflection.craft.MethodCraftTable;
import com.chua.common.support.unit.name.NamingCase;
import com.chua.common.support.utils.ArrayUtils;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.CollectionUtils;
import lombok.Setter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 方法集合
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/3/30
 */
public class MethodStation extends MethodCraftTable {

    public static final MethodStation INSTANCE = new MethodStation();
    protected static final String GETTER_PREFIX = CommonConstant.METHOD_GETTER;
    protected static final String SETTER_PREFIX = CommonConstant.METHOD_SETTER;
    private static final Map<Class<?>, MethodStation> CACHE = new HashMap<>();
    private static final Class<?>[] EMPTY_CLASS = new Class<?>[0];
    private static final String METHOD_HASH_CODE = "hashCode";
    private static final String METHOD_EQUALS = "equals";
    private static final String METHOD_TO_STRING = "toString";
    @Setter
    private Object entity;
    private Class<?> type;
    private Map<String, List<Method>> methodMap;
    private Map<String, List<Method>> methodAllMap;

    /**
     * 初始化
     *
     * @param entity 实体
     */
    public MethodStation(Object entity) {
        super(ClassUtils.toType(entity));
        this.entity = entity;
        this.type = ClassUtils.toType(entity);
        this.getMethods();
        this.getLocalMethods();
    }

    /**
     * 遍历字段
     *
     * @param consumer 消费者
     */
    public void doLocalWith(Consumer<Method> consumer) {
        if (null == consumer) {
            return;
        }

        if (null == methodMap) {
            methodMap = new LinkedHashMap<>();
            try {
                for (Method method : type.getDeclaredMethods()) {
                    methodMap.computeIfAbsent(method.getName(), it -> new ArrayList<>()).add(method);
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }

        for (Map.Entry<String, List<Method>> entry : methodMap.entrySet()) {
            entry.getValue().forEach(consumer);
        }


    }

    /**
     * 遍历字段
     *
     * @param consumer 消费者
     */

    public void doWith(Consumer<Method> consumer) {
        if (null == methodAllMap) {
            methodAllMap = new LinkedHashMap<>();
            Class<?> targetClass = type;
            do {
                Method[] methods = targetClass.getDeclaredMethods();
                for (Method method : methods) {
                    methodAllMap.computeIfAbsent(method.getName(), it -> new ArrayList<>()).add(method);
                }
                targetClass = targetClass.getSuperclass();
            }
            while (targetClass != null && targetClass != Object.class);
        }

        for (Map.Entry<String, List<Method>> entry : methodAllMap.entrySet()) {
            entry.getValue().forEach(consumer);
        }
    }

    /**
     * 过滤
     *
     * @param predicate 条件
     * @return 方法
     */
    public List<Method> filter(Predicate<Method> predicate) {
        List<Method> result = new ArrayList<>();
        doWith(method -> {
            if (predicate.test(method)) {
                result.add(method);
            }
        });
        return result;
    }

    /**
     * 过滤
     *
     * @param predicate 条件
     * @return 方法
     */
    public List<Method> filterLocal(Predicate<Method> predicate) {
        List<Method> result = new ArrayList<>();
        doLocalWith(method -> {
            if (predicate.test(method)) {
                result.add(method);
            }
        });
        return result;
    }

    /**
     * 获取字段的get方法
     *
     * @param fieldName 字段名
     * @return getter方法
     */
    public Method getGetterMethod(String fieldName) {
        return getMethods(GETTER_PREFIX + NamingCase.toFirstUpperCase(fieldName), EMPTY_CLASS);
    }

    /**
     * 获取所有方法
     *
     * @return 所有方法
     */
    public List<Method> getLocalMethods() {
        List<Method> result = new ArrayList<>();
        doLocalWith(result::add);
        return result;
    }

    /**
     * 获取指定方法
     *
     * @param methodName 方法名(支持通配符)
     * @return 方法
     */
    public List<Method> getLocalMethods(String methodName) {
        List<Method> result = new ArrayList<>();
        doLocalWith(method -> {
            if (methodName.equals(method.getName())) {
                result.add(method);
            }
        });
        return result;
    }

    /**
     * 获取所有方法
     *
     * @return 所有方法
     */
    public List<Method> getMethods() {
        List<Method> result = new ArrayList<>();
        doWith(result::add);
        return result;
    }

    /**
     * 根据类型获取方法
     *
     * @return 类型获取方法
     */
    public List<Method> getMethods(Class<?>... paramTypes) {
        return getMethods(item -> true, paramTypes);
    }

    /**
     * 根据类型获取方法
     *
     * @return 类型获取方法
     */
    public List<Method> getMethods(Predicate<Method> predicate, Class<?>... paramTypes) {
        int length = paramTypes.length;
        List<Method> result = new ArrayList<>();
        doWith(method -> {
            int parameterCount = method.getParameterCount();
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterCount != length) {
                return;
            }
            if (ArrayUtils.isEquals(parameterTypes, paramTypes) && predicate.test(method)) {
                result.add(method);
            }
        });
        return result;
    }

    /**
     * 获取指定方法
     *
     * @param methodName 方法名(支持通配符)
     * @return 方法
     */
    public List<Method> getMethods(String methodName) {
        List<Method> result = new ArrayList<>();
        doWith(method -> {
            if (methodName.equals(method.getName())) {
                result.add(method);
            }
        });
        return result;
    }

    /**
     * 获取指定方法
     *
     * @param methodName 方法名(支持通配符)
     * @return 方法
     */
    public Method getMethod(String methodName) {
        List<Method> result = new ArrayList<>();
        doWith(method -> {
            if (methodName.equals(method.getName())) {
                result.add(method);
            }
        });
        return result.isEmpty() ? null : result.get(0);
    }

    /**
     * 获取方法
     *
     * @param methodName     方法名
     * @param parameterTypes 参数类型
     * @return 存在返回方法, 反之返回null
     */
    public Method getMethods(String methodName, Class<?>... parameterTypes) {
        List<Method> methods = new ArrayList<>();
        doWith(method -> {
            if (!method.getName().equals(methodName)) {
                return;
            }
            Class<?>[] parameterTypes1 = method.getParameterTypes();
            if (ArrayUtils.isEquals(parameterTypes1, parameterTypes)) {
                methods.add(method);
            }
        });
        return CollectionUtils.findFirst(methods);
    }

    /**
     * 获取字段的set方法
     *
     * @param fieldName 字段名
     * @return setter方法
     */
    public Method getSetterMethod(String fieldName) {
        return getMethods(SETTER_PREFIX + NamingCase.toFirstUpperCase(fieldName), EMPTY_CLASS);
    }

    /**
     * 获取结果
     *
     * @param methodName 方法名
     * @param parameters 参数
     * @return 结果
     */
    public Object getValue(String methodName, Object... parameters) {
        Class<?>[] classes = ClassUtils.toType(parameters);
        Method method = getMethods(methodName, classes);
        if (null != method) {
            method.setAccessible(true);
            try {
                return method.invoke(entity, parameters == null ? new Object[]{null} : parameters);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    /**
     * 执行方法
     *
     * @param method 方法
     * @return 结果
     */
    public Object invoke(Method method) {
        if (null == method) {
            return null;
        }
        try {
            method.setAccessible(true);
            return method.invoke(entity);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 执行方法
     *
     * @param methodName 方法名
     * @param args       参数
     * @return 结果
     */
    public Object invoke(Method methodName, Object... args) {
        return invoke(methodName.getName(), args);
    }

    /**
     * 执行方法
     *
     * @param methodName 方法名
     * @param args       参数
     * @return 结果
     */
    public Object invoke(String methodName, Object... args) {
        Method method = getMethod(methodName);
        if (null != method) {
            method.setAccessible(true);
            int parameterCount = method.getParameterCount();
            try {
                if (parameterCount == 0) {
                    try {
                        return method.invoke(entity);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return method.invoke(entity, ClassUtils.createArgs(args, method.getParameterTypes()));
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    /**
     * 执行方法
     *
     * @param methodName 方法名
     * @param args       参数
     * @return 结果
     */
    public Object invokeStatic(String methodName, Object... args) {
        Method method = getMethod(methodName);
        if (null != method) {
            method.setAccessible(true);
            try {
                return method.invoke(null, args);
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    /**
     * 是否是基础方法
     *
     * @param method 方法
     * @return 是否是基础方法
     */
    public boolean isBaseMethod(Method method) {
        if (null == method) {
            return false;
        }
        return MethodStation.isEquals(method) ||
                MethodStation.isHashCode(method) ||
                MethodStation.isToString(method);
    }

    /**
     * 获取方法
     *
     * @param predicate 条件
     * @return 方法
     */
    public List<Method> listLocalMethods(Predicate<Method> predicate) {
        return getLocalMethods().stream().filter(predicate).collect(Collectors.toList());
    }

    /**
     * 获取方法
     *
     * @param predicate 条件
     * @return 方法
     */
    public List<Method> listMethods(Predicate<Method> predicate) {
        return getMethods().stream().filter(predicate).collect(Collectors.toList());
    }

    /**
     * 获取结果
     *
     * @param methodName 方法名
     * @param parameters 参数
     * @return 结果
     */
    public Object process(String methodName, Object... parameters) {
        Class<?>[] classes = ClassUtils.toType(parameters);
        Method method = getMethods(methodName, classes);
        if (null != method) {
            method.setAccessible(true);
            try {
                return method.invoke(entity, parameters == null ? new Object[]{null} : parameters);
            } catch (Exception e) {
                throw new IllegalStateException(e.getMessage());
            }
        }
        throw new IllegalStateException("");
    }

    /**
     * 执行方法
     *
     * @param entity 实体
     * @param method 方法
     * @param params 参数
     */
    public static Object invoke(Object entity, Method method, Object... params) {
        if (null == method) {
            return null;
        }

        try {
            return ClassUtils.invokeMethod(method, entity, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 初始化
     *
     * @param entity 实体
     * @return FieldStation
     */
    public static MethodStation of(Object entity) {
        Class<?> aClass = ClassUtils.toType(entity);
        MethodStation methodStation = CACHE.get(aClass);
        if (!CACHE.containsKey(aClass)) {
            methodStation = new MethodStation(entity);
            CACHE.put(aClass, methodStation);
        }
        methodStation.setEntity(entity);
        return methodStation;
    }

    /**
     * 是toString方法
     *
     * @param method 方法
     * @return 是toString方法返回true
     */
    public static boolean isToString(Method method) {
        return METHOD_TO_STRING.equalsIgnoreCase(method.getName());
    }

    /**
     * 是equals方法
     *
     * @param method 方法
     * @return 是equals方法返回true
     */
    public static boolean isEquals(Method method) {
        return METHOD_EQUALS.equalsIgnoreCase(method.getName());
    }

    /**
     * 是equals方法
     *
     * @param method 方法
     * @return 是equals方法返回true
     */
    public static boolean isHashCode(Method method) {
        return METHOD_HASH_CODE.equalsIgnoreCase(method.getName());
    }

    /**
     * 通过注解查询方法
     *
     * @param annotationType 注解
     * @return 满足条件的方法
     */
    public List<Method> getMethodByAnnotation(Class<? extends Annotation> annotationType) {
        if (null == annotationType) {
            return Collections.emptyList();
        }
        return filterLocal(method -> {
            return method.isAnnotationPresent(annotationType);
        });
    }

    /**
     * 通过注解和参数获取方法
     *
     * @param annotationType 注解
     * @param params         参数
     * @return 方法
     */
    public List<Method> getMethodByAnnotation(Class<? extends Annotation> annotationType, Class<?>... params) {
        if (null == annotationType) {
            return Collections.emptyList();
        }
        return filterLocal(method -> {
            return method.isAnnotationPresent(annotationType) && ArrayUtils.isEquals(method.getParameterTypes(), params);
        });
    }
}
