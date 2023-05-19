package com.chua.common.support.eventbus;

import com.chua.common.support.collection.AnnotationAttributes;
import com.chua.common.support.reflection.describe.AnnotationDescribe;
import com.chua.common.support.reflection.describe.TypeDescribe;
import com.chua.common.support.reflection.dynamic.AnnotationFactory;
import com.chua.common.support.reflection.dynamic.DynamicFactory;
import com.chua.common.support.reflection.dynamic.NonStandardDynamicFactory;
import com.chua.common.support.reflection.dynamic.attribute.AnnotationAttribute;
import com.chua.common.support.utils.AnnotationUtils;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 消息总线事件
 *
 * @author CH
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class EventbusEvent {
    private static final String[] EMPTY = new String[0];
    /**
     * 名称
     */
    private String name;
    /**
     * 类型
     */
    private EventbusType type;
    /**
     * bean名称
     */
    private String beanName;
    /**
     * bean
     */
    private Object bean;
    /**
     * 方法
     */
    private Method method;
    /**
     * BeanFactory
     */
    private Object beanFactory;
    /**
     * 配置
     */
    private Object configuration;
    /**
     * 参数类型
     */
    private Class<?> paramType;

    public EventbusEvent(Subscribe subscribe, Method method, String beanName, Object bean) {
        Map<String, Object> attributes = AnnotationUtils.getAnnotationValue(subscribe);
        setBean(bean);
        setParamType(method.getParameterTypes()[0]);
        setMethod(method);
        setBeanName(beanName);

        setName(StringUtils.isNullOrEmpty(subscribe.name()) ? subscribe.value() : subscribe.name());
        setType(EventbusType.valueOf(String.valueOf(attributes.get("type"))));
        setConfiguration(configuration);
        EventbusType type = subscribe.type();
        if (type == EventbusType.GUAVA && ClassUtils.isPresent("om.google.common.eventbus.Subscribe")) {
            setBean(analysisGuavaSubscribe(bean));
        }
    }

    /**
     * guava订阅者
     *
     * @param o 实体
     * @return 订阅实体
     */
    private Object analysisGuavaSubscribe(Object o) {
        Class<?> aClass = o.getClass();
        Method[] declaredMethods = aClass.getDeclaredMethods();
        boolean needRepair = false;
        for (Method declaredMethod : declaredMethods) {
            Subscribe declaredAnnotation = declaredMethod.getDeclaredAnnotation(Subscribe.class);
            if (null != declaredAnnotation) {
                TypeDescribe typeDescribe = TypeDescribe.create(o);
                if (typeDescribe.hasAnnotation("com.google.common.eventbus.Subscribe")) {
                    needRepair = true;
                    break;
                }
            }
        }

        if (!needRepair) {
            return o;
        }

        return repair(o, aClass, declaredMethods);
    }

    /**
     * 修复实体
     *
     * @param o               实体
     * @param aClass          类
     * @param declaredMethods 方法
     * @return 实体
     */
    private Object repair(Object o, Class<?> aClass, Method[] declaredMethods) {
        DynamicFactory dynamicFactory = new NonStandardDynamicFactory();
        dynamicFactory.interfaces(Arrays.stream(aClass.getInterfaces()).map(Class::getTypeName).toArray(value -> EMPTY));
        dynamicFactory.superType(aClass.getSuperclass().getTypeName());
        ClassUtils.doWithLocalFields(aClass, field -> {
            dynamicFactory.field(field.getName(), field.getType().getTypeName());
        });

        dynamicFactory.field("source", o.getClass().getTypeName(), o);

        List<String> needGuava = new LinkedList<>();
        needGuava.add(Subscribe.class.getTypeName());
        needGuava.add("com.google.common.eventbus.Subscribe");

        for (Method declaredMethod : declaredMethods) {
            dynamicFactory.method(declaredMethod.getName(), declaredMethod.getReturnType().getTypeName(), ClassUtils.toTypeName(declaredMethod.getParameterTypes()), methodName -> "return source." + methodName + "($$);");
            Subscribe subscribe = declaredMethod.getDeclaredAnnotation(Subscribe.class);
            if (null != subscribe) {
                needGuava.add(declaredMethod.getName());
                dynamicFactory.methodAnnotation(new AnnotationFactory() {
                    @Override
                    public String annotationName(String column) {
                        if (!needGuava.contains(column)) {
                            return null;
                        }
                        return Subscribe.class.getTypeName();
                    }

                    @Override
                    public Map<String, Object> annotationValues(String column) {
                        Map<String, Object> values = new HashMap<>(1 << 4);
                        values.put("name", subscribe.name());
                        values.put("type", EventbusType.GUAVA);
                        values.put("value", subscribe.value());
                        return values;
                    }
                });
                TypeDescribe typeDescribe = TypeDescribe.create(declaredMethod);
                if (typeDescribe.hasAnnotation("com.google.common.eventbus.Subscribe")) {
                    dynamicFactory.methodAnnotation(new AnnotationFactory() {
                        @Override
                        public String annotationName(String column) {
                            if (!needGuava.contains(column)) {
                                return null;
                            }
                            return "com.google.common.eventbus.Subscribe";
                        }

                        @Override
                        public Map<String, Object> annotationValues(String column) {
                            return Collections.emptyMap();
                        }
                    });
                }
            }
        }

        try {
            return dynamicFactory.toBean(Object.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return o;
    }
}
