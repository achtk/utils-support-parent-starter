package com.chua.sshd.support;


import com.chua.common.support.annotations.Spi;
import com.chua.common.support.protocol.server.Server;
import com.chua.common.support.protocol.server.ServerOption;
import com.chua.common.support.protocol.server.ServerProvider;

/**
 * server
 *
 * @author CH
 */
@Spi("ssh")
public class SshServerProvider implements ServerProvider {

    @Override
    public Server create(ServerOption option, String... args) {
        return new SshServer(option, args);
    }
}
