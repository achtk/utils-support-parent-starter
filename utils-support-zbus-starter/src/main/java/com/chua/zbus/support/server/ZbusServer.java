package com.chua.zbus.support.server;

import com.chua.common.support.protocol.server.AbstractServer;
import com.chua.common.support.protocol.server.ServerOption;
import com.chua.common.support.utils.IoUtils;
import org.zbus.mq.server.MqServer;
import org.zbus.mq.server.MqServerConfig;

/**
 * zbus服务器
 *
 * @author CH
 */
public class ZbusServer extends AbstractServer {
    private MqServer mqServer;

    protected ZbusServer(ServerOption serverOption) {
        super(serverOption);
    }

    @Override
    public void afterPropertiesSet() {

    }

    @Override
    protected void shutdown() {
        IoUtils.closeQuietly(mqServer);
    }

    @Override
    protected void run() {
        MqServerConfig mqServerConfig = new MqServerConfig();
        mqServerConfig.setServerPort(getPort());
        mqServerConfig.setServerHost(getHost());
        this.mqServer = new MqServer(mqServerConfig);
        try {
            mqServer.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
