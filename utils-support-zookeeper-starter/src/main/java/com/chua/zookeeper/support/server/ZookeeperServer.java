package com.chua.zookeeper.support.server;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.server.admin.AdminServer;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.apache.zookeeper.server.quorum.QuorumPeerMain;

import java.io.IOException;

/**
 * zookeeper
 *
 * @author CH
 */
@Slf4j
public class ZookeeperServer extends AbstractServer {

    private CustomQuorumPeerMain quorumPeerMain;

    protected ZookeeperServer(ServerOption serverOption, String... args) {
        super(serverOption, args);
    }

    @Override
    public void afterPropertiesSet() {

    }

    @Override
    protected void shutdown() {
        if (null == quorumPeerMain) {
            log.warn("服务未启动");
            return;
        }

        quorumPeerMain.close();
    }

    @Override
    protected void run() {
        this.quorumPeerMain = new CustomQuorumPeerMain();
        quorumPeerMain.run(new String[]{request.getString("conf")});
    }

    final class CustomQuorumPeerMain extends QuorumPeerMain {
        @Override
        protected void initializeAndRun(String[] args) throws QuorumPeerConfig.ConfigException, IOException, AdminServer.AdminServerException {
            super.initializeAndRun(args);
        }


        public void run(String[] args) {
            try {
                this.initializeAndRun(args);
            } catch (QuorumPeerConfig.ConfigException | IOException | AdminServer.AdminServerException e) {
                throw new RuntimeException(e);
            }
            ;
        }
    }
}
