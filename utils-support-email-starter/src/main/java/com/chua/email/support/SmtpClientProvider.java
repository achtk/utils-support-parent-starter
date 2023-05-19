package com.chua.email.support;


import com.chua.common.support.annotations.Spi;
import com.chua.common.support.protocol.client.AbstractClientProvider;
import com.chua.common.support.protocol.client.ClientOption;

import javax.mail.Session;

/**
 * stmp
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
@Spi("smtp")
public class SmtpClientProvider extends AbstractClientProvider<Session> {
    public SmtpClientProvider(ClientOption option) {
        super(option);
    }

    @Override
    protected Class<?> clientType() {
        return SmtpClient.class;
    }

}
