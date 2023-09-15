package com.chua.proxy.support.endpoint;

import com.chua.common.support.json.Json;
import com.chua.common.support.utils.PathUtils;
import com.chua.proxy.support.event.Event;
import com.chua.proxy.support.event.EventPublisher;
import com.chua.proxy.support.event.NotificationEvent;
import com.chua.proxy.support.exchange.Exchange;
import com.chua.proxy.support.filter.Filter;
import com.chua.proxy.support.filter.FilterChain;
import com.chua.proxy.support.notification.Notification;
import com.chua.proxy.support.utils.ResponseUtils;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;


/**
 * 网关内部终结点
 *
 * @author CH
 */
@Slf4j
public class GatewayInternalEndpoint implements Filter {

    private final Map<String, Endpoint> endpoints = new HashMap<>();

    private final String contextPath;

    @Setter
    private EventPublisher<Event> eventPublisher;

    public GatewayInternalEndpoint(String contextPath) {
        this.contextPath = contextPath;
        endpoints.put("/notification", new NotificationReceiverEndpoint());
    }

    @Override
    public Mono<Void> filter(Exchange exchange, FilterChain chain) {
        String fullPath = exchange.getRequest().fullPath();
        String path = fullPath.substring(contextPath.length());
        String normalizePath = PathUtils.normalize(path);
        if (!endpoints.containsKey(normalizePath)) {
            return ResponseUtils.sendNotFound(exchange.getResponse());
        }
        return endpoints.get(normalizePath).handle(exchange);
    }

    interface Endpoint {
        /**
         * 手柄
         *
         * @param exchange 交换
         * @return {@link Mono}<{@link Void}>
         */
        Mono<Void> handle(Exchange exchange);
    }

    class NotificationReceiverEndpoint implements Endpoint {

        @Override
        public Mono<Void> handle(Exchange exchange) {
            return exchange.getRequest().receive().aggregate().asString().flatMap(jsonStr -> {
                Notification notification;
                try {
                    notification = Json.fromJson(jsonStr, Notification.class);
                } catch (Exception exception) {
                    log.error("Failed to deserialize to Notification.class", exception);
                    return ResponseUtils.sendStatus(exchange.getResponse(), HttpResponseStatus.BAD_REQUEST);
                }
                try {
                    handleNotification(notification);
                } catch (Exception exception) {
                    // just in case
                    log.info("Exception while handle notification from dashboard.", exception);
                }

                return ResponseUtils.sendOk(exchange.getResponse());
            });
        }

        private void handleNotification(Notification notification) {
            eventPublisher.publishEvent(new NotificationEvent(notification));
        }

    }

}
