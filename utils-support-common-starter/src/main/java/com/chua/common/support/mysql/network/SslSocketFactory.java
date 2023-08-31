package com.chua.common.support.mysql.network;

import javax.net.ssl.SSLSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * @author <a href="mailto:stanley.shyiko@gmail.com">Stanley Shyiko</a>
 */
public interface SslSocketFactory {
    /**
     * SSLSocket
     *
     * @param socket socket
     * @return SSLSocket
     * @throws SocketException ex
     */
    SSLSocket createSocket(Socket socket) throws SocketException;
}
