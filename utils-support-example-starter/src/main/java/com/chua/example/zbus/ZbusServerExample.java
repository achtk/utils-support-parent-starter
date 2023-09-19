package com.chua.example.zbus;

import com.chua.common.support.protocol.server.Server;
import com.chua.common.support.protocol.server.ServerOption;
import com.chua.common.support.protocol.server.ServerProvider;


/**
 * @author CH
 */
public class ZbusServerExample {

    public static void main(String[] args) throws Exception {
        ServerProvider serverProvider = Server.createServerProvider("zbus");
        Server server = serverProvider.create(ServerOption.builder().port(2181).build());
        server.start();
    }
}
