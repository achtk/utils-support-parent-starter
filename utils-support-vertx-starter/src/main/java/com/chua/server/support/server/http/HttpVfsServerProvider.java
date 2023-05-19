package com.chua.server.support.server.http;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.protocol.server.Server;
import com.chua.common.support.protocol.server.ServerOption;
import com.chua.common.support.protocol.server.ServerProvider;

/**
 * http
 *
 * @author CH
 */
@Spi("static")
public class HttpVfsServerProvider implements ServerProvider {

    @Override
    public Server create(ServerOption option, String... args) {
        return new HttpVfsServer(option, args);
    }
}
