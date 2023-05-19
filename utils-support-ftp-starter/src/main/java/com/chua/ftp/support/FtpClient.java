package com.chua.ftp.support;

import com.chua.common.support.protocol.client.AbstractClient;
import com.chua.common.support.protocol.client.ClientOption;
import com.chua.common.support.utils.NetAddress;
import lombok.SneakyThrows;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;

/**
 * ftp
 *
 * @author CH
 */
public class FtpClient extends AbstractClient<FTPClient> {

    FTPClient ftp = new FTPClient();
    private String url;
    private long timeout;

    protected FtpClient(ClientOption clientOption) {
        super(clientOption);
    }

    @Override
    public FTPClient getClient() {
        return ftp;
    }

    @Override
    public void closeClient(FTPClient client) {
        try {
            client.disconnect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void connectClient() {
        ftp = connectClient(url);
    }

    /**
     * 创建客户端
     *
     * @param url 地址
     * @return 客户端
     */
    private FTPClient connectClient(String url) {
        FTPClient ftp = new FTPClient();
        NetAddress netAddress = NetAddress.of(url);
        FTPClientConfig clientConfig = new FTPClientConfig();
        try {
            ftp.configure(clientConfig);
            ftp.connect(netAddress.getHost(), netAddress.getPort());
            ftp.login(netAddress.getUsername(), netAddress.getPassword());
            int reply = ftp.getReplyCode();
            if ((!FTPReply.isPositiveCompletion(reply))) {
                ftp.disconnect();
            }
            // 开启服务器对UTF-8的支持，如果服务器支持就用UTF-8编码，否则就使用本地编码（GBK）.
            if (FTPReply.isPositiveCompletion(ftp.sendCommand("OPTS UTF8", "ON"))) {
                ftp.setControlEncoding("UTF-8");
            } else {
                ftp.setControlEncoding("GBK");
            }
            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
        } catch (Exception ignored) {
        }
        return ftp;
    }


    @SneakyThrows
    @Override
    public void close() {
        ftp.disconnect();
    }

    @Override
    public void afterPropertiesSet() {

    }
}
