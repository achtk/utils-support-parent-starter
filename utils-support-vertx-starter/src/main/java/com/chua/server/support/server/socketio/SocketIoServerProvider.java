package com.chua.server.support.server.socketio;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.protocol.server.Server;
import com.chua.common.support.protocol.server.ServerOption;
import com.chua.common.support.protocol.server.ServerProvider;

/**
 * SocketIo
 *
 * @author CH
 */
@Spi("socket-io")
public class SocketIoServerProvider implements ServerProvider {

    @Override
    public Server create(ServerOption option, String... args) {
        return new SocketIoServer(option, args);
    }
}
