package com.chua.proxy.support.route;

import com.chua.common.support.discovery.Discovery;
import com.chua.common.support.http.HttpMethod;
import com.chua.common.support.utils.StringUtils;
import com.chua.proxy.support.constant.Constants;
import com.chua.proxy.support.filter.Filter;
import lombok.Getter;
import lombok.Setter;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.chua.common.support.constant.NameConstant.HTTP;


/**
 * 路线
 *
 * @author CH
 */
@Getter
@Setter
public class Route {

    private String id;

    private String envKey;

    private Integer timeout;

    private String ip;


    private Integer port;

    private Set<HttpMethod> methods = Constants.HTTP_METHODS_ALL;

    private Map<String, String> headers = new LinkedHashMap<>();

    private String path;

    private String protocol;
    private List<Filter> filters;

    public List<Filter> getFilters() {
        return filters;
    }

    /**
     * 创造
     *
     * @param discovery 发现
     * @return {@link Route}
     */
    public static Route create(Discovery discovery) {
        if (null == discovery) {
            return null;
        }

        String protocol = StringUtils.defaultString(discovery.getProtocol(), HTTP);

        Route route = new Route();
        route.setIp(discovery.getIp());
        route.setPort(discovery.getPort());
        route.setProtocol(protocol);
        route.setPath(protocol + "://" + discovery.getIp() + ":" + discovery.getPort() + "" + discovery.getUriSpec());
        route.setTimeout(discovery.getTimeout());
        return route;
    }

    /**
     * 是http
     *
     * @return boolean
     */
    public boolean isHttp() {
        return HTTP.equals(protocol);
    }

    /**
     * 地址
     *
     * @return {@link SocketAddress}
     */
    public SocketAddress address() {
        return new InetSocketAddress(ip, port);
    }
}
