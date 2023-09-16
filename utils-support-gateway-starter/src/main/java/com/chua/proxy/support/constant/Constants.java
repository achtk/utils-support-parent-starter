package com.chua.proxy.support.constant;

import com.chua.common.support.http.HttpMethod;
import com.google.common.collect.Sets;

import java.util.Set;

/**
 * 常量
 *
 * @author CH
 * @since 2023/09/16
 */
public class Constants {


    public static final String DISCOVERY = "server.discovery";

    public static final Set<HttpMethod> HTTP_METHODS_ALL = Sets.newHashSet(
            HttpMethod.GET,
            HttpMethod.POST,
            HttpMethod.PATCH,
            HttpMethod.DELETE,
            HttpMethod.HEAD,
            HttpMethod.PUT
    );

}
