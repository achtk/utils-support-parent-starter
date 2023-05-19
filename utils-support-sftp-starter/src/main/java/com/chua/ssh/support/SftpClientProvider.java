package com.chua.ssh.support;

import com.chua.common.support.protocol.client.AbstractClientProvider;
import com.chua.common.support.protocol.client.ClientOption;
import com.jcraft.jsch.ChannelSftp;
import lombok.extern.slf4j.Slf4j;

/**
 * sftp
 *
 * @author CH
 */
@Slf4j
public class SftpClientProvider extends AbstractClientProvider<ChannelSftp> {

    public SftpClientProvider(ClientOption option) {
        super(option);
    }

    @Override
    protected Class<?> clientType() {
        return SftpClient.class;
    }
}
