package com.chua.proxy.support.route.locator;

import com.chua.common.support.json.Json;
import com.chua.proxy.support.definition.IdRouteDefinition;
import com.chua.proxy.support.event.*;
import com.chua.proxy.support.notification.Notification;
import com.chua.proxy.support.route.Route;
import com.chua.proxy.support.route.RouteConverter;
import com.chua.proxy.support.route.RouteEvent;
import com.chua.proxy.support.utils.Shuck;
import com.chua.proxy.support.utils.UriBuilder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;


/**
 * 从仪表板拉出路线定位器
 *
 * @author CH
 */
@Slf4j
public class PullFromDashboardRouteLocator extends AsyncUpdatableRouteLocator implements EventListener<Event> {

    private static final String PARAMETER_ENV_ID = "envId";

    private static final String PARAMETER_SEQ = "seq";

    @SuppressWarnings("java:S1075")
    private static final String SYNC_ROUTE_PATH = "/gateway/_sync";

    private final HttpClient httpClient;

    @Getter
    private final String dashboardAddress;

    @Getter
    private final String dashboardRouteSyncEndpoint;

    @Getter
    private final String dashboardAuthKey;

    @Getter
    private final String environmentId;

    private long latestSequenceNum = 0L;

    private final UriBuilder uriBuilder;

    @Setter
    private EventPublisher<Event> eventPublisher;

    public PullFromDashboardRouteLocator(String dashboardAddress,
                                         String dashboardApiContextPath,
                                         String dashboardAuthKey,
                                         String environmentId) {
        this.dashboardAddress = dashboardAddress;
        this.dashboardRouteSyncEndpoint = dashboardApiContextPath + SYNC_ROUTE_PATH;
        this.dashboardAuthKey = dashboardAuthKey;
        this.environmentId = environmentId;
        httpClient = HttpClient.create();
        try {
            uriBuilder = new UriBuilder(dashboardAddress);
            uriBuilder.path(dashboardRouteSyncEndpoint);
            uriBuilder.queryParam(PARAMETER_ENV_ID, this.environmentId);
            httpClient.baseUrl(uriBuilder.build().toString());
        } catch (URISyntaxException exception) {
            throw new RuntimeException("Dashboard address is not validated uri.", exception);
        }
    }

    @Override
    protected Flux<RouteEvent> fetchRouteChange() {
        return fetchRouteDefinitions()
                .doOnNext(idRouteDefinition -> latestSequenceNum = Math.max(latestSequenceNum, idRouteDefinition.getSeqNum()))
                .map(idRouteDefinition -> {
                    RouteEvent routeEvent = new RouteEvent();
                    routeEvent.setRouteId(idRouteDefinition.getId());

                    if (Objects.isNull(idRouteDefinition.getRouteDefinition())) {
                        // 没有routeDefinition说明是下线了
                        routeEvent.setDelete(true);
                    } else {
                        routeEvent.setDelete(false);
                        try {
                            Route route = RouteConverter.convertRouteDefinition(idRouteDefinition);
                            routeEvent.setRoute(route);
                        } catch (Exception exception) {
                            log.error("Failed to convert a route definition to route.", exception);
                            routeEvent.setRouteId(null);
                        }
                    }
                    return routeEvent;
                }).doOnComplete(() -> eventPublisher.publishEvent(new ApiOpSeqUpdateEvent(latestSequenceNum)));
    }

    private Flux<IdRouteDefinition> fetchRouteDefinitions() {
        URI uri;
        try {
            uri = uriBuilder.clearQueryParam(PARAMETER_SEQ).queryParam(PARAMETER_SEQ, this.latestSequenceNum + "").build();
        } catch (URISyntaxException exception) {
            throw new RuntimeException(exception);
        }
        return httpClient.get().uri(uri).responseContent().aggregate().asInputStream()
                .onErrorResume(throwable -> {
                    log.error("Failed to fetch route changes from uri: {}", uri);
                    return Mono.empty();
                }).map(inputStream -> {
                    SyncRouteResp syncRouteResp = Json.fromJson(inputStream, SyncRouteResp.class);
                    List<IdRouteDefinition> list = syncRouteResp.getData();
                    if (!list.isEmpty()) {
                        log.info("Fetched {} route definitions from {}", list.size(), uri);
                    }
                    return list;
                }).flatMapMany((Function<List<IdRouteDefinition>, Flux<IdRouteDefinition>>) Flux::fromIterable);
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof NotificationEvent) {
            NotificationEvent notificationEvent = (NotificationEvent) event;
            Notification notification = notificationEvent.getNotification();
            if (notification.isApiUpdated()) {
                log.info("Received api updated notification.");
                update();
            }
        } else if (event instanceof ApiOpSeqBehindEvent) {
            log.info("Api operation sequence is behind, going to sync from dashboard.");
            update();
        }
    }

    static class SyncRouteResp extends Shuck<List<IdRouteDefinition>> {

        @Override
        public List<IdRouteDefinition> getData() {
            if (getCode() != Shuck.CODE_OK) {
                log.error("Sync route definitions request returned not ok status, something maybe wrong.");
                return Collections.emptyList();
            }
            // 保证不能返回null
            List<IdRouteDefinition> data = super.getData();
            if (Objects.isNull(data)) {
                log.error("Sync route definitions request returned empty data, something maybe wrong.");
                return Collections.emptyList();
            }
            return data;
        }

    }

}
