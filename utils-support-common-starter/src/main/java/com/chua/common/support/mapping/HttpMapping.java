package com.chua.common.support.mapping;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.function.Splitter;
import com.chua.common.support.lang.proxy.DelegateMethodIntercept;
import com.chua.common.support.lang.proxy.ProxyMethod;
import com.chua.common.support.lang.proxy.ProxyUtils;
import com.chua.common.support.lang.robin.Robin;
import com.chua.common.support.mapping.annotations.*;
import com.chua.common.support.mapping.condition.MappingCondition;
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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.chua.common.support.constant.CommonConstant.EMPTY_STRING;
import static com.chua.common.support.constant.CommonConstant.SYMBOL_LEFT_BIG_PARENTHESES;
import static com.chua.common.support.constant.NameConstant.HTTP;

/**
 * 实体映射
 *
 * @author CH
 */
@Spi("http")
public class HttpMapping<T> extends AbstractMapping<T> {
    final PlaceholderSupport placeholderSupport = new PlaceholderSupport();

    {
        placeholderSupport.setPlaceholderPrefix(SYMBOL_LEFT_BIG_PARENTHESES);
    }

    public HttpMapping(Class<T> beanType, MappingConfig mappingConfig, MappingBinder mappingBinder) {
        super(beanType, mappingConfig, mappingBinder);
    }


    @Override
    public T get() {
        T proxy = ProxyUtils.proxy(beanType, beanType.getClassLoader(), new DelegateMethodIntercept<>(beanType, new Function<ProxyMethod, Object>() {
            @Override
            public Object apply(ProxyMethod proxyMethod) {
                MethodDescribe methodDescribe = new MethodDescribe(proxyMethod.getMethod());
                Request.RequestBuilder requestBuilder = Request.builder();
                Response.ResponseBuilder responseBuilder = Response.builder();
                Pretreatment pretreatment = new Pretreatment();
                doAnalysis(requestBuilder, responseBuilder, proxyMethod, methodDescribe, pretreatment);
                Request request = requestBuilder.build();
                refresh(request, pretreatment);

                HttpInvoker httpInvoke = ServiceProvider.of(HttpInvoker.class).getNewExtension(request.getInvokeType(), mappingConfig);
                String url = getUrl(request);
                Object execute = httpInvoke.execute(url, request);
                return responseBuilder.build().getValue(execute, proxyMethod);
            }
        }));
        mappingBinder.bind(beanType.getSimpleName(), beanType, proxy);
        return proxy;
    }

    /**
     * 刷新
     *
     * @param request      要求
     * @param pretreatment 预处理
     */
    private void refresh(Request request, Pretreatment pretreatment) {
        refreshBody(request.getBody(), request, pretreatment);
        refreshHeader(request.getHeader(), request, pretreatment);

    }

    /**
     * 刷新标头
     *
     * @param header       头球
     * @param request      要求
     * @param pretreatment 预处理
     */
    private void refreshHeader(Map<String, String> header, Request request, Pretreatment pretreatment) {
        Map<String, String> headerTmp = new HashMap<>(header.size());
        for (Map.Entry<String, String> entry : header.entrySet()) {
            headerTmp.put(entry.getKey(), format(entry.getValue(), request));
        }

        Map<String, String> pretreatmentHeader = pretreatment.getHeader();
        for (Map.Entry<String, String> entry : pretreatmentHeader.entrySet()) {
            headerTmp.put(entry.getKey(), ObjectUtils.defaultIfNull(getMethodValue(entry.getValue(), request), "").toString());
        }

        request.setHeader(headerTmp);
    }

    /**
     * 刷新主体
     *
     * @param body         身体
     * @param request      要求
     * @param pretreatment 预处理
     */
    private void refreshBody(Map<String, Object> body, Request request, Pretreatment pretreatment) {
        Map<String, Object> bodyTmp = new HashMap<>(body.size());
        for (Map.Entry<String, Object> entry : body.entrySet()) {
            String key = entry.getKey();
            if (pretreatment.hasKey(key)) {
                continue;
            }
            bodyTmp.put(key, format(entry.getValue(), request));
        }

        Map<String, String> pretreatmentBody = pretreatment.getBody();
        for (Map.Entry<String, String> entry : pretreatmentBody.entrySet()) {
            bodyTmp.put(entry.getKey(), getMethodValue(entry.getValue(), request));
        }
        request.setBody(bodyTmp);
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
     * @param value   string
     * @param request 要求
     * @return {@link String}
     */
    @SuppressWarnings("ALL")
    private <E> E format(E value, Request request) {
        if (!(value instanceof String)) {
            return value;
        }

        MapMixSystemPlaceholderResolver placeholderResolver = new MapMixSystemPlaceholderResolver(new LinkedHashMap<>(request.getBody()));
        placeholderResolver.add(request.getHeader());
        Map<String, Object> config = new HashMap<>(1 << 4);
        config.put("config.appKey", mappingConfig.getAppKey());
        config.put("config.appSecret", mappingConfig.getSecretAccessKey());
        config.put("config.host", mappingConfig.getHost());
        config.put("config.path", mappingConfig.getPath());
        config.put("config", mappingConfig);
        placeholderResolver.add(config);

        placeholderSupport.setResolver(placeholderResolver);
        PropertyResolver propertyResolver = new StringValuePropertyResolver(placeholderSupport);
        return (E) propertyResolver.resolvePlaceholders(String.valueOf(value));
    }

    /**
     * 解析
     *
     * @param builder         要求
     * @param responseBuilder 响应生成器
     * @param proxyMethod     代理方法
     * @param methodDescribe  方法描述
     * @param pretreatment    预处理
     */
    private void doAnalysis(Request.RequestBuilder builder, Response.ResponseBuilder responseBuilder, ProxyMethod proxyMethod, MethodDescribe methodDescribe, Pretreatment pretreatment) {
        doAddress(builder, proxyMethod);
        doUrl(builder, responseBuilder, methodDescribe);
        doBody(builder, proxyMethod, methodDescribe, pretreatment);
        doHeader(builder, proxyMethod, methodDescribe, pretreatment);
    }

    /**
     * 做头球
     * 消息头
     *
     * @param builder        建设者
     * @param proxyMethod    代理方法
     * @param methodDescribe 方法描述
     * @param pretreatment   预处理
     */
    private void doHeader(Request.RequestBuilder builder, ProxyMethod proxyMethod, MethodDescribe methodDescribe, Pretreatment pretreatment) {
        MappingHeaders mappingHeaders = methodDescribe.getAnnotation(MappingHeaders.class);
        if (null != mappingHeaders) {
            for (MappingHeader mappingHeader : mappingHeaders.value()) {
                doHeader(mappingHeader, builder, pretreatment);
            }
        }

        MappingHeader mappingHeader = methodDescribe.getAnnotation(MappingHeader.class);
        if (null == mappingHeader) {
            return;
        }
        doHeader(mappingHeader, builder, pretreatment);
    }

    /**
     * 做头球
     * 消息头
     *
     * @param mappingHeader 映射标头
     * @param builder       建设者
     * @param pretreatment  预处理
     */
    private void doHeader(MappingHeader mappingHeader, Request.RequestBuilder builder, Pretreatment pretreatment) {
        Class<?> aClass = mappingHeader.conditionType();
        if (!aClass.isInterface() && MappingCondition.class.isAssignableFrom(aClass)) {
            MappingCondition mappingCondition = (MappingCondition) ClassUtils.forObject(aClass);
            if (null == mappingCondition) {
                return;
            }
            builder.header(mappingHeader.name(), mappingCondition.resolve(mappingHeader.name(), mappingConfig, mappingBinder));
            return;
        }


        builder.body(mappingHeader.name(), mappingHeader.value());
        if (mappingHeader.type() == MappingParam.ParamType.METHOD) {
            pretreatment.addHeader(mappingHeader.name(), mappingHeader.value());
            return;
        }
    }

    /**
     * 做身体
     * 消息体
     *
     * @param builder        要求
     * @param proxyMethod    代理方法
     * @param methodDescribe 方法描述
     * @param pretreatment   预处理
     */
    private void doBody(Request.RequestBuilder builder, ProxyMethod proxyMethod, MethodDescribe methodDescribe, Pretreatment pretreatment) {
        Map<String, ParameterDescribe> parameters = methodDescribe.parameters();
        int index = 0;
        Object[] args = proxyMethod.getArgs();
        for (Map.Entry<String, ParameterDescribe> entry : parameters.entrySet()) {
            ParameterDescribe parameterDescribe = entry.getValue();
            if (isIgnore(parameterDescribe)) {
                continue;
            }
            MappingParam mappingParam = parameterDescribe.getAnnotation(MappingParam.class);
            if (null != mappingParam && mappingParam.type() == MappingParam.ParamType.METHOD) {
                pretreatment.addBody(entry.getKey(), mappingParam.value());
                builder.body(entry.getKey(), mappingParam.value());
                continue;
            }

            builder.body(entry.getKey(), ObjectUtils.defaultIfNull(
                    ArrayUtils.getIndex(args, index++),
                    Converter.convertIfNecessary(getDefaultValue(parameterDescribe), parameterDescribe.getType())
            ));
        }
    }

    /**
     * 获取方法值
     *
     * @param value   表达式
     * @param request 要求
     * @return {@link Object}
     */
    private Object getMethodValue(String value, Request request) {
        if (!value.contains("(")) {
            value += "()";
        }

        int index = value.indexOf("(");
        String method = value.substring(0, index);

        String substring = value.substring(index + 1, value.length() - 1);
        List<String> strings = Splitter.on(',').omitEmptyStrings().trimResults().splitToList(substring);

        StringBuilder stringBuilder = new StringBuilder(method).append("(");
        for (String string : strings) {
            stringBuilder.append("'").append(format(string, request)).append("',");
        }

        stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length()).append(")");
        return mappingBinder.parse(stringBuilder.toString());
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
     * @param builder     请求
     * @param proxyMethod 代理方法
     */
    private void doAddress(Request.RequestBuilder builder, ProxyMethod proxyMethod) {
        MappingAddress mappingAddress = proxyMethod.getMethod().getDeclaringClass().getDeclaredAnnotation(MappingAddress.class);
        if (null == mappingAddress) {
            return;
        }

        builder.invokeType(mappingAddress.invokeType());
        builder.readTimeout(mappingAddress.readTimeout());
        builder.connectTimeout(mappingAddress.connectTimeout());
        if (StringUtils.isNotBlank(mappingAddress.value())) {
            builder.address(mappingAddress.value());
        } else {
            String[] protocols = mappingConfig.getProtocol();
            String protocol = HTTP;
            if (!ArrayUtils.isEmpty(protocols)) {
                protocol = ArrayUtils.getIndex(protocols, 0);
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
        if (!ClassUtils.isVoid(mappingRequest.returnType())) {
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
