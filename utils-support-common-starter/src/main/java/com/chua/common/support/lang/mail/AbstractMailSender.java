package com.chua.common.support.lang.mail;

/**
 * 郵件发送器
 * @author CH
 */
public abstract class AbstractMailSender implements MailSender{

    protected final MailConfiguration configuration;

    public AbstractMailSender(MailConfiguration configuration) {
        this.configuration = configuration;
    }


}
