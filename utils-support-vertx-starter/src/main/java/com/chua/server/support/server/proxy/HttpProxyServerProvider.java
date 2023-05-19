package com.chua.server.support.server.proxy;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.protocol.server.Server;
import com.chua.common.support.protocol.server.ServerOption;
import com.chua.common.support.protocol.server.ServerProvider;
import lombok.extern.slf4j.Slf4j;

/**
 * http代理
 *
 * @author CH
 */
@Spi("http-proxy")
@Slf4j
public class HttpProxyServerProvider implements ServerProvider {

    @Override
    public Server create(ServerOption option, String... args) {
        return new HttpProxyServer(option, args);
    }
}
