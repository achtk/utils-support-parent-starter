package com.chua.common.support.mapping.builder;

import com.chua.common.support.bean.BeanUtils;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.function.Splitter;
import com.chua.common.support.http.HttpHeader;
import com.chua.common.support.http.HttpMethod;
import com.chua.common.support.lang.proxy.ProxyMethod;
import com.chua.common.support.lang.robin.Robin;
import com.chua.common.support.mapping.annotation.*;
import com.chua.common.support.mapping.condition.MappingCondition;
import com.chua.common.support.mapping.filter.MappingFilter;
import com.chua.common.support.mapping.value.MappingValue;
import com.chua.common.support.placeholder.MapMixSystemPlaceholderResolver;
import com.chua.common.support.placeholder.StringValuePropertyResolver;
import com.chua.common.support.reflection.describe.MethodDescribe;
import com.chua.common.support.reflection.marker.Bench;
import com.chua.common.support.reflection.marker.Marker;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.unit.name.NamingCase;
import com.chua.common.support.utils.ArrayUtils;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.NetAddress;
import com.chua.common.support.utils.StringUtils;
import lombok.Data;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

import static com.chua.common.support.constant.NumberConstant.SECOND;
import static com.chua.common.support.http.HttpMethod.GET;

/**
 * 请求
 * @author CH
 */
@Data
public class Request {

    private final MappingFilter filter;
    private final MappingResponse mappingResponse;
    private Object bean;

    private long timeout;
    private final Class<?> target;
    private final StringValuePropertyResolver propertyResolver;
    private final ProxyMethod proxyMethod;
    private final MappingRequest mappingRequest;
    private final Method method;
    private final String path;
    private Marker marker;
    private final Object[] args;
    private Robin<String> balance;

    private HttpMethod httpMethod;

    private NetAddress netAddress;

    private HttpHeader header = new HttpHeader();

    private Map<String, Object> body = new LinkedHashMap<>();

    private Map<String, Object> requestBody = new LinkedHashMap<>();
    public Request(Object bean, Class<?> target, Marker marker, StringValuePropertyResolver propertyResolver, ProxyMethod proxyMethod) {
        this.bean = bean;
        this.target = target;
        this.marker = marker;
        this.args = proxyMethod.getArgs();
        this.propertyResolver = propertyResolver;
        this.proxyMethod = proxyMethod;
        this.method = proxyMethod.getMethod();
        this.mappingRequest = method.getDeclaredAnnotation(MappingRequest.class);
        this.mappingResponse = method.getDeclaredAnnotation(MappingResponse.class);
        this.initialResolver();
        this.balance = createBalance(proxyMethod);
        this.httpMethod = createHttpMethod();
        this.path = createPath();
        this.header = createHeader();
        this.filter = createFilter();
    }

    private MappingFilter createFilter() {
        if(null == mappingResponse) {
            return null;
        }

        Class<? extends MappingFilter> filter = mappingResponse.filter();
        if(filter.isInterface()) {
            return null;
        }

        return ClassUtils.forObject(filter, MappingFilter.class);
    }

    /**
     * 初始化
     */
    private void initialResolver() {
        propertyResolver.getPlaceholderSupport().setResolver(
                new MapMixSystemPlaceholderResolver(body));

        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            doAnalysis(i, parameter);
        }

    }

    /**
     * 解析参数
     *
     * @param index     索引
     * @param parameter 字段
     */
    private void doAnalysis(int index, Parameter parameter) {
        String name = getName(parameter);
        MappingParam mappingParam = parameter.getDeclaredAnnotation(MappingParam.class);

        Object arg = args[index];
        if(null == arg && null != mappingParam) {
            arg = mappingParam.defaultValue();
        }

        if (null == arg) {
            if(null != mappingParam) {
                requestBody.put(name, null);
            }
            return;
        }

        arg = arg instanceof String ? propertyResolver.resolvePlaceholders(arg.toString()) : arg;

        requestBody.put(name, arg);
        body.put(name, arg);
        body.put(parameter.getName(), arg);
        propertyResolver.add(name, arg);
        propertyResolver.add(parameter.getName(), arg);

    }

    /**
     * 获取字段名称
     *
     * @param parameter 参数
     * @return 名称
     */
    private String getName(Parameter parameter) {
        String name = parameter.getName();
        MappingParam mappingParam = parameter.getDeclaredAnnotation(MappingParam.class);
        if (null != mappingParam) {
            return Optional.ofNullable(mappingParam.value()).orElse(name);
        }

        return name;
    }

    /**
     * header
     * @return header
     */
    private HttpHeader createHeader() {
        HttpHeader header = new HttpHeader();
        header.addHeader("Accept", "*/*");

        doAnalysisType();
        doAnalysisMethod();
        doAnalysisParameter();
        doAnalysisResponse();

        return header;
    }
    /**
     * 解析类型
     */
    private void doAnalysisParameter() {
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            doAnalysisHeader(header, i, parameter);
        }
    }

    /**
     * 解析类型
     */
    private void doAnalysisResponse() {
        MappingResponse mappingResponse = method.getDeclaredAnnotation(MappingResponse.class);
        if (null != mappingResponse && mappingResponse.isJson()) {
            header.addHeader("Content-Type", "application/json");
        }
    }

    /**
     * 解析类型
     */
    private void doAnalysisMethod() {
        MappingHeader[] headers = method.getDeclaredAnnotationsByType(MappingHeader.class);
        if (ArrayUtils.isEmpty(headers)) {
            return;
        }

        for (MappingHeader httpHeader : headers) {
            doAnalysisHeader(header, httpHeader, null);
        }
    }

    /**
     * 解析类型
     */
    private void doAnalysisType() {
        MappingHeader[] headers = target.getDeclaredAnnotationsByType(MappingHeader.class);
        if (ArrayUtils.isEmpty(headers)) {
            return;
        }

        for (MappingHeader httpHeader : headers) {
            doAnalysisHeader(header, httpHeader, null);
        }
    }

    /**
     * 解析参数上的消息头
     *
     * @param header    结果
     * @param index     索引
     * @param parameter 参数
     */
    private void doAnalysisHeader(HttpHeader header, int index, Parameter parameter) {
        MappingHeader mappingHeader = parameter.getDeclaredAnnotation(MappingHeader.class);
        if (null == mappingHeader) {
            return;
        }

        doAnalysisHeader(header, mappingHeader, Converter.convertIfNecessary(args[index], String.class));
    }

    /**
     * 分析消息头
     *
     * @param header     消息头
     * @param httpHeader 注解
     */
    private void doAnalysisHeader(HttpHeader header, MappingHeader httpHeader, String value) {
        Class<?> aClass = httpHeader.conditionType();
        if (null != aClass && !aClass.isInterface()) {
            MappingCondition mappingCondition = (MappingCondition) ClassUtils.forObject(aClass, MappingCondition.class);
            if (null != mappingCondition) {
                String resolve = mappingCondition.resolve(propertyResolver, httpHeader.name(), path);
                header.addHeader(httpHeader.name(), resolve);
                propertyResolver.add(httpHeader.name(), resolve);
                return;
            }
        }

        String script = httpHeader.script();
        if (!StringUtils.isNullOrEmpty(script)) {
            String value1 = analysisScript(script);
            header.addHeader(httpHeader.name(), value1);
            propertyResolver.add(httpHeader.name(), value1);
            return;
        }

        String newValue = value;
        if (StringUtils.isNullOrEmpty(value)) {
            newValue = httpHeader.value();
        }

        String placeholders = propertyResolver.resolvePlaceholders(newValue);
        if (null == placeholders) {
            return;
        }

        header.addHeader(httpHeader.name(), placeholders);
    }

    /**
     * 分析映射数据
     *
     * @param script 脚本
     * @return 数据
     */
    private String analysisScript(String script) {
        MethodDescribe methodDescribe = MethodDescribe.of(script);
        String methodName = methodDescribe.name();
        List<String> strings = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(methodDescribe.ext());
        List<Object> args = new LinkedList<>();
        if (!strings.isEmpty()) {
            for (String string : strings) {
                args.add(propertyResolver.resolvePlaceholders(string));
            }
        }
        Bench bench = marker.createBench(MethodDescribe.builder().name(methodName).build());
        return bench.executeBean(bean, args.toArray()).toString();
    }
    /**
     * 地址
     * @return 地址
     */
    private String createPath() {
        if (null == mappingRequest) {
            return NamingCase.toCamelHyphen(method.getName()).replace("-", "/");
        }

        String path = "";
        String value = this.mappingRequest.value();
        String[] split = value.split("\\s+", 2);
        if (split.length == 1) {
            path = StringUtils.endWithMove(value, "/");
        } else {
            path += StringUtils.endWithMove(split[1], "/");
        }
        return StringUtils.startWithAppend(path, "/");
    }

    /**
     * 方法类型
     * @return 方法类型
     */
    private HttpMethod createHttpMethod() {

        if(null == mappingRequest) {
            return GET;
        }

        String value = mappingRequest.value();
        String[] split = value.split("\\s+");
        if (split.length == SECOND) {
            return HttpMethod.valueOf(split[0].toUpperCase());
        }
        this.timeout = mappingRequest.timeout();
        return GET;

    }


    /**
     * 创建平衡器
     *
     * @param proxyMethod 请求
     * @return 平衡器
     */
    private Robin<String> createBalance(ProxyMethod proxyMethod) {
        MappingAddress mappingAddress = target.getDeclaredAnnotation(MappingAddress.class);
        Robin<String> stringRobin = ServiceProvider.of(Robin.class).getNewExtension(mappingAddress.balance());

        String[] value = mappingAddress.value();
        stringRobin.addNode(value);

        this.timeout = mappingAddress.timeout();
        return stringRobin;
    }

    /**
     * 过滤数据
     * @param proxyMethod 方法
     * @return 结果
     */
    public Object doFilter(ProxyMethod proxyMethod) {
        if(null == filter) {
            return null;
        }

        Object object = filter.doFilter(proxyMethod.getMethod().getName(), new MappingValue(proxyMethod.getArgs()));
        return BeanUtils.copyProperties(object, proxyMethod.getMethod().getReturnType());
    }

    /**
     * 保存值
     * @param rs 值
     */
    public void withFilter(Object rs) {
        if(null == filter) {
            return;
        }

        filter.doCache(rs);
    }
}
