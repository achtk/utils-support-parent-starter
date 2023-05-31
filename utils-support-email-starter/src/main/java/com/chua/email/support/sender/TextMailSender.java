package com.chua.email.support.sender;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.lang.mail.AbstractMailSender;
import com.chua.common.support.lang.mail.Mail;
import com.chua.common.support.lang.mail.MailConfiguration;
import com.chua.common.support.lang.mail.MailSender;
import com.chua.common.support.log.Log;
import com.chua.common.support.utils.UrlUtils;
import org.apache.commons.mail.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.mail.EmailAttachment.ATTACHMENT;

/**
 * 文本发送
 * @author CH
 */
@Spi("text")
public class TextMailSender extends AbstractMailSender {

    private static final Log log = Log.getLogger(MailSender.class);

    public TextMailSender(MailConfiguration configuration) {
        super(configuration);
    }

    @Override
    public void send(String from, Mail mail) throws Exception {
        Email email = createMail(mail);
        refresh(email, from, mail);
        email.setMsg(mail.getContent());

        email.send();
        if(log.isDebugEnabled()) {
            log.debug("{} -> {}邮件[{}]发送成功", from, mail.getTo(), mail.getTitle());
        }
    }

    private Email createMail(Mail mail) {
        return mail.hasAttach() ? new MultiPartEmail() : new SimpleEmail();
    }

    protected void refresh(Email email, String from , Mail mail) throws EmailException {
        email.setHostName(configuration.getSmtpHost());
        email.setSmtpPort(configuration.getSmtpPort());
        email.setSslSmtpPort(configuration.getSslSmtpPort());
        email.setAuthenticator(new DefaultAuthenticator(from, configuration.getPassword()));
        email.setSSLOnConnect(true);
        email.setStartTLSEnabled(true);
        email.setFrom(from);
        email.setSubject(mail.getTitle());
        email.addTo(mail.getTo());
        if(mail.hasAttach() && email instanceof MultiPartEmail) {
            List<EmailAttachment> collect = mail.getAttachment().stream().map(it -> {
                EmailAttachment attachment = new EmailAttachment();
                attachment.setDescription(it.getDesc());
                attachment.setURL(it.getUrl());
                attachment.setDisposition(ATTACHMENT);
                try {
                    attachment.setName(UrlUtils.getFileName(it.getUrl().openConnection()));
                } catch (IOException ignored) {
                }
                return attachment;
            }).collect(Collectors.toList());

            for (EmailAttachment attachment : collect) {
                ((MultiPartEmail) email).attach(attachment);
            }
        }
    }
}
