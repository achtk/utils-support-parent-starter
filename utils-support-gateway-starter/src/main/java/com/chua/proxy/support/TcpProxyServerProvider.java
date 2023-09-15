package com.chua.proxy.support;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.protocol.server.Server;
import com.chua.common.support.protocol.server.ServerOption;
import com.chua.common.support.protocol.server.ServerProvider;

/**
 * @author CH
 */
@Spi("tcp-proxy")
public class TcpProxyServerProvider implements ServerProvider {

    @Override
    public Server create(ServerOption option, String... args) {
        return new TcpProxyServer(option);
    }
}