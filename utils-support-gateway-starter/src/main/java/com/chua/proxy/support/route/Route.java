package com.chua.proxy.support.route;

import com.chua.proxy.support.constant.Constants;
import com.chua.proxy.support.exchange.Exchange;
import com.chua.proxy.support.filter.Filter;
import io.netty.handler.codec.http.HttpMethod;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
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

    private Set<HttpMethod> methods = Constants.HTTP_METHODS_ALL;

    private String path;

    private List<Filter> filters;


    public boolean isMatch(Exchange exchange) {
        return true;
    }
}
