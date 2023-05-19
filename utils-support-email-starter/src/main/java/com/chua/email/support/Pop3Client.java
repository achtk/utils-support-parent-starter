package com.chua.email.support;

import com.chua.common.support.protocol.client.AbstractClient;
import com.chua.common.support.protocol.client.ClientOption;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

/**
 * pop3
 *
 * @author CH
 */
public class Pop3Client extends AbstractClient<Session> {
    private Session session;

    protected Pop3Client(ClientOption clientOption) {
        super(clientOption);
    }

    @Override
    public void connectClient() {
        // 连接到SMTP服务器25端口:
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "pop3");
        props.setProperty("mail.pop3.host", netAddress.getHost()); // 按需要更改
        props.setProperty("mail.pop3.port", String.valueOf(netAddress.getPort(110)));
        // SSL安全连接参数
        props.setProperty("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.pop3.socketFactory.fallback", "false");
        props.setProperty("mail.pop3.socketFactory.port", String.valueOf(describe().getString("mail.pop3.socketFactory.port", "995")));
        // 解决DecodingException: BASE64Decoder: but only got 0 before padding character (=)
        props.setProperty("mail.mime.base64.ignoreerrors", "true");
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
