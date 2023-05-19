package com.chua.email.support;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.protocol.client.AbstractClientProvider;
import com.chua.common.support.protocol.client.ClientOption;

import javax.mail.Session;

/**
 * imap
 * ┌─────────┐    ┌─────────┐    ┌─────────┐
 * │░░░░░░░░░│    │░░░░░░░░░│    │░░░░░░░░░│
 * ┌───────┐    ├─────────┤    ├─────────┤    ├─────────┤    ┌───────┐
 * │░░░░░░░│    │░░░░░░░░░│    │░░░░░░░░░│    │░░░░░░░░░│    │░░░░░░░│
 * ├───────┤    ├─────────┤    ├─────────┤    ├─────────┤    ├───────┤
 * │       │───>│O ░░░░░░░│───>│O ░░░░░░░│───>│O ░░░░░░░│<───│       │
 * └───────┘    └─────────┘    └─────────┘    └─────────┘    └───────┘
 * MUA           MTA            MTA            MDA           MUA
 *
 * @author CH
 */
@Spi("imap")
public class ImapClientProvider extends AbstractClientProvider<Session> {
    public ImapClientProvider(ClientOption option) {
        super(option);
    }

    @Override
    protected Class<?> clientType() {
        return ImapClient.class;
    }

}
