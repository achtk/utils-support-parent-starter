package com.chua.ssh.support;

import com.chua.common.support.protocol.client.AbstractClientProvider;
import com.chua.common.support.protocol.client.ClientOption;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;

/**
 * ssh
 *
 * @author CH
 */
@Slf4j
public class SshClientProvider extends AbstractClientProvider<Session> {


    public SshClientProvider(ClientOption option) {
        super(option);
    }

    @Override
    protected Class<?> clientType() {
        return SshClient.class;
    }
}