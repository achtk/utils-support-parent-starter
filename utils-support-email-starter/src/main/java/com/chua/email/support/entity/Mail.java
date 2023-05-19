package com.chua.email.support.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.mail.Multipart;

/**
 * 邮件
 *
 * @author CH
 */
@Data
@Accessors(fluent = true)
public class Mail {
    /**
     * 发送邮箱
     */
    private String from;
    /**
     * 接收邮箱
     */
    private String to;
    /**
     * 抄送人邮箱
     */
    private String[] cc;
    /**
     * 邮件主题
     */
    private String subject;
    /**
     * 邮件正文
     */
    private String text;
    /**
     * 邮件正文类型.e.g. html
     */
    private String subtype;
    /**
     * 符合正文
     */
    private Multipart multipart;
}
