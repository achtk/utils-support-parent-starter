package com.chua.email.support.operate;

import com.chua.common.support.utils.ArrayUtils;
import com.chua.email.support.store.StoreListener;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * smtp
 *
 * @author CH
 */
public class Mail {
    private final Session session;
    private final String host;
    private final String username;
    private final String password;
    private final Store store;

    public Mail(Session session, String host, String protocol, String username, String password) {
        this.session = session;
        this.host = host;
        this.username = username;
        this.password = password;
        try {
            this.store = session.getStore(protocol);
        } catch (NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 发送邮件
     *
     * @param listener listener
     * @throws Exception ex
     */
    public void receiveMail(StoreListener listener) throws Exception {
        store.connect(host, username, password);
        listener.listen(store);
    }

    /**
     * 发送邮件
     *
     * @param mail 邮件
     * @throws Exception ex
     */
    public void sendMail(com.chua.email.support.entity.Mail mail) throws Exception {
        MimeMessage message = new MimeMessage(session);
        // 设置发送方地址:
        message.setFrom(new InternetAddress(mail.from()));
        // 设置接收方地址:       TO主要收件人 CC抄送人
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(mail.to()));
        //抄送人
        if (!ArrayUtils.isEmpty(mail.cc())) {
            for (String s : mail.cc()) {
                message.setRecipient(Message.RecipientType.CC, new InternetAddress(s));
            }
        }
        // 设置邮件主题:
        message.setSubject(mail.subject(), "UTF-8");
        // 设置邮件正文:
        //邮件正文中包含有"html"标签（控制文本的格式）
        message.setText(mail.text(), "UTF-8", mail.subtype());

        if (null != mail.multipart()) {
            message.setContent(mail.multipart());
        }
        // 发送:
        Transport.send(message);
    }

}
