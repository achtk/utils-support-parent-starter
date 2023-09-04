package com.chua.common.support.router;

import com.chua.common.support.converter.Converter;
import com.chua.common.support.function.strategy.resolver.NamePair;
import com.chua.common.support.function.strategy.resolver.NamedResolver;
import com.chua.common.support.function.strategy.resolver.SimpleNamedResolver;
import com.chua.common.support.objects.ConfigureContextConfiguration;
import com.chua.common.support.objects.ConfigureObjectContext;
import com.chua.common.support.objects.StandardConfigureObjectContext;
import com.chua.common.support.objects.bean.BeanObject;
import com.chua.common.support.objects.definition.ClassTypeDefinition;
import com.chua.common.support.objects.definition.MethodTypeDefinition;
import com.chua.common.support.objects.definition.ObjectTypeDefinition;
import com.chua.common.support.objects.definition.TypeDefinition;
import com.chua.common.support.objects.definition.resolver.OrderResolver;
import com.chua.common.support.objects.environment.properties.SimplePropertySource;
import com.chua.common.support.reflection.reflections.Reflections;
import com.chua.common.support.reflection.reflections.scanners.Scanners;
import com.chua.common.support.reflection.reflections.util.ConfigurationBuilder;
import com.chua.common.support.utils.ArrayUtils;
import com.chua.common.support.utils.ClassUtils;
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
    private final OrderResolver orderResolver = new OrderResolver.DefaultOrderResolver();
    private Class<? extends Annotation> type;
    protected ConfigureObjectContext beanFactory;

    public <T extends Annotation> Router(Class<T> type, String[] packages) {
        this.type = type;

        this.beanFactory =
                new StandardConfigureObjectContext(ConfigureContextConfiguration.builder()
                        .register((SimplePropertySource) name -> null)
                        .outSideInAnnotation(true).build());

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
            MethodTypeDefinition methodDefinition = new MethodTypeDefinition(method);
            methodDefinition.addAnnotation(mapping);
            methodDefinition.addBeanName(namedResolver.resolve(NamePair.builder().annotation(mapping).annotationType(mapping.annotationType()).type(method).build()));
            methodDefinition.order(orderResolver.resolve(NamePair.builder().annotation(mapping).build()));

            beanFactory.registerBean(methodDefinition);
        }

        Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(type);
        for (Class<?> aClass : typesAnnotatedWith) {
            ClassUtils.doWithMethods(aClass, method -> {
                T mapping = method.getDeclaredAnnotation(type);
                MethodTypeDefinition methodDefinition = new MethodTypeDefinition(method);
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
        beanObject.newInvoke(args);
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
        return Converter.convertIfNecessary(beanObject.newInvoke(args).invoke(), returnType);
    }

    /**
     * 调用
     *
     * @param name 名称
     * @param args 参数
     */
    public void route(String name, Map<String, Object> args) {
        BeanObject beanObject = beanFactory.getBean(name);
        beanObject.newInvoke(args).invoke();
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
        return Converter.convertIfNecessary(beanObject.newInvoke(args).invoke(), returnType);
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
            beanFactory.registerBean(new ClassTypeDefinition((Class) bean));
            return this;
        }
        beanFactory.registerBean(new ObjectTypeDefinition(bean));
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
        beanFactory.registerBean(new ObjectTypeDefinition(name, bean));
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
