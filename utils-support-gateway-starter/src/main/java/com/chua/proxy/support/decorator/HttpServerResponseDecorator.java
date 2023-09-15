package com.chua.proxy.support.decorator;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.cookie.Cookie;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.netty.NettyOutbound;
import reactor.netty.http.server.HttpServerResponse;
import reactor.netty.http.server.WebsocketServerSpec;
import reactor.netty.http.websocket.WebsocketInbound;
import reactor.netty.http.websocket.WebsocketOutbound;
import reactor.util.annotation.NonNull;

import java.net.SocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * http服务器响应修饰器
 *
 * @author CH
 */
public class HttpServerResponseDecorator implements HttpServerResponse {

    private final HttpServerResponse delegator;

    private final AtomicBoolean beforeSendExecuted = new AtomicBoolean(false);

    public HttpServerResponseDecorator(@NonNull HttpServerResponse delegator) {
        Objects.requireNonNull(delegator);
        this.delegator = delegator;
    }

    @Override
    public @NonNull HttpServerResponse addCookie(@NonNull Cookie cookie) {
        delegator.addCookie(cookie);
        return this;
    }

    @Override
    public @NonNull HttpServerResponse addHeader(@NonNull CharSequence name, @NonNull CharSequence value) {
        delegator.addHeader(name, value);
        return this;
    }

    @Override
    public @NonNull HttpServerResponse chunkedTransfer(boolean chunked) {
        delegator.chunkedTransfer(chunked);
        return this;
    }

    @Override
    public @NonNull HttpServerResponse compression(boolean compress) {
        delegator.compression(compress);
        return this;
    }

    @Override
    public boolean hasSentHeaders() {
        return delegator.hasSentHeaders();
    }

    private void beforeSend() {
        if (beforeSendExecuted.compareAndSet(false, true)) {
            doBeforeSend();
        }
    }

    protected void doBeforeSend() {
    }

    @Override
    public @NonNull HttpServerResponse header(@NonNull CharSequence name, @NonNull CharSequence value) {
        delegator.header(name, value);
        return this;
    }

    @Override
    public @NonNull HttpServerResponse headers(@NonNull HttpHeaders headers) {
        delegator.headers(headers);
        return this;
    }

    @Override
    public @NonNull HttpServerResponse keepAlive(boolean keepAlive) {
        this.delegator.keepAlive(keepAlive);
        return this;
    }

    @Override
    public @NonNull HttpHeaders responseHeaders() {
        return delegator.responseHeaders();
    }

    @Override
    public @NonNull Mono<Void> send() {
        beforeSend();
        return delegator.send();
    }

    @Override
    public @NonNull NettyOutbound sendHeaders() {
        return delegator.sendHeaders();
    }

    @Override
    public @NonNull Mono<Void> sendNotFound() {
        beforeSend();
        return delegator.sendNotFound();
    }

    @Override
    public @NonNull Mono<Void> sendRedirect(@NonNull String location) {
        beforeSend();
        return delegator.sendRedirect(location);
    }

    @Override
    public @NonNull Mono<Void> sendWebsocket(
            @NonNull BiFunction<? super WebsocketInbound, ? super WebsocketOutbound, ? extends Publisher<Void>> websocketHandler,
            @NonNull WebsocketServerSpec websocketServerSpec
    ) {
        beforeSend();
        return delegator.sendWebsocket(websocketHandler, websocketServerSpec);
    }

    @Override
    public @NonNull HttpServerResponse sse() {
        delegator.sse();
        return this;
    }

    @Override
    public @NonNull HttpResponseStatus status() {
        return delegator.status();
    }

    @Override
    public @NonNull HttpServerResponse status(@NonNull HttpResponseStatus status) {
        delegator.status(status);
        return this;
    }

    @Override
    public @NonNull HttpServerResponse trailerHeaders(@NonNull Consumer<? super HttpHeaders> trailerHeaders) {
        delegator.trailerHeaders(trailerHeaders);
        return this;
    }

    @Override
    public @NonNull HttpServerResponse withConnection(@NonNull Consumer<? super Connection> withConnection) {
        delegator.withConnection(withConnection);
        return this;
    }

    @Override
    public @NonNull ByteBufAllocator alloc() {
        return delegator.alloc();
    }

    @Override
    public @NonNull NettyOutbound send(@NonNull Publisher<? extends ByteBuf> dataStream) {
        beforeSend();
        return delegator.send(dataStream);
    }

    @Override
    public @NonNull NettyOutbound send(
            @NonNull Publisher<? extends ByteBuf> dataStream,
            @NonNull Predicate<ByteBuf> predicate
    ) {
        beforeSend();
        return delegator.send(dataStream, predicate);
    }

    @Override
    public @NonNull NettyOutbound sendObject(@NonNull Object message) {
        beforeSend();
        return delegator.sendObject(message);
    }

    @Override
    public @NonNull NettyOutbound sendObject(@NonNull Publisher<?> dataStream, @NonNull Predicate<Object> predicate) {
        beforeSend();
        return delegator.sendObject(dataStream, predicate);
    }

    @Override
    public @NonNull <S> NettyOutbound sendUsing(
            @NonNull Callable<? extends S> sourceInput,
            @NonNull BiFunction<? super Connection, ? super S, ?> mappedInput,
            @NonNull Consumer<? super S> sourceCleanup
    ) {
        beforeSend();
        return delegator.sendUsing(sourceInput, mappedInput, sourceCleanup);
    }

    @Override
    public @NonNull Map<CharSequence, List<Cookie>> allCookies() {
        return delegator.allCookies();
    }

    @Override
    public @NonNull Map<CharSequence, Set<Cookie>> cookies() {
        return delegator.cookies();
    }

    @Override
    public @NonNull String fullPath() {
        return delegator.fullPath();
    }

    @Override
    public boolean isKeepAlive() {
        return delegator.isKeepAlive();
    }

    @Override
    public boolean isWebsocket() {
        return delegator.isWebsocket();
    }

    @Override
    public @NonNull HttpMethod method() {
        return delegator.method();
    }

    @Override
    public @NonNull String requestId() {
        return delegator.requestId();
    }

    @Override
    public @NonNull String uri() {
        return delegator.uri();
    }

    @Override
    public @NonNull HttpVersion version() {
        return delegator.version();
    }

    @Override
    public SocketAddress hostAddress() {
        return delegator.hostAddress();
    }

    @Override
    public SocketAddress connectionHostAddress() {
        return delegator.connectionHostAddress();
    }

    @Override
    public SocketAddress remoteAddress() {
        return delegator.remoteAddress();
    }

    @Override
    public SocketAddress connectionRemoteAddress() {
        return delegator.connectionRemoteAddress();
    }

    @Override
    public @NonNull String scheme() {
        return delegator.scheme();
    }

    @Override
    public @NonNull String connectionScheme() {
        return delegator.connectionScheme();
    }

    @Override
    public @NonNull String hostName() {
        return delegator.hostName();
    }

    @Override
    public int hostPort() {
        return delegator.hostPort();
    }

}
