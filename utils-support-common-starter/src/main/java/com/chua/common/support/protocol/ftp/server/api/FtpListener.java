package com.chua.common.support.protocol.ftp.server.api;

import com.chua.common.support.protocol.ftp.server.FtpConnection;

/**
 * Listens for events
 * @author Guilherme Chaguri
 */
public interface FtpListener {

    /**
     * Triggered when a new connection is created
     * @param con The new connection
     */
    void onConnected(FtpConnection con);

    /**
     * Triggered when a connection disconnects
     * @param con The connection
     */
    void onDisconnected(FtpConnection con);

}
