package com.chua.example.zbus;

import org.zbus.mq.server.MqServer;
import org.zbus.mq.server.MqServerConfig;

/**
 * @author CH
 */
public class ZbusServerExample {

    public static void main(String[] args) throws Exception {
        MqServerConfig mqServerConfig = new MqServerConfig();
        mqServerConfig.setServerPort(55555);
        MqServer mqServer = new MqServer(mqServerConfig);
        mqServer.start();
    }
}
