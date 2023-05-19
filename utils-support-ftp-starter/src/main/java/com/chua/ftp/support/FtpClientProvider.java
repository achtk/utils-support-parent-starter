package com.chua.ftp.support;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.protocol.client.AbstractClientProvider;
import com.chua.common.support.protocol.client.ClientOption;
import org.apache.commons.net.ftp.FTPClient;

/**
 * ftp
 *
 * @author CH
 */
@Spi("ftp")
public class FtpClientProvider extends AbstractClientProvider<FTPClient> {

    public FtpClientProvider(ClientOption option) {
        super(option);
    }

    @Override
    protected Class<?> clientType() {
        return FtpClient.class;
    }
}
