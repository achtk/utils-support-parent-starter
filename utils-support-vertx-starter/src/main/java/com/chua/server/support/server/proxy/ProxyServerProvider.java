package com.chua.server.support.server.proxy;


import com.chua.common.support.annotations.Spi;
import com.chua.common.support.protocol.server.Server;
import com.chua.common.support.protocol.server.ServerOption;
import com.chua.common.support.protocol.server.ServerProvider;

/**
 * 代理
 *
 * @author CH
 */
@Spi({"proxy", "tcp-proxy"})
public class ProxyServerProvider implements ServerProvider {
    @Override
    public Server create(ServerOption option, String... args) {
        return new ProxyServer(option, args);
    }
}
