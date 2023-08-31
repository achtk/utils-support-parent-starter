package com.chua.common.support.mysql.network;

import java.net.Socket;
import java.net.SocketException;

/**
 * SocketFactory
 *
 * @author <a href="mailto:stanley.shyiko@gmail.com">Stanley Shyiko</a>
 */
public interface SocketFactory {
    /**
     * Socket
     *
     * @return Socket
     * @throws SocketException ex
     */
    Socket createSocket() throws SocketException;

}