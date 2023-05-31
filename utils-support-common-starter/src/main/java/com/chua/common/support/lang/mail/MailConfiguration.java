package com.chua.common.support.lang.mail;

import lombok.Data;

import java.io.Serializable;

/**
 * 邮件实体
 */
@Data
public class MailConfiguration implements Serializable {

    /**
     * 主机
     */
    private String smtpHost = "smtp.qq.com";
    /**
     * ssl smtp端口
     */
    private String sslSmtpPort = "465";
    /**
     * 授权码
     */
    private String password;

    /**
     * smtp端口
     */
    private int smtpPort = 25;
}
