package com.chua.zbus.support.server;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.protocol.server.Server;
import com.chua.common.support.protocol.server.ServerOption;
import com.chua.common.support.protocol.server.ServerProvider;

/**
 * zbus服务器
 *
 * @author CH
 */
@Spi("zbus")
public class ZbusServerProvider implements ServerProvider {

    @Override
    public Server create(ServerOption option, String... args) {
        return new ZbusServer(option);
    }
}
