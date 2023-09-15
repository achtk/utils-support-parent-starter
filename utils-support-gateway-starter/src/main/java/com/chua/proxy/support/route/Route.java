package com.chua.proxy.support.route;

import com.chua.proxy.support.constant.Constants;
import com.chua.proxy.support.exchange.Exchange;
import com.chua.proxy.support.filter.Filter;
import io.netty.handler.codec.http.HttpMethod;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


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

    private Set<HttpMethod> methods = Constants.HTTP_METHODS_ALL;

    private Map<String, String> headers = new LinkedHashMap<>();

    private String path;

    private List<Filter> filters;

    public List<Filter> getFilters() {
        return filters;
    }

    public boolean isMatch(Exchange exchange) {
        return true;
    }
}
