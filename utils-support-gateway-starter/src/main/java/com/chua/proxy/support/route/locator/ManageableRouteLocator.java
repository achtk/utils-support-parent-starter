package com.chua.proxy.support.route.locator;

import com.chua.common.support.matcher.AntPathMatcher;
import com.chua.common.support.matcher.PathMatcher;
import com.chua.common.support.utils.PathUtils;
import com.chua.proxy.support.route.Route;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 可管理路线定位器
 *
 * @author CH
 */
public class ManageableRouteLocator implements RouteLocator {

    private final Map<String, Route> allRoutes = new ConcurrentHashMap<>(8);

    /**
     * Route配置的path都是常量，没有pattern，key就是normalize后的path
     */
    private final Map<String, RouteGroup> constantPathRoutes = new ConcurrentHashMap<>(8);

    /**
     * Route配置的path都不是常量，含有pattern，key是最长的常量前缀
     * 比如一个Route的path是/foo/bar/{name}/info,那么它应该在key为/foo/bar的group中
     */
    private final Map<String, RouteGroup> patternPathRoutes = new ConcurrentHashMap<>(8);

    private final PathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Flux<Route> getRoutes(String path) {
        String normalizePath = PathUtils.normalize(path);
        RouteGroup routeGroup = constantPathRoutes.get(normalizePath);
        List<RouteGroup> groups = findInPatternPathRoutes(normalizePath);
        if (Objects.nonNull(routeGroup)) {
            groups.add(0, routeGroup);
        }
        if (groups.isEmpty()) {
            return Flux.empty();
        } else if (groups.size() == 1) {
            return groups.get(0).getRoutes();
        }
        List<Flux<Route>> list = new ArrayList<>(groups.size());
        for (RouteGroup rg : groups) {
            list.add(rg.getRoutes());
        }
        return Flux.concat(list);
    }

    /**
     * 在模式中查找路径路线
     *
     * @param normalizePath 规范化路径
     * @return {@link List}<{@link RouteGroup}>
     */
    private List<RouteGroup> findInPatternPathRoutes(String normalizePath) {
        String prefix = PathUtils.removeLast(normalizePath);
        List<RouteGroup> groups = new LinkedList<>();
        while (!prefix.isEmpty()) {
            RouteGroup routeGroup = patternPathRoutes.get(prefix);
            if (Objects.nonNull(routeGroup)) {
                groups.add(routeGroup);
            }
            prefix = PathUtils.removeLast(prefix);
        }
        return groups;
    }

    /**
     * 添加路由
     *
     * @param route 路线
     */
    protected synchronized void addRoute(Route route) {
        if (allRoutes.containsKey(route.getId())) {
            // 是一个已经存在的api的更新动作，其path可能已经改变，要先根据id删除之
            removeRouteById(route.getId());
        }
        this.allRoutes.put(route.getId(), route);
        String normalizePath = PathUtils.normalize(route.getPath());

        RouteGroup targetGroup;

        if (pathMatcher.isPattern(normalizePath)) {
            String prefix = PathUtils.constantPrefix(normalizePath);
            targetGroup = patternPathRoutes.get(prefix);
            if (Objects.isNull(targetGroup)) {
                targetGroup = new RouteGroup();
                patternPathRoutes.put(prefix, targetGroup);
            }
        } else {
            targetGroup = constantPathRoutes.get(normalizePath);
            if (Objects.isNull(targetGroup)) {
                targetGroup = new RouteGroup();
                constantPathRoutes.put(normalizePath, targetGroup);
            }
        }
        targetGroup.addRoute(route);
    }

    /**
     * 按id删除路由
     *
     * @param routeId 路由id
     * @return {@link Route}
     */
    protected synchronized Route removeRouteById(String routeId) {
        if (!allRoutes.containsKey(routeId)) {
            return null;
        }
        Route removedRoute = allRoutes.remove(routeId);
        String normalizePath = PathUtils.normalize(removedRoute.getPath());

        RouteGroup targetGroup;

        if (pathMatcher.isPattern(normalizePath)) {
            String prefix = PathUtils.constantPrefix(normalizePath);
            targetGroup = patternPathRoutes.get(prefix);
            if (Objects.nonNull(targetGroup)) {
                targetGroup.removeRouteById(routeId);
                if (targetGroup.isEmpty()) {
                    patternPathRoutes.remove(prefix);
                }
            }
        } else {
            targetGroup = constantPathRoutes.get(normalizePath);
            if (Objects.nonNull(targetGroup)) {
                targetGroup.removeRouteById(routeId);
                if (targetGroup.isEmpty()) {
                    constantPathRoutes.remove(normalizePath);
                }
            }
        }
        return removedRoute;
    }

    private static class RouteGroup {

        volatile boolean changed = false;

        final Map<String, Route> routes = new HashMap<>(4);

        Flux<Route> flux = Flux.empty();

        Flux<Route> getRoutes() {
            if (changed) {
                this.flux = Flux.fromIterable(routes.values());
                changed = false;
            }
            return flux;
        }

        void addRoute(Route route) {
            routes.put(route.getId(), route);
            changed = true;
        }

        void removeRouteById(String id) {
            changed = Objects.nonNull(this.routes.remove(id));
        }

        boolean isEmpty() {
            return this.routes.isEmpty();
        }

    }

}
