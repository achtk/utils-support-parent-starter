package com.chua.common.support.protocol.server;

import com.chua.common.support.constant.Projects;
import com.chua.common.support.media.MediaType;
import com.chua.common.support.media.MediaTypeFactory;
import com.chua.common.support.objects.ConfigureContextConfiguration;
import com.chua.common.support.objects.StandardConfigureObjectContext;
import com.chua.common.support.objects.bean.BeanObject;
import com.chua.common.support.objects.bean.SingleBeanObject;
import com.chua.common.support.objects.definition.ObjectTypeDefinition;
import com.chua.common.support.objects.definition.TypeDefinition;
import com.chua.common.support.objects.definition.element.ParameterDescribe;
import com.chua.common.support.objects.environment.properties.FunctionPropertySource;
import com.chua.common.support.objects.provider.ObjectProvider;
import com.chua.common.support.protocol.server.annotations.ServiceMapping;
import com.chua.common.support.protocol.server.parameter.ParameterResolver;
import com.chua.common.support.protocol.server.request.Request;
import com.chua.common.support.protocol.server.resolver.Resolver;
import com.chua.common.support.protocol.server.resolver.ResourceResolver;
import com.chua.common.support.reflection.reflections.Reflections;
import com.chua.common.support.reflection.reflections.scanners.Scanners;
import com.chua.common.support.reflection.reflections.util.ConfigurationBuilder;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.CollectionUtils;
import com.chua.common.support.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
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

    private static final String AUTO = "auto-scanner";
    private static final String PACKAGES = "packages";
    private static final String ANY = "*/*";
    protected ServerRequest request;
    private TemplateResolver templateResolver;
    private StandardConfigureObjectContext objectContext;

    protected AbstractServer(ServerOption serverOption) {
        this.request = new ServerRequest(serverOption);
        this.beforeAfterPropertiesSet();
        afterPropertiesSet();
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    private void beforeAfterPropertiesSet() {
        this.objectContext =
                new StandardConfigureObjectContext(ConfigureContextConfiguration.builder()
                        .register("server", new FunctionPropertySource("server", s -> request.getObject(s)))
                        .outSideInAnnotation(true).build());

        Map<String, TemplateResolver> list = ServiceProvider.of(TemplateResolver.class).list();
        for (TemplateResolver resolver : list.values()) {
            objectContext.registerBean(resolver);
        }

        List<Object> bean = request.getList("bean");
        for (Object o : bean) {
            objectContext.registerBean(o);
        }


        if (request.getBooleanValue(AUTO, false)) {

            ConfigurationBuilder configuration = new ConfigurationBuilder();
            configuration.addScanners(Scanners.MethodsAnnotated);
            if (!CollectionUtils.isEmpty(request.getList(PACKAGES))) {
                configuration.forPackages(request.getList(PACKAGES, String.class).toArray(EMPTY_ARRAY));
            } else {
                configuration.setUrls(new ArrayList<>());
                configuration.forPackages("");
            }

            Reflections reflections = new Reflections(configuration);
            Set<Method> methodsAnnotatedWith = reflections.getMethodsAnnotatedWith(ServiceMapping.class);
            for (Method method : methodsAnnotatedWith) {
                ServiceMapping mapping = method.getDeclaredAnnotation(ServiceMapping.class);
                objectContext.registerBean(method).addBeanName(mapping.value());
            }
        }


        this.templateResolver = objectContext.getBean(TemplateResolver.class).get();

    }

    /**
     * 监听
     *
     * @param name 名称
     * @return 结果
     */
    protected BeanObject getMapping(String name) {
        return objectContext.getBean(name);
    }

    /**
     * 监听
     *
     * @param annotationType 名称
     * @return 结果
     */
    protected Map<String, BeanObject> getMappingByMethodParameterType(Class<? extends Annotation> annotationType) {
        Map<String, TypeDefinition> beanByMethod = objectContext.getBeanByMethod(annotationType);
        return beanByMethod.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, it ->
                new SingleBeanObject(it.getValue(), objectContext)));
    }


    @Override
    public void start() {
        log.debug("服务启动中");
        run();
        log.info("服务启动成功: 端口: {}, PID: {}", request.getInteger("port"), Projects.getPid());
    }

    @Override
    public Server register(Object bean) {
        objectContext.registerBean(bean);
        return this;
    }

    @Override
    public Server register(String name, Object bean) {
        objectContext.registerBean(new ObjectTypeDefinition(name, bean));
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
        Resolver resolver = new ResourceResolver(objectContext);
        if (null != produces) {
            Resolver extension = ServiceProvider.of(Resolver.class).getNewExtension(produces, templateResolver, objectContext);
            if (null != extension) {
                return extension;
            }
        }

        if (resolver.hasResolve(router)) {
            Optional<MediaType> mediaType = MediaTypeFactory.getMediaType(router);
            if (mediaType.isPresent()) {
                Resolver extension = ServiceProvider.of(Resolver.class).getNewExtension(mediaType.get().subtype(), templateResolver, objectContext);
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
        if (StringUtils.isNullOrEmpty(accept) || ANY.equals(accept)) {
            return getResolver(produces, router);
        }

        return getResolver(accept, router);
    }


    protected Object getValue(ParameterDescribe parameterDefinition, Request request) {
        ParameterResolver resolver = null;
        ObjectProvider<ParameterResolver> objectContextBean = objectContext.getBean(ParameterResolver.class);
        Collection<ParameterResolver> all = objectContextBean.getAll();
        for (ParameterResolver parameterResolver : all) {
            if (parameterResolver.isMatch(parameterDefinition)) {
                resolver = parameterResolver;
                break;
            }
        }

        if (null == resolver) {
            return objectContext.getBean(parameterDefinition.getType());
        }

        if (parameterDefinition.isAssignableFrom(Request.class)) {
            return request;
        }

        return resolver.resolve(parameterDefinition, request);
    }

}
