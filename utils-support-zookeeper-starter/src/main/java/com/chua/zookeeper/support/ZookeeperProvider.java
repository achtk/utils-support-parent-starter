package com.chua.zookeeper.support;

import com.chua.common.support.protocol.client.AbstractClientProvider;
import com.chua.common.support.protocol.client.ClientOption;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

/**
 * zookeeper
 *
 * @author CH
 */
@Slf4j
public class ZookeeperProvider extends AbstractClientProvider<CuratorFramework> {

    public ZookeeperProvider(ClientOption option) {
        super(option);
    }

    @Override
    protected Class<?> clientType() {
        return ZookeeperClient.class;
    }

}
