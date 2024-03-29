package com.chua.common.support.protocol.ftp.client.connectors;

import com.chua.common.support.protocol.ftp.client.BaseFtpConnector;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * This one connects a remote ftp host through a SOCKS4/4a proxy server.
 * <p>
 * The connector's default value for the
 * <em>useSuggestedAddressForDataConnections</em> flag is <em>false</em>.
 *
 * @author Carlo Pelliccia
 */
public class Sock4Connector extends BaseFtpConnector {

    /**
     * The socks4 proxy host name.
     */
    private String socks4host;

    /**
     * The socks4 proxy port.
     */
    private int socks4port;

    /**
     * The socks4 proxy user (optional).
     */
    private String socks4user;

    /**
     * It builds the connector.
     *
     * @param socks4host The socks4 proxy host name.
     * @param socks4port The socks4 proxy port.
     * @param socks4user The socks4 proxy user (optional, can be set to null).
     */
    public Sock4Connector(String socks4host, int socks4port, String socks4user) {
        this.socks4host = socks4host;
        this.socks4port = socks4port;
        this.socks4user = socks4user;
    }

    /**
     * It builds the connector.
     *
     * @param socks4host The socks4 proxy host name.
     * @param socks4port The socks4 proxy port.
     */
    public Sock4Connector(String socks4host, int socks4port) {
        this(socks4host, socks4port, null);
    }

    private Socket socksConnect(String host, int port, boolean forDataTransfer) throws IOException {
        // Socks 4 or 4a?
        boolean socks4a = false;
        byte[] address;
        try {
            address = InetAddress.getByName(host).getAddress();
        } catch (Exception e) {
            // Cannot resolve host, switch to version 4a.
            socks4a = true;
            address = new byte[]{0x00, 0x00, 0x00, 0x01};
        }
        // A connection status flag.
        boolean connected = false;
        // The socket for the connection with the proxy.
        Socket socket = null;
        InputStream in = null;
        OutputStream out = null;
        // FTPConnection routine.
        try {
            if (forDataTransfer) {
                socket = tcpConnectForDataTransferChannel(socks4host, socks4port);
            } else {
                socket = tcpConnectForCommunicationChannel(socks4host, socks4port);
            }
            in = socket.getInputStream();
            out = socket.getOutputStream();
            // Send the request.
            // Version 4.
            out.write(0x04);
            // CONNECT method.
            out.write(0x01);
            // Remote port number.
            out.write(port >> 8);
            out.write(port);
            // Remote host address.
            out.write(address);
            // The user.
            if (socks4user != null) {
                out.write(socks4user.getBytes("UTF-8"));
            }
            // End of user.
            out.write(0x00);
            // Version 4a?
            if (socks4a) {
                out.write(host.getBytes("UTF-8"));
                out.write(0x00);
            }
            // Get and parse the response.
            int aux = read(in);
            if (aux != 0x00) {
                throw new IOException("SOCKS4Connector: invalid proxy response");
            }
            aux = read(in);
            switch (aux) {
                case 0x5a:
                    in.skip(6);
                    connected = true;
                    break;
                case 0x5b:
                    throw new IOException(
                            "SOCKS4Connector: connection refused/failed");
                case 0x5c:
                    throw new IOException(
                            "SOCKS4Connector: cannot validate the user");
                case 0x5d:
                    throw new IOException("SOCKS4Connector: invalid user");
                default:
                    throw new IOException("SOCKS4Connector: invalid proxy response");
            }
        } catch (IOException e) {
            throw e;
        } finally {
            if (!connected) {
                if (out != null) {
                    try {
                        out.close();
                    } catch (Throwable t) {
                        ;
                    }
                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (Throwable t) {
                        ;
                    }
                }
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (Throwable t) {
                        ;
                    }
                }
            }
        }
        return socket;
    }

    private int read(InputStream in) throws IOException {
        int aux = in.read();
        if (aux < 0) {
            throw new IOException(
                    "SOCKS4Connector: connection closed by the proxy");
        }
        return aux;
    }

    public Socket connectForCommunicationChannel(String host, int port)
            throws IOException {
        return socksConnect(host, port, false);
    }

    public Socket connectForDataTransferChannel(String host, int port)
            throws IOException {
        return socksConnect(host, port, true);
    }

}
