package com.chua.ssh.support;

import com.chua.common.support.protocol.client.AbstractClient;
import com.chua.common.support.protocol.client.ClientOption;
import com.chua.common.support.utils.NetAddress;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;

/**
 * ssh
 *
 * @author CH
 */
@Slf4j
public class SftpClient extends AbstractClient<ChannelSftp> {

    protected Session session;
    ChannelSftp channel = null;

    protected SftpClient(ClientOption clientOption) {
        super(clientOption);
    }

    @Override
    public ChannelSftp getClient() {
        return channel;
    }

    @Override
    public void closeClient(ChannelSftp client) {
        client.disconnect();
    }


    @Override
    public void close() {
        if (channel != null) {
            //关闭通道
            channel.disconnect();
        }
        session.disconnect();
    }

    @Override
    public void afterPropertiesSet() {

    }

    @Override
    protected boolean allowPooling() {
        return false;
    }

    @Override
    public void connectClient() {
        {
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

                //建立交互式通道
                channel = (ChannelSftp) session.openChannel("sftp");
                channel.connect((int) timeout);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}