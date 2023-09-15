package com.chua.proxy.support.route.locator;

import com.chua.proxy.support.route.Route;
import com.chua.proxy.support.route.RouteEvent;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.util.Objects;

/**
 * 可更新路线定位器
 *
 * @author CH
 */
@Slf4j
public abstract class UpdatableRouteLocator extends ManageableRouteLocator {

    /**
     * 获取路由更改
     *
     * @return {@link Flux}<{@link RouteEvent}>
     */
    protected abstract Flux<RouteEvent> fetchRouteChange();

    public synchronized void update() {
        doUpdate();
    }

    protected void doUpdate() {
        fetchRouteChange().doOnNext(routeEvent -> {
            if (Objects.isNull(routeEvent.getRouteId())) {
                // 可以通过设置routeId为null来标识是一个无效事件
                return;
            }
            if (routeEvent.isDelete()) {
                Route route = removeRouteById(routeEvent.getRouteId());
                log.info("{} remove route with id = {}", this.getClass().getSimpleName(), route.getId());
            } else {
                log.info("Add or update a route with id = {}", routeEvent.getRouteId());
                addRoute(routeEvent.getRoute());
            }
        }).subscribe();
    }

}
