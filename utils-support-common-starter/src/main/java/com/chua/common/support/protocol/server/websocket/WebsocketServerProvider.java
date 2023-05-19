package com.chua.common.support.protocol.server.websocket;


import com.chua.common.support.annotations.Spi;
import com.chua.common.support.protocol.server.Server;
import com.chua.common.support.protocol.server.ServerOption;
import com.chua.common.support.protocol.server.ServerProvider;

/**
 * websocket
 *
 * @author CH
 */
@Spi("websocket")
public class WebsocketServerProvider implements ServerProvider {
    @Override
    public Server create(ServerOption option, String... args) {
        return new WebsocketServer(option, args);
    }
}
