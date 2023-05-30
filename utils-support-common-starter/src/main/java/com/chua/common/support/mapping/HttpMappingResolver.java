package com.chua.common.support.mapping;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.http.HttpClient;
import com.chua.common.support.http.HttpClientBuilder;
import com.chua.common.support.http.HttpClientInvoker;
import com.chua.common.support.http.HttpResponse;
import com.chua.common.support.lang.proxy.DelegateMethodIntercept;
import com.chua.common.support.lang.proxy.ProxyUtils;
import com.chua.common.support.lang.proxy.plugin.ProxyPlugin;
import com.chua.common.support.mapping.annotation.MappingPlugins;
import com.chua.common.support.mapping.builder.Request;
import com.chua.common.support.mapping.builder.Response;
import com.chua.common.support.placeholder.PlaceholderSupport;
import com.chua.common.support.placeholder.StringValuePropertyResolver;
import com.chua.common.support.reflection.describe.MethodDescribe;
import com.chua.common.support.reflection.marker.Bench;
import com.chua.common.support.reflection.marker.Marker;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.StringUtils;

import java.lang.reflect.Method;

/**
 * mapping
 *
 * @author CH
 */
@Spi("http")
public final class HttpMappingResolver implements MappingResolver {
    private Object bean;

    @Override
    public <T> T create(Class<T> target) {
        Marker marker = Marker.of(target);
        T proxy = ProxyUtils.newProxy(target, new DelegateMethodIntercept<>(target, (proxyMethod) -> {
            Method method = proxyMethod.getMethod();
            if (method.isDefault()) {
                return ClassUtils.invokeMethod(method, proxyMethod.getProxy(), proxyMethod.getArgs());
            }

            StringValuePropertyResolver propertyResolver = new StringValuePropertyResolver(new PlaceholderSupport());

            Request request = new Request(bean, target, marker, propertyResolver, proxyMethod);
            HttpClientBuilder httpClientBuilder = HttpClient.newHttpMethod(request.getHttpMethod());
            httpClientBuilder.connectTimout(request.getTimeout())
                    .header(request.getHeader());

            httpClientBuilder.url(propertyResolver.resolvePlaceholders(StringUtils.endWithMove(
                    request.getBalance().selectNode().getContent(), "/") + request.getPath()));

            httpClientBuilder.body(request.getRequestBody());

            HttpClientInvoker invoker = httpClientBuilder.newInvoker();
            HttpResponse execute = invoker.execute();
            int code = execute.code();
            if(200 != code) {
                request.doFilter(proxyMethod);
                throw new RuntimeException(execute.content(String.class));
            }
            return new Response(execute.content(), request).getValue();
        }));

        this.bean = ProxyUtils.newProxy(target, new DelegateMethodIntercept<>(target, (proxyMethod) -> {
            Bench bench = marker.createBench(MethodDescribe.builder().method(proxyMethod.getMethod()).build());
            return bench.executeBean(proxy, proxyMethod.getArgs(), proxyMethod.getPlugins()).getValue();
        }), createPlugin(target));
        return (T) bean;
    }

    private ProxyPlugin[] createPlugin(Class<?> target) {
        MappingPlugins mappingPlugins = target.getDeclaredAnnotation(MappingPlugins.class);
        if(null == mappingPlugins) {
            return new ProxyPlugin[0];
        }

        ServiceProvider<ProxyPlugin> serviceProvider = ServiceProvider.of(ProxyPlugin.class);
        String[] value = mappingPlugins.value();
        ProxyPlugin[] rs = new ProxyPlugin[value.length];
        for (int i = 0; i < value.length; i++) {
            String s = value[i];
            rs[i] = serviceProvider.getNewExtension(s);
        }

        return rs;
    }


}
