package com.chua.email.support;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.protocol.client.AbstractClientProvider;
import com.chua.common.support.protocol.client.ClientOption;

import javax.mail.Session;

/**
 * pop3
 * ┌───────┐    ┌─────────┐    ┌─────────┐
 * │░░░░░░░│    │░░░░░░░░░│    │░░░░░░░░░│
 * ┌───────┐    ├─────────┤    ├─────────┤    ├─────────┤    ┌───────┐
 * │░░░░░░░│    │░░░░░░░░░│    │░░░░░░░░░│    │░░░░░░░░░│    │░░░░░░░│
 * ├───────┤    ├─────────┤    ├─────────┤    ├─────────┤    ├───────┤
 * │       │───>│O ░░░░░░░│───>│O ░░░░░░░│───>│O ░░░░░░░│<───│       │
 * └───────┘    └─────────┘    └─────────┘    └─────────┘    └───────┘
 * MUA           MTA            MTA            MDA           MUA
 *
 * @author CH
 */
@Spi("pop3")
public class Pop3ClientProvider extends AbstractClientProvider<Session> {
    public Pop3ClientProvider(ClientOption option) {
        super(option);
    }

    @Override
    protected Class<?> clientType() {
        return Pop3Client.class;
    }

}
