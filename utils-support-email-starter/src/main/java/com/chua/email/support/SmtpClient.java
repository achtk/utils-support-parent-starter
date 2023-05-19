package com.chua.email.support;

import com.chua.common.support.protocol.client.AbstractClient;
import com.chua.common.support.protocol.client.ClientOption;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

/**
 * stmp
 *
 * @author CH
 */
public class SmtpClient extends AbstractClient<Session> {
    private Session session;

    protected SmtpClient(ClientOption clientOption) {
        super(clientOption);
    }

    @Override
    public void connectClient() {
        // 连接到SMTP服务器25端口:
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        // SMTP主机名
        props.put("mail.smtp.host", netAddress.getHost());
        // 主机端口号
        props.put("mail.smtp.port", netAddress.getPort(25));

        // SSL安全连接参数
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.socketFactory.port", String.valueOf(describe().getString("mail.smtp.socketFactory.port", "587")));

        // 获取Session实例:
        //参数1：SMTP服务器的连接信息
        //参数2：用户认证对象（Authenticator接口的匿名实现类）
        this.session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(clientOption.username(), clientOption.password());
            }
        });
        // 设置debug模式便于调试:
        session.setDebug(true);
    }

    @Override
    public Session getClient() {
        return session;
    }

    @Override
    public void closeClient(Session client) {

    }

    @Override
    public void close() {

    }

    @Override
    public void afterPropertiesSet() {

    }
}
