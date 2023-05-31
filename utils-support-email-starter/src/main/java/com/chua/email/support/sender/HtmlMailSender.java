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
@Spi("html")
public class HtmlMailSender extends TextMailSender {

    private static final Log log = Log.getLogger(MailSender.class);

    public HtmlMailSender(MailConfiguration configuration) {
        super(configuration);
    }

    @Override
    public void send(String from, Mail mail) throws Exception {
        HtmlEmail email = new HtmlEmail();
        refresh(email, from, mail);
        email.setHtmlMsg(mail.getContent());

        email.send();
        if(log.isDebugEnabled()) {
            log.debug("{} -> {}邮件[{}]发送成功", from, mail.getTo(), mail.getTitle());
        }
    }

}
