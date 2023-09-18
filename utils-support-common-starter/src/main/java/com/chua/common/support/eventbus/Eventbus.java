package com.chua.common.support.eventbus;

import com.chua.common.support.objects.definition.attribute.AnnotationAttribute;
import com.chua.common.support.objects.definition.element.AnnotationDescribe;
import com.chua.common.support.objects.definition.element.MethodDescribe;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.ObjectUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import static com.chua.common.support.constant.NameConstant.CGLIB$$;

/**
 * 事件
 *
 * @author CH
 */
public interface Eventbus{


    /**
     * 新默认值
     *
     * @return {@link Eventbus}
     */
    static Eventbus newDefault() {
        return new EventbusImpl(null);
    }

    /**
     * 新默认值
     *
     * @param executor 遗嘱执行人
     * @return {@link Eventbus}
     */
    static Eventbus newDefault(Executor executor) {
        return new EventbusImpl(executor);
    }
    /**
     * 注册用户
     * 注册
     *
     * @param name     名称
     * @param eventbus 事件总线
     * @return {@link Eventbus}
     */
    Eventbus registerSubscriber(String name, SubscribeEventbus eventbus);

    /**
     * 注销
     *
     * @param name 名称
     * @return {@link Eventbus}
     */
    Eventbus unregisterSubscriber(String name);


    /**
     * 注册对象
     *
     * @param eventbusEvent event
     * @return this
     */
    Eventbus register(EventbusEvent... eventbusEvent);

    /**
     * 注册对象
     *
     * @param entity 实体
     * @return this
     */
    default Eventbus registerObject(Object entity) {
        Class<?> aClass = entity.getClass();
        String typeName = aClass.getTypeName();
        if (typeName.contains(CGLIB$$)) {
            aClass = ObjectUtils.defaultIfNull(ClassUtils.forName(typeName.substring(0, typeName.indexOf("$$"))), aClass);
        }

        Map<Method, AnnotationDescribe> collect = Arrays.stream(aClass.getDeclaredMethods()).map(MethodDescribe::new).filter(it -> {
            return it.hasAnnotation(Subscribe.class);
        }).collect(Collectors.toMap(MethodDescribe::method, it1 -> it1.getAnnotationDescribe(Subscribe.class)));


        if (collect.isEmpty()) {
            return this;
        }

        for (Map.Entry<Method, AnnotationDescribe> entry : collect.entrySet()) {
            processSubscribe(entry.getValue(), entry.getKey().getName(), entry.getKey(), entity);
        }

        return this;
    }


    /**
     * 注册
     *
     * @param method 方法
     * @param entity 实体
     */
    default void register(Method method, Object entity) {
        MethodDescribe methodDescribe = new MethodDescribe(method, entity.getClass(), entity);
        AnnotationDescribe annotationDescribe = methodDescribe.getAnnotationDescribe(Subscribe.class);
        if (null == annotationDescribe) {
            return;
        }

        processSubscribe(annotationDescribe, method.getName(), method, entity);
    }

    /**
     * 预加载
     *
     * @param annotationDescribe 注解
     * @param beanName  名称
     * @param method    方法
     * @param bean      对象
     */
    default void processSubscribe(AnnotationDescribe annotationDescribe, String beanName, Method method, Object bean) {
        AnnotationAttribute attributes = annotationDescribe.getAnnotationAttribute();
        String value = "";
        if (null != attributes) {
            value = attributes.getString("name");
            if (null == value) {
                value = "";
            }
        }
        register(new EventbusEvent(annotationDescribe, method, beanName, bean));
    }

    /**
     * 注销
     *
     * @param eventbusEvent event
     * @return this
     */
    Eventbus unregister(EventbusEvent eventbusEvent);

    /**
     * 邮递
     * 下发消息
     *
     * @param name          名称
     * @param message       消息
     * @param subscribeName 订阅名称
     * @return 结果
     */
     EventbusResult post(String subscribeName, String name, Object message);

    /**
     * 下发消息
     *
     * @param name    名称
     * @param message 消息
     * @return 结果
     */
    default EventbusResult post(String name, Object message) {
        return post(null, name, message);
    }


    /**
     * 停机
     */
    void shutdown();
}
