package com.chua.agent.support.store;

import com.chua.agent.support.constant.Constant;
import com.chua.agent.support.server.EmbedHttpServer;
import com.chua.agent.support.server.EmbedServer;
import com.chua.agent.support.server.EmbedSpringServer;
import net.bytebuddy.agent.builder.AgentBuilder;

import static com.chua.agent.support.store.AgentStore.getStringValue;

/**
 * 服务缓存器
 *
 * @author CH
 */
public class ServerStore implements Constant {
    public static EmbedServer embedServer;

    /**
     * 安装服务
     */
    public static AgentBuilder.Identified.Extendable installServer(AgentBuilder.Identified.Extendable transform) {
        String protocol = getStringValue(SERVER_TYPE, "");
        if ("http".equalsIgnoreCase(protocol)) {
            embedServer = new EmbedHttpServer();
        } else if ("spring".equalsIgnoreCase(protocol)) {
            EmbedSpringServer server = new EmbedSpringServer();
            transform = server.inject(transform);
            embedServer = server;
        }

        return transform;
    }

    /**
     * 预处理服务
     */
    public static void preServer() {
        if (null == embedServer) {
            return;
        }
        embedServer.start();
    }
}
