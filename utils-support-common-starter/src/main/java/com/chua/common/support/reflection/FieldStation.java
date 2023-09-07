package com.chua.common.support.reflection;

import com.chua.common.support.collection.TypeHashMap;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.unit.name.NamingCase;
import com.chua.common.support.utils.AnnotationUtils;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.CollectionUtils;
import lombok.Setter;

import java.beans.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;



/**
 * 字段集合
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/3/30
 */
public final class FieldStation  {

    public static final FieldStation INSTANCE = new FieldStation(null);
    private static final Map<Class<?>, FieldStation> CACHE = new HashMap<>();
    private static final Annotation[] TEMP = new Annotation[0];
    private static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;
    private static final AnnotationType ANNOTATION_TYPE = new AnnotationType();

    @Setter
    private Object entity;
    private Class<?> type;
    private List<PropertyDescriptor> propertyDescriptors;
    private Map<String, Field> fieldMap;
    private Map<String, Field> fieldAllMap;

    /**
     * 初始化
     *
     * @param entity 实体
     */
    public FieldStation(Object entity) {
        this.entity = entity;
        this.type = ClassUtils.toType(entity);
        this.getFields();
        this.getLocalFields();
    }

    /**
     * 是否全部包含字段
     *
     * @param fields 字段
     * @return 全部包含返回true
     */
    public boolean contains(List<String> fields) {
        for (Field field : getFields()) {
            if (!fields.contains(field.getName())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 遍历字段
     *
     * @param consumer 消费者
     */
    public void doLocalWith(Consumer<Field> consumer) {
        if (null == consumer) {
            return;
        }

        if (null == fieldMap) {
            fieldMap = new LinkedHashMap<>();
            doSyncLocalWith(fieldMap);
        }

        for (Map.Entry<String, Field> entry : fieldMap.entrySet()) {
            consumer.accept(entry.getValue());
        }
    }

    /**
     * 遍历字段
     *
     * @param fieldMap 消费者
     */
    private void doSyncLocalWith(Map<String, Field> fieldMap) {
        if (null == fieldMap) {
            return;
        }

        try {
            for (Field field : type.getDeclaredFields()) {
                fieldMap.put(field.getName(), field);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * 遍历字段
     *
     * @param biConsumer 消费者
     */

    public void doWithPropertyDescriptor(BiConsumer<PropertyDescriptor, Field> biConsumer) {
        if (null == propertyDescriptors) {
            BeanInfo beanInfo = null;
            try {
                beanInfo = Introspector.getBeanInfo(type);
                propertyDescriptors = Arrays.asList(beanInfo.getPropertyDescriptors());
                propertyDescriptors.sort(Comparator.comparing(FeatureDescriptor::getName));
            } catch (IntrospectionException ignored) {
            }
        }

        if (null != propertyDescriptors) {
            for (PropertyDescriptor descriptor : propertyDescriptors) {
                Field field = getField(descriptor.getName());
                if (null == field) {
                    continue;
                }
                biConsumer.accept(descriptor, field);
            }
        }
    }

    /**
     * 遍历字段
     *
     * @param consumer 消费者
     */

    public synchronized void doWith(Consumer<Field> consumer) {
        if (null == fieldAllMap) {
            fieldAllMap = new LinkedHashMap<>();
            doSyncWith(fieldAllMap);
        }
        for (Map.Entry<String, Field> entry : fieldAllMap.entrySet()) {
            consumer.accept(entry.getValue());
        }
    }

    /**
     * 遍历字段
     *
     * @param fieldMap 消费者
     */

    private void doSyncWith(Map<String, Field> fieldMap) {
        Class<?> targetClass = type;
        do {
            Field[] fields = targetClass.getDeclaredFields();
            for (Field field : fields) {
                fieldMap.put(field.getName(), field);
            }
            targetClass = targetClass.getSuperclass();
        }
        while (targetClass != null && targetClass != Object.class);
    }

    /**
     * 获取不同的字段
     *
     * @param fields 字段
     * @return 不同的字段
     */
    public List<String> getDifferentFields(List<String> fields) {
        List<String> result = new ArrayList<>();
        List<String> collect = getFields().stream().map(Field::getName).collect(Collectors.toList());
        for (String field : fields) {
            if (!collect.contains(field)) {
                result.add(field);
            }
        }
        return result;
    }

    /**
     * 获取字段
     *
     * @param fieldName 字段名称
     * @return 字段
     */
    public Field getField(String fieldName) {
        return getField(fieldName, Object.class);
    }

    /**
     * 获取字段
     *
     * @param fieldName 字段名称
     * @param fieldType 字段类型
     * @return 字段
     */
    public Field getField(String fieldName, Class<?> fieldType) {
        Field field = null;
        if (fieldAllMap.containsKey(fieldName)) {
            field = fieldAllMap.get(fieldName);
        }
        if (null == field) {
            field = fieldAllMap.get(NamingCase.toFirstLowerCase(fieldName));
        }

        return field;
    }

    /**
     * 获取字段所有注解
     *
     * @param name 字段名称
     * @return 注解
     */
    public Annotation[] getFieldAnnotations(String name) {
        Field field = getField(name);
        if (null == field) {
            return new Annotation[0];
        }

        return field.getDeclaredAnnotations();
    }

    /**
     * 获取字段所有注解
     *
     * @param name 字段名称
     * @return 注解
     */
    public AnnotationType getFieldAnnotations(String name, String type) {
        return getFieldAnnotations(name, ClassUtils.forName(type));
    }

    /**
     * 获取字段所有注解
     *
     * @param name 字段名称
     * @return 注解
     */
    public AnnotationType getFieldAnnotations(String name, Class<?> type) {
        if (null == type) {
            return ANNOTATION_TYPE;
        }
        Field field = getField(name);
        if (null == field) {
            return ANNOTATION_TYPE;
        }

        List<Annotation> result = new ArrayList<>();
        Annotation[] annotations = field.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            if (type.isAssignableFrom(annotation.annotationType())) {
                result.add(annotation);
            }
        }

        return result.isEmpty() ? ANNOTATION_TYPE : new AnnotationType(result.get(0));
    }

    /**
     * 获取所有字段
     *
     * @return 所有字段
     */
    public List<Field> getFields() {
        List<Field> result = new ArrayList<>();
        doWith(result::add);
        return result;
    }

    /**
     * 获取字段
     *
     * @param fieldName 字段名称
     * @return 字段
     */
    public Field getLocalField(String fieldName) {
        return fieldMap.getOrDefault(fieldName, fieldMap.get(NamingCase.toFirstLowerCase(fieldName)));
    }

    /**
     * 获取所有字段
     *
     * @return 所有字段
     */
    public List<Field> getLocalFields() {
        List<Field> result = new ArrayList<>();
        doLocalWith(result::add);
        return result;
    }

    /**
     * 获取静态字段
     *
     * @return 静态字段
     */
    public List<Field> getLocalStaticFields() {
        return listLocalFields(field -> Modifier.isStatic(field.getModifiers()));
    }

    /**
     * 获取对象
     *
     * @return 获取对象
     */
    public Object getObject() {
        return entity;
    }

    /**
     * 获取静态字段
     *
     * @return 静态字段
     */
    public List<Field> getStaticFields() {
        return listFields(field -> Modifier.isStatic(field.getModifiers()));
    }

    /**
     * 获取字段的值
     *
     * @param field 字段
     * @return 值
     */
    public Object getValue(Field field) {
        if (null == field) {
            return null;
        }
        field.setAccessible(true);
        try {
            return field.get(entity);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    /**
     * 获取字段的值
     *
     * @param index 索引
     * @return 值
     */
    public Object getValue(int index) {
        Field field = CollectionUtils.find(fieldAllMap.values(), index);
        field.setAccessible(true);
        try {
            return field.get(entity);
        } catch (IllegalAccessException ignore) {
        }
        return null;
    }

    /**
     * 获取字段的值
     *
     * @param fieldName 字段名
     * @return 值
     */
    public Object getValue(String fieldName) {
        if (entity instanceof Map) {
            return ((Map<?, ?>) entity).get(fieldName);
        }

        if (entity instanceof Dictionary) {
            return ((Dictionary) entity).get(fieldName);
        }

        Field field = getField(fieldName);
        if (null != field) {
            field.setAccessible(true);
            try {
                return Converter.convertIfNecessary(field.get(entity), field.getType());
            } catch (IllegalAccessException ignore) {
            }
        }
        return null;
    }

    /**
     * 获取字段的值
     *
     * @param fieldName 字段名
     * @param type      类型
     * @param <E>       类型
     * @return 值
     */
    public <E> E getValue(String fieldName, Class<E> type) {
        Object value = getValue(fieldName);
        if (null == value) {
            return null;
        }
        return Converter.convertIfNecessary(value, type);
    }

    /**
     * 获取字段的值
     *
     * @param fieldName 字段名
     * @param type      类型
     * @return 值
     */
    public Object getFieldValue(String fieldName, Object type) {
        Field value = getField(fieldName);
        if (null == value) {
            return null;
        }
        value.setAccessible(true);
        try {
            return value.get(type);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取字段的值
     *
     * @param fieldName 字段名
     * @return 值
     */
    public Object getFieldValue(String fieldName) {
        return getFieldValue(fieldName, entity);
    }

    /**
     * 获取字段的值
     *
     * @param field 字段名
     * @return 值
     */
    public Object getFieldValue(Field field) {
        return getFieldValue(field, Object.class);
    }

    /**
     * 获取字段的值
     *
     * @param field 字段名
     * @param type  类型
     * @param <T>   元素类型
     * @return 值
     */
    public <T> T getFieldValue(Field field, Class<T> type) {
        try {
            return Converter.convertIfNecessary(field.get(entity), type);
        } catch (Exception e) {
            field.setAccessible(true);
            try {
                return Converter.convertIfNecessary(field.get(entity), type);
            } catch (IllegalAccessException illegalAccessException) {
                return null;
            }
        }
    }

    /**
     * 获取字段
     *
     * @param predicate 条件
     * @return 字段
     */
    public List<Field> listFields(Predicate<Field> predicate) {
        return getLocalFields().stream().filter(predicate).collect(Collectors.toList());
    }

    /**
     * 获取本地字段
     *
     * @param predicate 条件
     * @return 字段
     */
    public List<Field> listLocalFields(Predicate<Field> predicate) {
        return getLocalFields().stream().filter(predicate).collect(Collectors.toList());
    }

    /**
     * 字段名称
     *
     * @return 字段名称
     */
    public List<String> names() {
        return new ArrayList<>(fieldAllMap.keySet());
    }

    /**
     * 设置字段的值
     *
     * @param fieldName  字段名
     * @param fieldValue 字段值
     */
    public void setValue(String fieldName, Object fieldValue) {
        setValue(fieldName, fieldValue, false);
    }

    /**
     * 设置字段的值
     *
     * @param fieldName  字段名
     * @param fieldValue 字段值
     * @param isStatic   是否是静态
     */
    public void setValue(String fieldName, Object fieldValue, boolean isStatic) {
        ClassUtils.setFieldValue(fieldName, fieldValue, isStatic ? null : getObject());
    }

    /**
     * 字段数量
     *
     * @return 字段数量
     */
    public int size() {
        return fieldMap.size();
    }

    /**
     * 获取字段以及值
     *
     * @return 字段: 值
     */
    public Map<String, Object> valueMap() {
        Map<String, Object> result = new HashMap<>(DEFAULT_INITIAL_CAPACITY);
        doWith(field -> {
            String name = field.getName();
            Object value = getValue(name);
            result.put(field.getName(), value);
        });
        return result;
    }

    /**
     * 获取字段以及值
     *
     * @return 字段: 值
     */
    public Map<String, Object> valueNoneMap() {
        Map<String, Object> result = new HashMap<>(DEFAULT_INITIAL_CAPACITY);
        doWithPropertyDescriptor(((propertyDescriptor, field) -> {
            if (Modifier.isStatic(field.getModifiers())) {
                return;
            }
            Object value1 = null;
            Method readMethod = propertyDescriptor.getReadMethod();
            if (null != readMethod) {
                value1 = MethodStation.invoke(entity, readMethod);
            }
            if (null == value1) {
                value1 = FieldStation.invoke(entity, field);
            }
            if (null != value1) {
                result.put(field.getName(), value1);
            }
        }));
        return result;
    }

    /**
     * 获取字段
     *
     * @param entity 实体
     * @param field  方法
     * @return 字段值
     */
    public static Object invoke(Object entity, Field field) {
        if (null == entity || null == field) {
            return null;
        }
        try {
            field.setAccessible(true);
            return field.get(entity);
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * 获取字段(方法 -> 字段)
     *
     * @param entity 实体
     * @param field  方法
     * @return 字段值
     */
    public static Object invokes(Object entity, Field field) {
        return FieldStation.of(entity).getValue(field.getName(), field.getType());
    }

    /**
     * 初始化
     *
     * @param entity 实体
     * @return FieldStation
     */
    public static FieldStation of(Object entity) {
        Class<?> aClass = ClassUtils.toType(entity);
        FieldStation fieldStation = CACHE.get(aClass);
        if (!CACHE.containsKey(aClass)) {
            fieldStation = new FieldStation(entity);
            CACHE.put(aClass, fieldStation);
        }
        fieldStation.setEntity(entity);
        return fieldStation;
    }

    /**
     * 通过注解获取字段
     *
     * @param annotationType 注解类
     * @return 字段
     */
    List<Field> getLocalFieldByAnnotation(Class<? extends Annotation> annotationType) {
        if (null == annotationType) {
            return Collections.emptyList();
        }
        List<Field> result = new ArrayList<>();
        doLocalWith(field -> {
            if (field.isAnnotationPresent(annotationType)) {
                result.add(field);
            }
        });
        return result;
    }

    /**
     * 通过注解获取字段
     *
     * @param annotationType 注解类
     * @return 字段
     */
    List<Field> getFieldByAnnotation(Class<? extends Annotation> annotationType) {
        if (null == annotationType) {
            return Collections.emptyList();
        }
        List<Field> result = new ArrayList<>();
        doWith(field -> {
            if (field.isAnnotationPresent(annotationType)) {
                result.add(field);
            }
        });
        return result;
    }


    /**
     * 注解类型
     *
     * @author CH
     * @since 2021-10-19
     */
    public static class AnnotationType extends TypeHashMap {

        private Annotation annotation;
        private Class<?> type;
        private Map<String, Object> attribute;

        public AnnotationType() {
        }

        public AnnotationType(Annotation annotation) {
            this.annotation = annotation;
            this.type = annotation.annotationType();
            this.attribute = AnnotationUtils.getAnnotationAttributes(annotation);
        }

        @Override
        public Object getObject(String key) {
            return attribute.get(key);
        }

    }
}
