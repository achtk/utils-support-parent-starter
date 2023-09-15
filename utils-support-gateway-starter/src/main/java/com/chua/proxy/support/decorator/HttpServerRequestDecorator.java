package com.chua.proxy.support.decorator;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.multipart.HttpData;
import reactor.core.publisher.Flux;
import reactor.netty.ByteBufFlux;
import reactor.netty.Connection;
import reactor.netty.http.server.HttpServerFormDecoderProvider;
import reactor.netty.http.server.HttpServerRequest;
import reactor.util.annotation.NonNull;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * http服务器请求修饰器
 *
 * @author CH
 */
public class HttpServerRequestDecorator implements HttpServerRequest {

    private final HttpServerRequest decorator;

    public HttpServerRequestDecorator(HttpServerRequest decorator) {
        Objects.requireNonNull(decorator);
        this.decorator = decorator;
    }

    @Override
    public @NonNull HttpServerRequest withConnection(@NonNull Consumer<? super Connection> withConnection) {
        return decorator.withConnection(withConnection);
    }

    @Override
    public String param(@NonNull CharSequence key) {
        return decorator.param(key);
    }

    @Override
    public Map<String, String> params() {
        return decorator.params();
    }

    @Override
    public @NonNull HttpServerRequest paramsResolver(@NonNull Function<? super String, Map<String, String>> paramsResolver) {
        return decorator.paramsResolver(paramsResolver);
    }

    @Override
    public boolean isFormUrlencoded() {
        return decorator.isFormUrlencoded();
    }

    @Override
    public boolean isMultipart() {
        return decorator.isMultipart();
    }

    @Override
    public @NonNull Flux<HttpData> receiveForm() {
        return decorator.receiveForm();
    }

    @Override
    public @NonNull Flux<HttpData> receiveForm(@NonNull Consumer<HttpServerFormDecoderProvider.Builder> formDecoderBuilder) {
        return decorator.receiveForm(formDecoderBuilder);
    }

    @Override
    public InetSocketAddress hostAddress() {
        return decorator.hostAddress();
    }

    @Override
    public SocketAddress connectionHostAddress() {
        return decorator.connectionHostAddress();
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return decorator.remoteAddress();
    }

    @Override
    public SocketAddress connectionRemoteAddress() {
        return decorator.connectionRemoteAddress();
    }

    @Override
    public @NonNull HttpHeaders requestHeaders() {
        return decorator.requestHeaders();
    }

    @Override
    public @NonNull String protocol() {
        return decorator.protocol();
    }

    @Override
    public @NonNull ZonedDateTime timestamp() {
        return decorator.timestamp();
    }

    @Override
    public @NonNull String scheme() {
        return decorator.scheme();
    }

    @Override
    public @NonNull String connectionScheme() {
        return decorator.connectionScheme();
    }

    @Override
    public @NonNull String hostName() {
        return decorator.hostName();
    }

    @Override
    public int hostPort() {
        return decorator.hostPort();
    }

    @Override
    public @NonNull ByteBufFlux receive() {
        return decorator.receive();
    }

    @Override
    public @NonNull Flux<?> receiveObject() {
        return decorator.receiveObject();
    }

    @Override
    public @NonNull Map<CharSequence, List<Cookie>> allCookies() {
        return decorator.allCookies();
    }

    @Override
    public @NonNull Map<CharSequence, Set<Cookie>> cookies() {
        return decorator.cookies();
    }

    @Override
    public @NonNull String fullPath() {
        return decorator.fullPath();
    }

    @Override
    public @NonNull String requestId() {
        return decorator.requestId();
    }

    @Override
    public boolean isKeepAlive() {
        return decorator.isKeepAlive();
    }

    @Override
    public boolean isWebsocket() {
        return decorator.isWebsocket();
    }

    @Override
    public @NonNull HttpMethod method() {
        return decorator.method();
    }

    @Override
    public @NonNull String uri() {
        return decorator.uri();
    }

    @Override
    public @NonNull HttpVersion version() {
        return decorator.version();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [delegator=" + decorator + "]";
    }

}
