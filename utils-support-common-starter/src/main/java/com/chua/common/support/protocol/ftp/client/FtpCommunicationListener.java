package com.chua.common.support.protocol.ftp.client;

/**
 * This interface describes how to build objects used to intercept any
 * communication between the client and the server. It is useful to catch what
 * happens behind. A FTPCommunicationListener can be added to any FTPClient
 * object by calling its addCommunicationListener() method.
 *
 * @author Carlo Pelliccia
 */
public interface FtpCommunicationListener {

    /**
     * Called every time a telnet statement has been sent over the network to
     * the remote FTP server.
     *
     * @param statement The statement that has been sent.
     */
    public void sent(String statement);

    /**
     * Called every time a telnet statement is received by the client.
     *
     * @param statement The received statement.
     */
    public void received(String statement);

}
