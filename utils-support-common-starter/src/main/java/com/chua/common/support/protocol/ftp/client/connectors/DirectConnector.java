package com.chua.common.support.protocol.ftp.client.connectors;


import com.chua.common.support.protocol.ftp.client.FTPConnector;

import java.io.IOException;
import java.net.Socket;

/**
 * The DirectConnector connects the remote host with a straight socket
 * connection, using no proxy.
 * <p>
 * The connector's default value for the
 * <em>useSuggestedAddressForDataConnections</em> flag is <em>false</em>.
 *
 * @author Carlo Pelliccia
 */
public class DirectConnector extends FTPConnector {

    public Socket connectForCommunicationChannel(String host, int port)
            throws IOException {
        return tcpConnectForCommunicationChannel(host, port);
    }

    public Socket connectForDataTransferChannel(String host, int port)
            throws IOException {
        return tcpConnectForDataTransferChannel(host, port);
    }

}
