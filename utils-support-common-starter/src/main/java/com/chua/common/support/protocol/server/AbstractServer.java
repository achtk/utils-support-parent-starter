package com.chua.common.support.protocol.server;

import com.chua.common.support.constant.CommonConstant;
import com.chua.common.support.constant.Projects;
import com.chua.common.support.context.bean.BeanObject;
import com.chua.common.support.context.definition.MethodDefinition;
import com.chua.common.support.context.definition.ObjectDefinition;
import com.chua.common.support.context.definition.TypeDefinition;
import com.chua.common.support.context.environment.StandardEnvironment;
import com.chua.common.support.context.environment.property.FunctionPropertySource;
import com.chua.common.support.context.factory.ApplicationContextBuilder;
import com.chua.common.support.context.factory.ConfigureApplicationContext;
import com.chua.common.support.media.MediaType;
import com.chua.common.support.media.MediaTypeFactory;
import com.chua.common.support.protocol.server.annotations.Mapping;
import com.chua.common.support.protocol.server.parameter.ParameterResolver;
import com.chua.common.support.protocol.server.request.Request;
import com.chua.common.support.protocol.server.resolver.Resolver;
import com.chua.common.support.protocol.server.resolver.ResourceResolver;
import com.chua.common.support.reflection.describe.ParameterDescribe;
import com.chua.common.support.reflection.reflections.Reflections;
import com.chua.common.support.reflection.reflections.scanners.Scanners;
import com.chua.common.support.reflection.reflections.util.ConfigurationBuilder;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.CollectionUtils;
import com.chua.common.support.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static com.chua.common.support.constant.CommonConstant.EMPTY_ARRAY;

/**
 * 客户端
 *
 * @author CH
 */
@Slf4j
public abstract class AbstractServer implements Server, Constant {

    protected ServerRequest request;
    protected ConfigureApplicationContext beanFactory;
    private TemplateResolver templateResolver;

    protected AbstractServer(ServerOption serverOption) {
        this.request = new ServerRequest(serverOption);
        this.beforeAfterPropertiesSet();
        afterPropertiesSet();
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    private void beforeAfterPropertiesSet() {


        this.beanFactory = ApplicationContextBuilder.newBuilder()
                .environment(new StandardEnvironment()
                        .addPropertySource("server", new FunctionPropertySource("server", s -> request.getObject(s))))
                .build();
        Map<String, TemplateResolver> list = ServiceProvider.of(TemplateResolver.class).list();
        for (TemplateResolver resolver : list.values()) {
            beanFactory.registerBean(ObjectDefinition.of(resolver));
        }

        List<Object> bean = request.getList("bean");
        for (Object o : bean) {
            beanFactory.registerBean(ObjectDefinition.of(o));
        }


        if (request.getBooleanValue("auto-scanner", false)) {

            ConfigurationBuilder configuration = new ConfigurationBuilder();
            configuration.addScanners(Scanners.MethodsAnnotated);
            if (!CollectionUtils.isEmpty(request.getList("packages"))) {
                configuration.forPackages(request.getList("packages", String.class).toArray(EMPTY_ARRAY));
            } else {
                configuration.setUrls(new ArrayList<>());
                configuration.forPackages("");
            }

            Reflections reflections = new Reflections(configuration);
            Set<Method> methodsAnnotatedWith = reflections.getMethodsAnnotatedWith(Mapping.class);
            for (Method method : methodsAnnotatedWith) {
                Mapping mapping = method.getDeclaredAnnotation(Mapping.class);
                MethodDefinition methodDefinition = new MethodDefinition(method);
                methodDefinition.addBeanName(mapping.value());

                beanFactory.registerBean(methodDefinition);
            }
        }


        this.templateResolver = beanFactory.getBean(TemplateResolver.class);

    }

    /**
     * 监听
     *
     * @param name 名称
     * @return 结果
     */
    protected BeanObject getMapping(String name) {
        return beanFactory.getBean(name);
    }

    /**
     * 监听
     *
     * @param type 名称
     * @return 结果
     */
    protected Map<String, BeanObject> getMappingByMethodParameterType(Class<?>... type) {
        Map<String, TypeDefinition<Object>> beanByMethod = beanFactory.getBeanByMethod(String.class);
        return beanByMethod.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, it -> new BeanObject(it.getValue(), it.getValue().getObject(beanFactory), beanFactory)));
    }


    @Override
    public void start() {
        log.debug("服务启动中");
        run();
        log.info("服务启动成功: 端口: {}, PID: {}", request.getInteger("port"), Projects.getPid());
    }

    @Override
    public Server register(Object bean) {
        beanFactory.registerBean(bean instanceof TypeDefinition ? (TypeDefinition) bean : ObjectDefinition.of(bean));
        return this;
    }

    @Override
    public Server register(String name, Object bean) {
        TypeDefinition<Object> typeDefinition = ObjectDefinition.of(bean);
        typeDefinition.addBeanName(name);
        beanFactory.registerBean(typeDefinition);
        return this;
    }

    /**
     * 获取IP
     *
     * @return IP
     */
    protected String getHost() {
        return request.getString("host", "0.0.0.0");
    }

    /**
     * 获取端口
     *
     * @return 端口
     */
    protected int getPort() {
        return request.getIntValue("port", 12345);
    }

    @Override
    public void close() {
        shutdown();
    }

    /**
     * 停止
     */
    abstract protected void shutdown();

    /**
     * 启动
     */
    abstract protected void run();

    /**
     * 获取解释器
     *
     * @param produces 返回类型
     * @param router   route
     * @return 结果
     */
    protected Resolver getResolver(String produces, String router) {
        Resolver resolver = new ResourceResolver(beanFactory);
        if (null != produces) {
            Resolver extension = ServiceProvider.of(Resolver.class).getNewExtension(produces, templateResolver, beanFactory);
            if (null != extension) {
                return extension;
            }
        }

        if (resolver.hasResolve(router)) {
            Optional<MediaType> mediaType = MediaTypeFactory.getMediaType(router);
            if (mediaType.isPresent()) {
                Resolver extension = ServiceProvider.of(Resolver.class).getNewExtension(mediaType.get().subtype(), templateResolver, beanFactory);
                if (null == extension) {
                    return resolver;
                }
                return extension;
            }
        }
        return getResolver("json", router);
    }

    /**
     * 获取解释器
     *
     * @param produces 返回类型
     * @param accept   请求接受类型
     * @param router   route
     * @return 结果
     */
    protected Resolver getResolver(String produces, String accept, String router) {
        if (StringUtils.isNullOrEmpty(accept) || "*/*".equals(accept)) {
            return getResolver(produces, router);
        }

        return getResolver(accept, router);
    }


    protected Object getValue(ParameterDescribe parameterDescribe, Request request) {
        ParameterResolver resolver = null;
        List<ParameterResolver> anyBean = beanFactory.getAnyBean(ParameterResolver.class);
        for (ParameterResolver parameterResolver : anyBean) {
            if (parameterResolver.isMatch(parameterDescribe)) {
                resolver = parameterResolver;
                break;
            }
        }

        if (null == resolver) {
            return beanFactory.getBean(parameterDescribe.returnClassType());
        }

        if (parameterDescribe.returnClassType().isAssignableFrom(Request.class)) {
            return request;
        }

        return resolver.resolve(parameterDescribe, request);
    }

}
