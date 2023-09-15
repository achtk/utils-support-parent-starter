package com.chua.proxy.support.constant;

import com.google.common.collect.Sets;
import io.netty.handler.codec.http.HttpMethod;

import java.util.Set;


/**
 * 常量
 *
 * @author CH
 */
public class Constants {

    private Constants() {
    }

    public static final Set<HttpMethod> HTTP_METHODS_ALL = Sets.newHashSet(
            HttpMethod.GET,
            HttpMethod.POST,
            HttpMethod.PATCH,
            HttpMethod.DELETE,
            HttpMethod.HEAD,
            HttpMethod.PUT,
            HttpMethod.CONNECT,
            HttpMethod.TRACE
    );

}
