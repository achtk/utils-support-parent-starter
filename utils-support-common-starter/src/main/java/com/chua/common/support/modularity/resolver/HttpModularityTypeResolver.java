package com.chua.common.support.modularity.resolver;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.annotations.SpiOption;
import com.chua.common.support.collection.MsgHeaders;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.function.Splitter;
import com.chua.common.support.http.*;
import com.chua.common.support.json.Json;
import com.chua.common.support.lang.any.Any;
import com.chua.common.support.lang.expression.parser.ExpressionParser;
import com.chua.common.support.modularity.Modularity;
import com.chua.common.support.modularity.ModularityFactory;
import com.chua.common.support.modularity.ModularityResult;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.unit.TimeUnit;
import com.chua.common.support.utils.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 模块类型解析器
 *
 * @author CH
 */
@Spi("http")
@SuppressWarnings("ALL")
@SpiOption("http请求处理器")
public class HttpModularityTypeResolver implements ModularityTypeResolver {

    @Override
    public ModularityResult execute(ModularityFactory modularityFactory, Modularity modularity, Map<String, Object> args) {
        String method = getMethod(modularity);
        MsgHeaders msgHeaders = getHeaders(modularity, args);
        String url = getUrl(modularity, args, msgHeaders);
        long connectTimeout = getConnectTimeout(modularity);
        Map<String, Object> body = analysisBody(modularity, args, msgHeaders);
        HttpClientBuilder httpClientBuilder = HttpClient.newHttpMethod(HttpMethod.valueOf(method.toUpperCase()));
        HttpClientInvoker newInvoker = httpClientBuilder.body(args).header(msgHeaders.toHttpHeader())
                .url(url)
                .connectTimout(connectTimeout)
                .retry(3)
                .newInvoker();
        HttpResponse execute = newInvoker.execute();
        ModularityResult.ModularityResultBuilder builder = ModularityResult.builder().code(execute.code() + "").msg(execute.message());
        if(!modularity.hasResponseType()) {
            return builder.data(execute.content()).build();
        }
        String content = newInvoker.execute().content(String.class);
        return builder.data(parse(modularity, content)).build();
    }

    private Map<String, Object> analysisBody(Modularity modularity, Map<String, Object> args, MsgHeaders msgHeaders) {
        Map<String, Object> param = new ConcurrentHashMap<>();
        String moduleRequest = modularity.getModuleRequest();
        ExpressionParser parser = ServiceProvider.of(ExpressionParser.class).getNewExtension("spring");
        parser.setVariable("header", msgHeaders);
        if(StringUtils.isNotEmpty(moduleRequest)) {
            parser.setVariable(args);
            Map<String, String> stringStringMap = Splitter.on(";").withKeyValueSeparator(":").split(moduleRequest);
            for (Map.Entry<String, String> entry : stringStringMap.entrySet()) {
                String value = entry.getValue();
                if(value != null) {
                    value = parser.parse(value).getStringValue();
                }
                param.put(parser.parse(entry.getKey()).getStringValue(), value);
            }

            return param;
        }

        for (Map.Entry<String, Object> entry : args.entrySet()) {
            Object value = entry.getValue();
            if(value != null) {
                if(value instanceof String) {
                    value = parser.parse(value.toString()).getStringValue();
                }
            }
            param.put(parser.parse(entry.getKey()).getStringValue(), value);
        }

        return param;
    }

    /**
     * 解析结果
     * @param modularity 模块
     * @param content 响应
     * @return 结果
     * @param <T> 类型
     */
    private <T> T parse(Modularity modularity, String content) {
        Any any = Converter.convertIfNecessary(content, Any.class);
        if(any.isNull()) {
            return (T) content;
        }

        String moduleResponse = modularity.getModuleResponse();
        ExpressionParser parser = ServiceProvider.of(ExpressionParser.class).getNewExtension("spring");
        if(any.isMap()) {
            parser.setVariable((Map<String, Object>) any.getValue());
        } else {
            parser.setVariable("ew", any.getValue());
        }

        return (T) parser.parse(moduleResponse).getValue();
    }

    /**
     * 处理消息头
     * @param modularity 模块
     * @param args 参数
     * @return 消息头
     */
    private MsgHeaders getHeaders(Modularity modularity, Map<String, Object> args) {
        MsgHeaders msgHeaders = new MsgHeaders();
        String moduleHeader = modularity.getModuleHeader();
        if(StringUtils.isEmpty(moduleHeader)) {
            return msgHeaders;
        }
        ExpressionParser parser = ServiceProvider.of(ExpressionParser.class).getNewExtension("spring");

        List<MsgHeaders.MsgHeader> headers = Converter.convertIfNecessaryList(moduleHeader, MsgHeaders.MsgHeader.class);
        for (MsgHeaders.MsgHeader header : headers) {
            msgHeaders.add(parser.parse(header.getName()).getStringValue(), parser.parse(header.getValue()).getStringValue());
        }

        return msgHeaders;
    }

    /**
     * 连接超时时间
     * @param modularity 模块
     * @return 超时时间
     */
    private long getConnectTimeout(Modularity modularity) {
        String connectTimeout = modularity.getModuleConnectionTimeout();
        return StringUtils.isEmpty(connectTimeout) ? 0 : TimeUnit.parse(connectTimeout).toMillis();
    }

    /**
     * 地址
     *
     * @param modularity 模块
     * @param args       参数
     * @param msgHeaders
     * @return 地址
     */
    private String getUrl(Modularity modularity, Map<String, Object> args, MsgHeaders msgHeaders) {
        ExpressionParser parser = ServiceProvider.of(ExpressionParser.class).getNewExtension("spring");
        parser.setVariable(args);
        parser.setVariable("header", msgHeaders);
        String moduleScript = modularity.getModuleScript();
        String[] split = moduleScript.split("\\s+", 2);
        String url = moduleScript;
        if (split.length != 0) {
            url = split[1];
        }

        return parser.parse(url).getStringValue();
    }

    /**
     * http方法
     *
     * @param modularity 模块
     * @return 方法
     */
    private String getMethod(Modularity modularity) {
        String moduleScript = modularity.getModuleScript();
        String[] split = moduleScript.split("\\s+", 2);
        if (split.length == 0) {
            return "GET";
        }

        return split[0];
    }
}
