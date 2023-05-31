package com.chua.common.support.lang.mail;

/**
 * 郵件发送器
 * @author CH
 */
public interface MailSender {

    /**
     * 发送邮件
     *
     * @param from 谁发送
     * @param mail 邮件信息
     * @throws Exception ex
     */
    void send(String from, Mail mail) throws Exception;
}
