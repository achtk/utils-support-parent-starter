package com.chua.ftp.support.server;

import com.chua.common.support.protocol.server.Server;
import com.chua.common.support.protocol.server.ServerOption;
import com.chua.common.support.protocol.server.ServerProvider;

/**
 * 服务端
 *
 * @author CH
 */
public class FtpServerProvider implements ServerProvider {
    @Override
    public Server create(ServerOption option, String... args) {
        return new FtpVfsServer(option, args);
    }
}
