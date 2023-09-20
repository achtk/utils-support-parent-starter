package com.chua.sshd.support.client;

import com.chua.common.support.protocol.client.AbstractClientProvider;
import com.chua.common.support.protocol.client.ClientOption;

/**
 * ssh客户端
 *
 * @author CH
 * @since 2023/09/19
 */
public class SshClientProvider extends AbstractClientProvider<SshClient> {

    public SshClientProvider(ClientOption option) {
        super(option);
    }

    @Override
    protected Class<?> clientType() {
        return SshClient.class;
    }
}
