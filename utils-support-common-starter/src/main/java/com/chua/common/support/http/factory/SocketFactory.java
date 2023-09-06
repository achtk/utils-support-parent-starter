package com.chua.common.support.http.factory;

import javax.net.ssl.SSLContext;

/**
 * 插座工厂
 *
 * @author CH
 * @since 2023/09/06
 */
public interface SocketFactory {
    /**
     * 注册
     *
     * @param ctx ctx
     */
    void register(SSLContext ctx);
}
