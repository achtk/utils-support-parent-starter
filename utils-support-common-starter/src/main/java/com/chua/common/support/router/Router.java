package com.chua.common.support.router;

import com.chua.common.support.context.aggregate.JarAggregate;
import com.chua.common.support.context.bean.BeanObject;
import com.chua.common.support.context.definition.ClassDefinition;
import com.chua.common.support.context.definition.MethodDefinition;
import com.chua.common.support.context.definition.ObjectDefinition;
import com.chua.common.support.context.definition.TypeDefinition;
import com.chua.common.support.context.environment.StandardEnvironment;
import com.chua.common.support.context.factory.ApplicationContextBuilder;
import com.chua.common.support.context.factory.ConfigureApplicationContext;
import com.chua.common.support.context.resolver.*;
import com.chua.common.support.context.resolver.factory.SimpleNamedResolver;
import com.chua.common.support.context.resolver.factory.SimpleOrderResolver;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.reflection.reflections.Reflections;
import com.chua.common.support.reflection.reflections.scanners.Scanners;
import com.chua.common.support.reflection.reflections.util.ConfigurationBuilder;
import com.chua.common.support.utils.ArrayUtils;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.IdUtils;
import lombok.experimental.Accessors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * 路由
 *
 * @author CH
 */
@Accessors(fluent = true)
public class Router {

    private final NamedResolver namedResolver = new SimpleNamedResolver();
    private final OrderResolver orderResolver = new SimpleOrderResolver();
    private Class<? extends Annotation> type;
    protected ConfigureApplicationContext beanFactory;

    public <T extends Annotation> Router(Class<T> type, String[] packages) {
        this.type = type;

        this.beanFactory = ApplicationContextBuilder.newBuilder()
                .name(IdUtils.createTimeId())
                .openScanner(false)
                .environment(new StandardEnvironment())
                .build();

        ConfigurationBuilder configuration = new ConfigurationBuilder();
        configuration.addScanners(Scanners.MethodsAnnotated);
        configuration.addScanners(Scanners.TypesAnnotated);
        if (!ArrayUtils.isEmpty(packages)) {
            configuration.forPackages(packages);
        } else {
            configuration.setUrls(new ArrayList<>());
            configuration.forPackages("");
        }

        Reflections reflections = new Reflections(configuration);
        Set<Method> methodsAnnotatedWith = reflections.getMethodsAnnotatedWith(type);
        for (Method method : methodsAnnotatedWith) {
            T mapping = method.getDeclaredAnnotation(type);
            MethodDefinition methodDefinition = new MethodDefinition(method);
            methodDefinition.addAnnotation(mapping);
            methodDefinition.addBeanName(namedResolver.resolve(NamePair.builder().annotation(mapping).annotationType(mapping.annotationType()).type(method).build()));
            methodDefinition.order(orderResolver.resolve(NamePair.builder().annotation(mapping).build()));

            beanFactory.registerBean(methodDefinition);
        }

        Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(type);
        for (Class<?> aClass : typesAnnotatedWith) {
            ClassUtils.doWithMethods(aClass, method -> {
                T mapping = method.getDeclaredAnnotation(type);
                MethodDefinition methodDefinition = new MethodDefinition(method);
                if (null == mapping) {
                    methodDefinition.addBeanName(method.getName());
                } else {
                    methodDefinition.addAnnotation(mapping);
                    methodDefinition.addBeanName(namedResolver.resolve(NamePair.builder().annotation(mapping).annotationType(mapping.annotationType()).type(method).build()));
                    methodDefinition.order(orderResolver.resolve(NamePair.builder().annotation(mapping).build()));
                }

                beanFactory.registerBean(methodDefinition);
            });
        }
    }

    public static <T extends Annotation> Router create(Class<T> type, String... packages) {
        return new Router(type, packages);
    }

    public static <T extends Annotation> Router create(String... packages) {
        return new Router(Route.class, packages);
    }

    /**
     * 调用
     *
     * @param name 名称
     * @param args 参数
     */
    public void route(String name, Object... args) {
        BeanObject beanObject = beanFactory.getBean(name);
        beanObject.invoke(args);
    }

    /**
     * 调用
     *
     * @param returnType 返回类型
     * @param name       名称
     * @param args       参数
     */
    public <T> T route(Class<T> returnType, String name, Object... args) {
        BeanObject beanObject = beanFactory.getBean(name);
        return Converter.convertIfNecessary(beanObject.invoke(args).getInvoke().getValue(), returnType);
    }

    /**
     * 调用
     *
     * @param name 名称
     * @param args 参数
     */
    public void route(String name, Map<String, Object> args) {
        BeanObject beanObject = beanFactory.getBean(name);
        beanObject.invoke(args);
    }

    /**
     * 调用
     *
     * @param returnType 返回类型
     * @param name       名称
     * @param args       参数
     */
    public <T> T route(Class<T> returnType, String name, Map<String, Object> args) {
        BeanObject beanObject = beanFactory.getBean(name);
        return Converter.convertIfNecessary(beanObject.invoke(args).getInvoke().getValue(), returnType);
    }

    /**
     * 是否存在路由
     *
     * @param name 名称
     * @return 是否存在路由
     */
    public boolean hasRoute(String name) {
        BeanObject beanObject = beanFactory.getBean(name);
        return beanObject.isEmpty();
    }

    /**
     * 添加插件
     *
     * @param name      插件名称
     * @param aggregate 插件
     * @return this
     */
    @SuppressWarnings("ALL")
    public Router addPlugin(String name, JarAggregate aggregate) {
        beanFactory.mount(name, aggregate);
        return this;
    }
    /**
     * 删除插件
     *
     * @param name      插件名称
     * @return this
     */
    public Router removePlugin(String name) {
        beanFactory.unmount(name);
        return this;
    }
    /**
     * 删除插件
     *
     * @param aggregate 插件
     * @return this
     */
    public Router removePlugin(JarAggregate aggregate) {
        beanFactory.unmount(aggregate);
        return this;
    }
    /**
     * 添加路由
     *
     * @param bean 实现
     * @return this
     */
    @SuppressWarnings("ALL")
    public Router addRouter(Object bean) {
        if (bean instanceof TypeDefinition) {
            beanFactory.registerBean((TypeDefinition) bean);
            return this;
        }

        if (bean instanceof Class) {
            beanFactory.registerBean(new ClassDefinition((Class) bean));
            return this;
        }
        beanFactory.registerBean(ObjectDefinition.of(bean));
        return this;
    }

    /**
     * 添加路由
     *
     * @param name 路由
     * @param bean 实现
     * @return this
     */
    public Router addRouter(String name, Object bean) {
        TypeDefinition<Object> typeDefinition = ObjectDefinition.of(bean);
        typeDefinition.addBeanName(name);
        beanFactory.registerBean(typeDefinition);
        return this;
    }

    /**
     * 装配
     *
     * @param bean bean
     */
    public void autowire(Object bean) {
        beanFactory.autowire(bean);
    }
}
