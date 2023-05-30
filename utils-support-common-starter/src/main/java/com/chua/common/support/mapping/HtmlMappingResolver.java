package com.chua.common.support.mapping;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.crawler.JsoupUtil;
import com.chua.common.support.crawler.annotations.XpathQuery;
import com.chua.common.support.crawler.process.PageParserProcessor;
import com.chua.common.support.crawler.request.Request;
import com.chua.common.support.jsoup.nodes.Document;
import com.chua.common.support.jsoup.nodes.Element;
import com.chua.common.support.jsoup.select.Elements;
import com.chua.common.support.lang.profile.Profile;
import com.chua.common.support.lang.proxy.DelegateMethodIntercept;
import com.chua.common.support.lang.proxy.ProxyUtils;
import com.chua.common.support.lang.robin.Robin;
import com.chua.common.support.log.Log;
import com.chua.common.support.mapping.annotation.MappingAddress;
import com.chua.common.support.mapping.annotation.MappingRequest;
import com.chua.common.support.mapping.annotation.MappingResponse;
import com.chua.common.support.reflection.describe.MethodDescribe;
import com.chua.common.support.reflection.marker.Bench;
import com.chua.common.support.reflection.marker.Marker;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.ClassUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import static com.chua.common.support.constant.NumberConstant.DEFAULT_INITIAL_CAPACITY;
import static com.chua.common.support.jsoup.nodes.Document.OutputSettings.Syntax.html;

/**
 * html解析
 * @author CH
 */
@Spi("jsoup")
public class HtmlMappingResolver implements MappingResolver {
    private Object bean;

    private Profile profile;

    public HtmlMappingResolver(Profile profile) {
        this.profile = profile;
    }

    private static final Log log = Log.getLogger(MappingResolver.class);

    @Override
    public <T> T create(Class<T> target) {
        Marker marker = Marker.of(target);
        T proxy = ProxyUtils.newProxy(target, new DelegateMethodIntercept<>(target, (proxyMethod) -> {
            Method method = proxyMethod.getMethod();
            if (method.isDefault()) {
                return ClassUtils.invokeMethod(method, proxyMethod.getProxy(), proxyMethod.getArgs());
            }

            String value = null;
            MappingRequest request = method.getDeclaredAnnotation(MappingRequest.class);
            if(null == request) {
                XpathQuery pageVoSelect = method.getAnnotation(XpathQuery.class);
                value = (pageVoSelect != null && pageVoSelect.value() != null && pageVoSelect.value().trim().length() > 0) ? pageVoSelect.value() : "html";
            } else {
                value = request.value();
            }

            Document document = getDocument(method);
            Elements elements = document.selectXpath(value);
            return parse(method, elements);
        }));

        this.bean = ProxyUtils.newProxy(target, new DelegateMethodIntercept<>(target, (proxyMethod) -> {
            Bench bench = marker.createBench(MethodDescribe.builder()
                    .method(proxyMethod.getMethod())
                    .build());
            return bench.executeBean(proxy, proxyMethod.getArgs()).getValue();
        }));
        return (T) bean;
    }

    private Object parse(Method method, Elements elements) {
        Class<?> returnType = ClassUtils.getActualType(method.getReturnType());

        if(Collection.class.isAssignableFrom(returnType)) {
            MappingResponse mappingResponse = method.getDeclaredAnnotation(MappingResponse.class);
            if(null != mappingResponse && null != mappingResponse.target()) {
                returnType = mappingResponse.target();
            }
        }

        List<Object> rs = new LinkedList<>();
        for (Element pageVoElement : elements) {
            Field[] fields = returnType.getDeclaredFields();
            Object pageVo;
            try {
                pageVo = returnType.newInstance();
            } catch (Exception e) {
                return null;
            }
            for (Field field : fields) {
                PageParserProcessor.parseFieldAnnotations(pageVo, pageVoElement, field);

            }
            rs.add(pageVo);
        }
        return Converter.convertIfNecessary(rs, method.getReturnType());
    }

    @SuppressWarnings("ALL")
    private Document getDocument(Method method) {
        MappingAddress mappingAddress = method.getDeclaringClass().getDeclaredAnnotation(MappingAddress.class);
        if(null != mappingAddress) {
            String address = getAddress(mappingAddress);
            return JsoupUtil.load(Request.builder().url(address).timeout((int) mappingAddress.timeout()).build());
        }

        throw new IllegalArgumentException("url not found");

    }

    @SuppressWarnings("ALL")
    private String getAddress(MappingAddress mappingAddress) {
        Set<String> address = new LinkedHashSet<>();
        address.addAll(Arrays.asList(mappingAddress.value()));
        String balance = mappingAddress.balance();
        Robin robin = ServiceProvider.of(Robin.class).getNewExtension(balance);
        Robin robin1 = robin.create();
        robin1.addNode(address);
        return profile.resolvePlaceholders(robin1.selectNode().getContent().toString());

    }
}
