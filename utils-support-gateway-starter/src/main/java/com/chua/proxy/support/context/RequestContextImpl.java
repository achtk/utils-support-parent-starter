package com.chua.proxy.support.context;

import com.chua.proxy.support.exchange.Exchange;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;


/**
 * 请求上下文Impl
 *
 * @author CH
 */
public class RequestContextImpl implements RequestContext {

    @Getter
    private final Exchange exchange;

    @Getter
    private final Map<String, Object> attributes = new HashMap<>();

    public RequestContextImpl(Exchange exchange) {
        this.exchange = exchange;
    }

}
