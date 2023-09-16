package com.chua.proxy.support.request;

import com.chua.common.support.http.HttpHeader;
import com.chua.common.support.request.Attribute;
import com.chua.common.support.request.WebServerRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * web服务器请求
 *
 * @author CH
 * @since 2023/09/16
 */
@Data
public class NettyWebServerRequest implements WebServerRequest {

    private final ChannelHandlerContext ctx;
    private FullHttpRequest request;

    private final Map<String, Object> attribute = new LinkedHashMap<>();

    public NettyWebServerRequest(ChannelHandlerContext ctx, FullHttpRequest msg) {
        this.ctx = ctx;
        request = msg;
    }

    /**
     * 获取属性
     *
     * @param name 名称
     * @return {@link T}
     */
    @SuppressWarnings("ALL")
    public <T> void addAttribute(String name, T value) {
        attribute.put(name, value);
    }

    /**
     * 获取属性
     *
     * @param name 名称
     * @return {@link T}
     */
    @SuppressWarnings("ALL")
    public <T> T getAttribute(String name) {
        return (T) attribute.get(name);
    }

    /**
     * 获取属性
     *
     * @return {@link Attribute}
     */
    public Attribute getAttribute() {
        return new Attribute.MapAttribute(attribute);
    }

    @Override
    public <T> T getChannel() {
        return (T) ctx;
    }

    @Override
    public String uri() {
        return request.uri();
    }

    @Override
    public HttpHeader headers() {
        return new HttpHeader(request.headers());
    }
}
