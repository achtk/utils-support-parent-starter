package com.chua.common.support.mapping;

import com.chua.common.support.converter.Converter;
import com.chua.common.support.function.Splitter;
import com.chua.common.support.lang.proxy.DelegateMethodIntercept;
import com.chua.common.support.lang.proxy.ProxyMethod;
import com.chua.common.support.lang.proxy.ProxyUtils;
import com.chua.common.support.lang.robin.Robin;
import com.chua.common.support.mapping.annotations.*;
import com.chua.common.support.mapping.invoke.HttpInvoker;
import com.chua.common.support.objects.definition.element.MethodDescribe;
import com.chua.common.support.objects.definition.element.ParameterDescribe;
import com.chua.common.support.placeholder.MapMixSystemPlaceholderResolver;
import com.chua.common.support.placeholder.PlaceholderSupport;
import com.chua.common.support.placeholder.PropertyResolver;
import com.chua.common.support.placeholder.StringValuePropertyResolver;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.ArrayUtils;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.ObjectUtils;
import com.chua.common.support.utils.StringUtils;

import java.util.Map;
import java.util.function.Function;

import static com.chua.common.support.constant.CommonConstant.EMPTY_STRING;
import static com.chua.common.support.constant.NameConstant.HTTP;

/**
 * 实体映射
 *
 * @author CH
 */
public class HttpMapping<T> extends AbstractMapping<T> {

    public HttpMapping(Class<T> beanType, MappingConfig mappingConfig) {
        super(beanType, mappingConfig);
    }


    @Override
    public T get() {
        return ProxyUtils.proxy(beanType, beanType.getClassLoader(), new DelegateMethodIntercept<>(beanType, new Function<ProxyMethod, Object>() {
            @Override
            public Object apply(ProxyMethod proxyMethod) {
                MethodDescribe methodDescribe = new MethodDescribe(proxyMethod.getMethod());
                Request.RequestBuilder requestBuilder = Request.builder();
                Response.ResponseBuilder responseBuilder = Response.builder();
                doAnalysis(requestBuilder, responseBuilder, proxyMethod, methodDescribe);
                Request request = requestBuilder.build();

                HttpInvoker httpInvoke = ServiceProvider.of(HttpInvoker.class).getNewExtension(request.getInvokeType(), mappingConfig);
                String url = getUrl(request);
                Object execute = httpInvoke.execute(url, request);
                return responseBuilder.build().getValue(execute, proxyMethod);
            }
        }));
    }


    /**
     * 获取url
     *
     * @param request 要求
     * @return {@link String}
     */
    private String getUrl(Request request) {
        String balance = request.getBalance();
        Robin robin = ServiceProvider.of(Robin.class).getExtension(balance);
        Robin robin1 = robin.create();
        for (String s : Splitter.on(",").omitEmptyStrings().trimResults().splitToSet(request.getAddress())) {
            robin1.addNode(s + request.getUrl());
        }
        return format(robin1.selectNode().getString(), request);
    }

    /**
     * 总体安排
     *
     * @param string  string
     * @param request 要求
     * @return {@link String}
     */
    private String format(String string, Request request) {
        PlaceholderSupport placeholderSupport = new PlaceholderSupport();
        MapMixSystemPlaceholderResolver placeholderResolver = new MapMixSystemPlaceholderResolver(request.getBody());
        placeholderResolver.add(request.getHeader());
        placeholderSupport.setResolver(placeholderResolver);
        PropertyResolver propertyResolver = new StringValuePropertyResolver(placeholderSupport);
        return propertyResolver.resolvePlaceholders(string);
    }

    /**
     * 解析
     *
     * @param builder         要求
     * @param proxyMethod     代理方法
     * @param methodDescribe  方法描述
     * @param responseBuilder 响应生成器
     */
    private void doAnalysis(Request.RequestBuilder builder, Response.ResponseBuilder responseBuilder, ProxyMethod proxyMethod, MethodDescribe methodDescribe) {
        doAddress(builder, proxyMethod);
        doUrl(builder, responseBuilder, methodDescribe);
        doBody(builder, proxyMethod, methodDescribe);
        doHeader(builder, proxyMethod, methodDescribe);
    }

    /**
     * 消息头
     *
     * @param builder        建设者
     * @param proxyMethod    代理方法
     * @param methodDescribe 方法描述
     */
    private void doHeader(Request.RequestBuilder builder, ProxyMethod proxyMethod, MethodDescribe methodDescribe) {
        MappingHeaders mappingHeaders = methodDescribe.getAnnotation(MappingHeaders.class);
        if(null != mappingHeaders) {
            for (MappingHeader mappingHeader : mappingHeaders.value()) {
                doHeader(mappingHeader, builder, proxyMethod);
            }
        }

        MappingHeader mappingHeader = methodDescribe.getAnnotation(MappingHeader.class);
        if(null == mappingHeader) {
            return;
        }
        doHeader(mappingHeader, builder, proxyMethod);
    }

    /**
     * 消息头
     *
     * @param mappingHeader 映射标头
     * @param builder       建设者
     * @param proxyMethod   代理方法
     */
    private void doHeader(MappingHeader mappingHeader, Request.RequestBuilder builder, ProxyMethod proxyMethod) {
        System.out.println();
    }

    /**
     * 消息体
     *
     * @param builder        要求
     * @param methodDescribe 方法描述
     * @param proxyMethod    代理方法
     */
    private void doBody(Request.RequestBuilder builder, ProxyMethod proxyMethod, MethodDescribe methodDescribe) {
        Map<String, ParameterDescribe> parameters = methodDescribe.parameters();
        int index = 0;
        Object[] args = proxyMethod.getArgs();
        for (Map.Entry<String, ParameterDescribe> entry : parameters.entrySet()) {
            ParameterDescribe parameterDescribe = entry.getValue();
            if(isIgnore(parameterDescribe)) {
                continue;
            }
            builder.body(entry.getKey(), ObjectUtils.defaultIfNull(
                    ArrayUtils.getIndex(args, index ++),
                    Converter.convertIfNecessary(getDefaultValue(parameterDescribe), parameterDescribe.getType())
            ));
        }
    }

    /**
     * 被忽略
     *
     * @param parameterDescribe 参数描述
     * @return boolean
     */
    private boolean isIgnore(ParameterDescribe parameterDescribe) {
        MappingParam mappingParam = parameterDescribe.getAnnotation(MappingParam.class);
        return null != mappingParam && mappingParam.ignore();
    }

    /**
     * 获取默认值
     *
     * @param parameterDescribe 参数描述
     * @return {@link Object}
     */
    private Object getDefaultValue(ParameterDescribe parameterDescribe) {
        MappingParam mappingParam = parameterDescribe.getAnnotation(MappingParam.class);
        return null == mappingParam ? null : mappingParam.defaultValue();
    }

    /**
     * 做住址
     * 地址解析
     *
     * @param builder        请求
     * @param proxyMethod    代理方法
     */
    private void doAddress(Request.RequestBuilder builder, ProxyMethod proxyMethod) {
        MappingAddress mappingAddress = proxyMethod.getMethod().getDeclaringClass().getDeclaredAnnotation(MappingAddress.class);
        if (null == mappingAddress) {
            return;
        }

        builder.invokeType(mappingAddress.invokeType());
        builder.readTimeout(mappingAddress.readTimeout());
        builder.connectTimeout(mappingAddress.connectTimeout());
        if(StringUtils.isNotBlank(mappingAddress.value())) {
            builder.address(mappingAddress.value());
        } else {
            String[] protocols = mappingConfig.getProtocol();
            String protocol = HTTP;
            if(!ArrayUtils.isEmpty(protocols)) {
                protocol =  ArrayUtils.getIndex(protocols, 0);
            }
            builder.address(protocol + "://" + mappingConfig.getHost() + (StringUtils.defaultString(mappingConfig.getPath(), EMPTY_STRING)));
        }
        builder.balance(mappingAddress.balance());
    }

    /**
     * 做url
     * url
     *
     * @param builder         请求
     * @param methodDescribe  方法描述
     * @param responseBuilder 响应生成器
     */
    private void doUrl(Request.RequestBuilder builder, Response.ResponseBuilder responseBuilder, MethodDescribe methodDescribe) {
        MappingRequest mappingRequest = methodDescribe.getAnnotation(MappingRequest.class);
        responseBuilder.returnType(methodDescribe.getType());
        if (null == mappingRequest) {
            builder.url(methodDescribe.name());
            return;
        }

        responseBuilder.jsonPath(mappingRequest.jsonPath());
        if(!ClassUtils.isVoid(mappingRequest.returnType())) {
            responseBuilder.returnType(mappingRequest.returnType());
        }
        builder.readTimeout(mappingRequest.readTimeout());
        builder.connectTimeout(mappingRequest.connectTimeout());
        String value = mappingRequest.value();
        String[] split = value.split("\\s+");
        if (split.length == 1) {
            builder.url(split[0]);
            return;
        }

        builder.url(split[1]);
        builder.method(split[0].toUpperCase());
    }
}
