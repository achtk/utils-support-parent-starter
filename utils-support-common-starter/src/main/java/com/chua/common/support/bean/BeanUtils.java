package com.chua.common.support.bean;

import com.chua.common.support.collection.ConcurrentReferenceHashMap;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.unit.name.NamingCase;
import com.chua.common.support.utils.ArrayUtils;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.MapUtils;
import com.chua.common.support.utils.Preconditions;
import lombok.Data;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * bean tools
 *
 * @author CH
 */
public class BeanUtils {

    private static final Map<Class<?>, BeanIntrospection> CACHE = new ConcurrentReferenceHashMap<>(512);

    /**
     * 拷贝对象
     *
     * @param source           源对象
     * @param target           目标对象
     * @param realType         实际类型
     * @param ignoreProperties 忽略字段
     */
    @SuppressWarnings("ALL")
    public static <T> T copyProperties(Object source, Class<?> target, Class<T> realType, String... ignoreProperties) {
        if (void.class.isAssignableFrom(target)) {
            return null;
        }

        Object forObject = ClassUtils.forObject(target);
        if (null == source || null == forObject) {
            return null;
        }

        Class<?> aClass = forObject.getClass();

        if (List.class.isAssignableFrom(aClass)) {
            copyPropertiesBeanList(source, forObject, realType, ignoreProperties);
        } else if (Set.class.isAssignableFrom(aClass)) {
            copyPropertiesBeanSet(source, forObject, realType, ignoreProperties);

        } else if (aClass.isArray()) {
            return (T) copyPropertiesBeanArray(source, forObject, realType, ignoreProperties);
        } else {
            copyPropertiesBean(source, forObject, ignoreProperties);
        }

        return (T) forObject;
    }

    @SuppressWarnings("ALL")
    private static <T> T[] copyPropertiesBeanArray(Object source, Object forObject, Class<T> realType, String[] ignoreProperties) {
        List<Object> tpl = Arrays.asList(forObject);
        if (source instanceof Collection) {
            ((Collection<?>) source).forEach(it -> {
                T forObject1 = ClassUtils.forObject(realType);
                copyPropertiesBean(it, forObject1, ignoreProperties);
                tpl.add(forObject1);
            });
        } else {
            T forObject1 = ClassUtils.forObject(realType);
            copyPropertiesBean(source, forObject1, ignoreProperties);
            tpl.add(forObject1);
        }

        return tpl.toArray((T[]) Array.newInstance(realType, 0));
    }

    @SuppressWarnings("ALL")
    private static <T> void copyPropertiesBeanSet(Object source, Object forObject, Class<T> realType, String[] ignoreProperties) {
        Set<Object> tpl = (Set) forObject;
        if (source instanceof Collection) {
            ((Collection<?>) source).forEach(it -> {
                T forObject1 = ClassUtils.forObject(realType);
                copyPropertiesBean(it, forObject1, ignoreProperties);
                tpl.add(forObject1);
            });
        } else {
            T forObject1 = ClassUtils.forObject(realType);
            copyPropertiesBean(source, forObject1, ignoreProperties);
            tpl.add(forObject1);
        }
    }


    @SuppressWarnings("ALL")
    private static <T> void copyPropertiesBeanList(Object source, Object forObject, Class<T> realType, String[] ignoreProperties) {
        List<T> tpl = (List<T>) forObject;
        if (source instanceof Collection) {
            ((Collection<?>) source).forEach(it -> {
                T forObject1 = ClassUtils.forObject(realType);
                copyPropertiesBean(it, forObject1, ignoreProperties);
                tpl.add(forObject1);
            });
        } else {
            T forObject1 = ClassUtils.forObject(realType);
            copyPropertiesBean(source, forObject1, ignoreProperties);
            tpl.add(forObject1);
        }
    }

    /**
     * 拷贝对象
     *
     * @param source           源对象
     * @param target           目标对象
     * @param ignoreProperties 忽略字段
     */
    public static <T> T copyProperties(Object source, Class<T> target, String... ignoreProperties) {
        if (void.class.isAssignableFrom(target)) {
            return null;
        }

        T forObject = ClassUtils.forObject(target);
        if (null == source) {
            return forObject;
        }

        copyPropertiesBean(source, forObject, ignoreProperties);
        return forObject;
    }

    /**
     * 拷贝对象
     *
     * @param source           源对象
     * @param target           目标对象
     * @param ignoreProperties 忽略字段
     */
    public static void copyProperties(Object source, Object target, String... ignoreProperties) {
        copyPropertiesBean(source, target, ignoreProperties);
    }

    /**
     * 拷贝对象
     *
     * @param source           源对象
     * @param target           目标对象
     * @param ignoreProperties 忽略字段
     */
    private static void copyPropertiesBean(Object source, Object target, String[] ignoreProperties) {
        Preconditions.checkNotNull(source, "源对象不能为空");
        Preconditions.checkNotNull(target, "目标对象不能为空");
        Class<?> targetClass = target.getClass();

        if (target instanceof Map) {
            copyToMap(source, (Map) target, ignoreProperties);
            return;
        }

        AtomicInteger cnt = new AtomicInteger(0);
        ClassUtils.doWithFields(targetClass, field -> {
            if (ArrayUtils.contains(ignoreProperties, field.getName())) {
                return;
            }
            Object value = Converter.convertIfNecessary(getPropertyDescriptorValue(source, field, cnt.getAndIncrement()), field.getType());
            if (null == value) {
                return;
            }

            ClassUtils.setFieldValue(field, value, target);
        });
    }

    /**
     * @param source           源
     * @param target           目标
     * @param ignoreProperties 忽略字段
     */
    private static void copyToMap(Object source, Map target, String[] ignoreProperties) {
        BeanMap beanMap = BeanMap.of(source);
        beanMap.forEach((k, v) -> {
            if (ArrayUtils.contains(ignoreProperties, k)) {
                return;
            }
            if (null == v) {
                return;
            }

            target.put(k, v);
        });
    }

    /**
     * 设置字段值
     *
     * @param targetClass 类
     * @param target      目标类型
     * @param descriptor  描述
     * @param value       值
     */
    private static void setFieldValue(Class<?> targetClass, Object target, PropertyDescriptor descriptor, Object value) {
        Field declaredField = null;
        try {
            declaredField = targetClass.getDeclaredField(descriptor.getName());
        } catch (NoSuchFieldException ignored) {
        }

        if (null == declaredField) {
            return;
        }

        ClassUtils.setAccessible(declaredField);
        ClassUtils.setFieldValue(declaredField, value, target);

    }

    /**
     * 获取对象值
     *
     * @param source   源对象
     * @param targetPd 目标属性
     * @return 结果
     */
    private static Object getPropertyDescriptorValue(Object source, PropertyDescriptor targetPd) {
        String name = targetPd.getName();
        if (source instanceof Map) {
            Map source1 = (Map) source;
            String string = MapUtils.getString(source1, name);
            if (null != string) {
                return string;
            }

            string = MapUtils.getString(source1, name.toUpperCase());
            if (null != string) {
                return string;
            }
            string = MapUtils.getString(source1, NamingCase.toUnderlineCase(name));
            if (null != string) {
                return string;
            }

            string = MapUtils.getString(source1, NamingCase.toUnderlineCase(name).toUpperCase());
            if (null != string) {
                return string;
            }


            return null;

        }
        Class<?> aClass = source.getClass();
        PropertyDescriptor sourcePd = getPropertyDescriptor(aClass, name);
        if (sourcePd == null) {
            return getFieldValue(aClass, source, name);
        }

        Method readMethod = sourcePd.getReadMethod();
        if (readMethod == null) {
            return getFieldValue(aClass, source, name);
        }

        ClassUtils.setAccessible(readMethod);
        return ClassUtils.invokeMethod(readMethod, source);
    }

    /**
     * 获取对象值
     *
     * @param source 源对象
     * @param field  目标属性
     * @param index  索引
     * @return 结果
     */
    private static Object getPropertyDescriptorValue(Object source, Field field, int index) {
        String name = field.getName();
        if (source instanceof Object[]) {
            return index < ((Object[]) source).length ? ((Object[]) source)[index] : null;
        }
        if (source instanceof Map) {
            Map source1 = (Map) source;
            String string = MapUtils.getString(source1, name);
            if (null != string) {
                return string;
            }

            string = MapUtils.getString(source1, name.toUpperCase());
            if (null != string) {
                return string;
            }
            string = MapUtils.getString(source1,  NamingCase.toUnderlineCase(name));
            if (null != string) {
                return string;
            }

            string = MapUtils.getString(source1, NamingCase.toUnderlineCase(name).toUpperCase());
            if (null != string) {
                return string;
            }


            return null;

        }
        Class<?> aClass = source.getClass();
        PropertyDescriptor sourcePd = getPropertyDescriptor(aClass, name);
        if (sourcePd == null) {
            return getFieldValue(aClass, source, name);
        }

        Method readMethod = sourcePd.getReadMethod();
        if (readMethod == null) {
            return getFieldValue(aClass, source, name);
        }

        ClassUtils.setAccessible(readMethod);
        return ClassUtils.invokeMethod(readMethod, source);
    }

    /**
     * 获取字段值
     *
     * @param aClass 类
     * @param source 对象
     * @param name   名称
     * @return 结果
     */
    private static Object getFieldValue(Class<?> aClass, Object source, String name) {
        Field declaredField = null;
        try {
            declaredField = aClass.getDeclaredField(name);
        } catch (NoSuchFieldException ignored) {
        }
        return getFieldDescriptorValue(declaredField, source);
    }

    /**
     * 获取对象值
     *
     * @param source        源对象
     * @param declaredField 字段
     * @return 结果
     */
    private static Object getFieldDescriptorValue(Field declaredField, Object source) {
        if (null == declaredField) {
            return null;
        }
        ClassUtils.setAccessible(declaredField);
        return ClassUtils.getFieldValue(declaredField, source);
    }

    /**
     * 获取属性描述
     *
     * @param aClass 类
     * @param name   属性名称
     * @return 属性
     */
    private static PropertyDescriptor getPropertyDescriptor(Class<?> aClass, String name) {
        return CACHE.computeIfAbsent(aClass, it -> {
            BeanIntrospection beanIntrospection = new BeanIntrospection();
            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(it);
                beanIntrospection.setDescriptors(beanInfo.getPropertyDescriptors());
            } catch (IntrospectionException ignored) {
            }
            return beanIntrospection;
        }).getPropertyDescriptor(name);
    }

    /**
     * 获取树形描述
     *
     * @param target 目标类型
     * @return 结果
     */
    private static PropertyDescriptor[] getPropertyDescriptors(Class<?> target) {
        return CACHE.computeIfAbsent(target, it -> {
            BeanIntrospection beanIntrospection = new BeanIntrospection();
            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(it);
                beanIntrospection.setDescriptors(beanInfo.getPropertyDescriptors());
            } catch (IntrospectionException ignored) {
            }
            return beanIntrospection;
        }).getDescriptors();
    }

    /**
     * bean
     *
     * @param data   data
     * @param target 类型
     * @return 结果
     */
    public static <E> Set<E> copyPropertiesSet(Collection<?> data, Class<E> target) {
        return data.stream().map(it -> BeanUtils.copyProperties(it, target)).collect(Collectors.toSet());
    }

    /**
     * bean
     *
     * @param data   data
     * @param target 类型
     * @return 结果
     */
    public static <E> List<E> copyPropertiesList(Collection<?> data, Class<E> target) {
        return data.stream().map(it -> {
            return BeanUtils.copyProperties(it, target);
        }).collect(Collectors.toList());
    }


    @Data
    static final class BeanIntrospection {
        /**
         * 属性
         */
        PropertyDescriptor[] descriptors;

        private Map<String, PropertyDescriptor> descriptorMap;

        public void setDescriptors(PropertyDescriptor[] descriptors) {
            this.descriptors = descriptors;
            this.descriptorMap = new HashMap<>(descriptors.length);
            for (PropertyDescriptor descriptor : descriptors) {
                descriptorMap.put(descriptor.getName(), descriptor);
            }
        }

        /**
         * 获取属性
         *
         * @param name 名称
         * @return 属性
         */
        public PropertyDescriptor getPropertyDescriptor(String name) {
            return descriptorMap.get(name);
        }
    }

}
