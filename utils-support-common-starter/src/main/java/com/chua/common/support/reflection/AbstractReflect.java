package com.chua.common.support.reflection;

import com.chua.common.support.collection.ConcurrentReferenceTable;
import com.chua.common.support.collection.Table;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.lang.proxy.DelegateMethodIntercept;
import com.chua.common.support.lang.proxy.ProxyUtils;
import com.chua.common.support.spi.ServiceDefinition;
import com.chua.common.support.spi.finder.SamePackageServiceFinder;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.value.DelegateValue;
import com.chua.common.support.value.Value;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;

/**
 * 反射
 *
 * @author CH
 */
@SuppressWarnings("ALL")
public class AbstractReflect<T> extends DelegateMethodIntercept<T> {

    private final T entity;
    private final Class<T> type;
    private final Table<String, Class<?>[], Method> methodTable;
    private final Map<String, Field> fieldMap;


    protected AbstractReflect(Class<T> type) {
        this(null, type);
    }

    protected AbstractReflect(T entity, Class<T> type) {
        super(type, proxyMethod -> null);
        this.entity = entity;
        this.type = type;
        this.fieldMap = new HashMap<>(1 << 4);

        ClassUtils.doWithFields(type, field -> {
            field.setAccessible(true);
            fieldMap.put(field.getName(), field);
        });

        this.methodTable = new ConcurrentReferenceTable<>();
        ClassUtils.doWithMethods(type, method -> {
            method.setAccessible(true);
            methodTable.put(method.getName(), method.getParameterTypes(), method);
        });
    }

    /**
     * object value
     *
     * @param params 构造参数
     * @return value
     */
    @SuppressWarnings("ALL")
    public Value<T> getObjectValue(Object... params) {
        if (null == type) {
            return new DelegateValue<>(null);
        }

        if (List.class.isAssignableFrom(type)) {
            return new DelegateValue<>((T) Collections.emptyList());
        }

        if (Set.class.isAssignableFrom(type)) {
            return new DelegateValue<>((T) Collections.emptySet());
        }

        if (Map.class.isAssignableFrom(type)) {
            return new DelegateValue<>((T) Collections.emptyMap());
        }

        try {
            return new DelegateValue<>((T) ClassUtils.forObject(type, params));
        } catch (Throwable e) {
            e.printStackTrace();
            return new DelegateValue<>(createProxy(), e);
        }
    }

    /**
     * 创建代理
     *
     * @return 代理
     */
    private T createProxy() {
        return ProxyUtils.proxy(type, ClassLoader.getSystemClassLoader(), this);
    }

    /**
     * 获取字段
     *
     * @param name 名称
     * @return 字段
     */
    public Field getField(String name) {
        return fieldMap.get(name);
    }

    /**
     * 获取值
     *
     * @param name 名称
     * @return 字段
     */
    public <T> T getFieldValue(String name, Class<T> returnType) {
        return Converter.convertIfNecessary(getFieldValue(entity, name), returnType);
    }

    /**
     * 获取值
     *
     * @param name 名称
     * @return 字段
     */
    public Object getFieldValue(String name) {
        return getFieldValue(entity, name);
    }

    /**
     * 获取值
     *
     * @param obj  实体
     * @param name 名称
     * @return 字段
     */
    public Object getFieldValue(Object obj, String name) {
        return ClassUtils.getFieldValue(name, type, obj);
    }

    /**
     * 设置字段
     *
     * @param name  名称
     * @param value 值
     * @return 字段
     */
    public void setValue(String name, Object value) {
        Field field = getField(name);
        try {
            field.set(entity, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 执行方法
     *
     * @param methodName
     * @param value
     */
    public Function<Method, Object> withMethod(String methodName, Class<?>[] value) {
        Method method = methodTable.get(methodName, value);
        return new Function<Method, Object>() {
            @Override
            public Object apply(Method method) {
                return null;
            }
        };
    }

    /**
     * 执行方法
     *
     * @param methodName
     * @param value
     */
    public void setMethod(String methodName, Object value) {
        if (null == value) {
            return;
        }

        Method method = methodTable.get(methodName, new Class<?>[]{value.getClass()});
        if (null == method) {
            return;
        }

        try {
            method.invoke(entity, value);
        } catch (Exception ignore) {
        }
    }
}
