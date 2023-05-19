package com.chua.zookeeper.support.server;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.protocol.server.Server;
import com.chua.common.support.protocol.server.ServerOption;
import com.chua.common.support.protocol.server.ServerProvider;
import lombok.extern.slf4j.Slf4j;

/**
 * zookeeper
 *
 * @author CH
 */
@Spi({"zookeeper", "zk"})
@Slf4j
public class ZookeeperServerProvider implements ServerProvider {

    @Override
    public Server create(ServerOption option, String... args) {
        return new ZookeeperServer(option, args);
    }
}
