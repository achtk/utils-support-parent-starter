package com.chua.proxy.support.utils;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerResponse;

/**
 * 响应util
 *
 * @author CH
 */
public class ResponseUtils {

    private ResponseUtils() {
    }

    public static Mono<Void> send(HttpServerResponse response, String message) {
        return send(response, HttpResponseStatus.OK, Shuck.CODE_OK, message);
    }

    public static Mono<Void> send(HttpServerResponse response, int code, String message) {
        return send(response, HttpResponseStatus.OK, code, message);
    }

    public static Mono<Void> send(HttpServerResponse response, HttpResponseStatus httpResponseStatus, int code, String message) {
        String json = Shuck.jsonOf(code, message);
        return send(response, httpResponseStatus, HttpHeaderValues.APPLICATION_JSON.toString(), json);
    }

    public static Mono<Void> send(HttpServerResponse response, HttpResponseStatus httpResponseStatus, String contentType, String content) {
        return response.status(httpResponseStatus)
                .header(HttpHeaderNames.CONTENT_TYPE, contentType)
                .sendString(Mono.just(content))
                .then();
    }

    public static Mono<Void> sendRawJson(HttpServerResponse response, String jsonMessage) {
        return send(response, HttpResponseStatus.OK, HttpHeaderValues.APPLICATION_JSON.toString(), jsonMessage);
    }

    public static Mono<Void> sendStatus(HttpServerResponse response, HttpResponseStatus status) {
        return send(response, status, status.code(), status.reasonPhrase());
    }

    public static Mono<Void> sendStatus(HttpServerResponse response, HttpResponseStatus status, String message) {
        return send(response, status, status.code(), message);
    }

    public static Mono<Void> sendOk(HttpServerResponse response) {
        return send(response, Shuck.CODE_OK, Shuck.MESSAGE_OK);
    }

    public static Mono<Void> sendError(HttpServerResponse response) {
        return sendStatus(response, HttpResponseStatus.INTERNAL_SERVER_ERROR);
    }

    public static Mono<Void> sendError(HttpServerResponse response, String message) {
        return send(response, HttpResponseStatus.INTERNAL_SERVER_ERROR, Shuck.CODE_ERROR, message);
    }

    public static Mono<Void> sendNotFound(HttpServerResponse response) {
        return sendStatus(response, HttpResponseStatus.NOT_FOUND);
    }

}
