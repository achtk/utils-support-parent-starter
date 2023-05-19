package com.chua.zookeeper.support.monitor;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.monitor.AbstractMonitor;
import com.chua.common.support.monitor.NotifyType;
import com.chua.common.support.monitor.session.ObjectSession;
import com.chua.common.support.monitor.session.SessionNotifyMessage;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

import java.util.Optional;

/**
 * zk
 *
 * @author CH
 */
@Spi("zookeeper")
public class ZookeeperMonitor extends AbstractMonitor {
    private ZkClient zkClient;

    @Override
    public void preStart() {
    }

    @Override
    public void afterStart() {
        this.zkClient = new ZkClient(configuration.url());
        zkClient.subscribeChildChanges(Optional.ofNullable(configuration.database()).orElse("/"), (parentPath, currentChilds) -> {
            ObjectSession objectSession = new ObjectSession(currentChilds);
            SessionNotifyMessage sessionNotifyMessage = new SessionNotifyMessage();
            sessionNotifyMessage.setType(NotifyType.MODIFY);
            sessionNotifyMessage.setMessage(parentPath);
            sessionNotifyMessage.setSession(objectSession);
            ZookeeperMonitor.this.notifyMessage(sessionNotifyMessage);
        });

        zkClient.subscribeDataChanges(Optional.ofNullable(configuration.database()).orElse("/"), new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
                ObjectSession objectSession = new ObjectSession(data);
                SessionNotifyMessage sessionNotifyMessage = new SessionNotifyMessage();
                sessionNotifyMessage.setType(NotifyType.MODIFY);
                sessionNotifyMessage.setMessage(dataPath);
                sessionNotifyMessage.setSession(objectSession);
                ZookeeperMonitor.this.notifyMessage(sessionNotifyMessage);
            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                ObjectSession objectSession = new ObjectSession(dataPath);
                SessionNotifyMessage sessionNotifyMessage = new SessionNotifyMessage();
                sessionNotifyMessage.setType(NotifyType.DELETE);
                sessionNotifyMessage.setMessage(dataPath);
                sessionNotifyMessage.setSession(objectSession);
                ZookeeperMonitor.this.notifyMessage(sessionNotifyMessage);
            }

        });
    }

    @Override
    public void preStop() {

    }

    @Override
    public void afterStop() {
        zkClient.close();
    }
}
