package com.chua.ssh.support;

import com.chua.common.support.protocol.client.AbstractClient;
import com.chua.common.support.protocol.client.ClientOption;
import com.chua.common.support.utils.NetAddress;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;

/**
 * ssh
 *
 * @author CH
 */
@Slf4j
public class SshClient extends AbstractClient<Session> {

    protected Session session;

    protected SshClient(ClientOption clientOption) {
        super(clientOption);
    }

    @Override
    public Session getClient() {
        return session;
    }

    @Override
    public void closeClient(Session client) {
        client.disconnect();
    }

    @Override
    public void connectClient() {
        NetAddress netAddress = NetAddress.of(url);
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(clientOption.username(), netAddress.getHost(), netAddress.getPort());
            //设置登录主机的密码
            session.setPassword(clientOption.password());
            //如果服务器连接不上，则抛出异常
            if (session == null) {
                throw new Exception("session is null");
            }
            //设置首次登录跳过主机检查
            session.setConfig("StrictHostKeyChecking", "no");
            //设置登录超时时间
            session.connect((int) timeout);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }



    @Override
    public void close() {
        session.disconnect();
    }

    @Override
    public void afterPropertiesSet() {

    }

    @Override
    protected boolean allowPooling() {
        return false;
    }
}
