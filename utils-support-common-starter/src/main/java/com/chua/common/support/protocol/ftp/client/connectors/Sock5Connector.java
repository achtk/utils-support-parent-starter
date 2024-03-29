package com.chua.common.support.protocol.ftp.client.connectors;


import com.chua.common.support.protocol.ftp.client.BaseFtpConnector;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * This one connects a remote ftp host through a SOCKS5 proxy server.
 * <p>
 * The connector's default value for the
 * <em>useSuggestedAddressForDataConnections</em> flag is <em>false</em>.
 *
 * @author Carlo Pelliccia
 */
public class Sock5Connector extends BaseFtpConnector {

    /**
     * The socks5 proxy host name.
     */
    private final String socks5host;

    /**
     * The socks5 proxy port.
     */
    private final int socks5port;

    /**
     * The socks5 proxy user (optional).
     */
    private final String socks5user;

    /**
     * The socks5 proxy password (optional).
     */
    private final String socks5pass;

    /**
     * It builds the connector.
     *
     * @param socks5host The socks5 proxy host name.
     * @param socks5port The socks5 proxy port.
     * @param socks5user The socks5 proxy user (optional, can be set to null).
     * @param socks5pass The socks5 proxy password (optional, can be set to null if
     *                   also socks5user is null).
     */
    public Sock5Connector(String socks5host, int socks5port,
                           String socks5user, String socks5pass) {
        this.socks5host = socks5host;
        this.socks5port = socks5port;
        this.socks5user = socks5user;
        this.socks5pass = socks5pass;
    }

    /**
     * It builds the connector.
     *
     * @param socks5host The socks5 proxy host name.
     * @param socks5port The socks5 proxy port.
     */
    public Sock5Connector(String socks5host, int socks5port) {
        this(socks5host, socks5port, null, null);
    }

    private Socket socksConnect(String host, int port, boolean forDataTransfer) throws IOException {
        // Authentication flag
        boolean authentication = socks5user != null && socks5pass != null;
        // A connection status flag.
        boolean connected = false;
        // The socket for the connection with the proxy.
        Socket socket = null;
        InputStream in = null;
        OutputStream out = null;
        // FTPConnection routine.
        try {
            if (forDataTransfer) {
                socket = tcpConnectForDataTransferChannel(socks5host, socks5port);
            } else {
                socket = tcpConnectForCommunicationChannel(socks5host, socks5port);
            }
            in = socket.getInputStream();
            out = socket.getOutputStream();
            int aux;
            // Version 5.
            out.write(0x05);
            // Authentication?
            if (authentication) {
                // Authentication with username/password.
                out.write(0x01);
                out.write(0x02);
            } else {
                // No authentication.
                out.write(0x01);
                out.write(0x00);
            }
            // Get the response.
            aux = read(in);
            if (aux != 0x05) {
                throw new IOException("SOCKS5Connector: invalid proxy response");
            }
            aux = read(in);
            if (authentication) {
                if (aux != 0x02) {
                    throw new IOException(
                            "SOCKS5Connector: proxy doesn't support "
                                    + "username/password authentication method");
                }
                // Authentication with username/password.
                byte[] user = socks5user.getBytes("UTF-8");
                byte[] pass = socks5pass.getBytes("UTF-8");
                int userLength = user.length;
                int passLength = pass.length;
                // Check sizes.
                if (userLength > 0xff) {
                    throw new IOException("SOCKS5Connector: username too long");
                }
                if (passLength > 0xff) {
                    throw new IOException("SOCKS5Connector: password too long");
                }
                // Version 1.
                out.write(0x01);
                // Username.
                out.write(userLength);
                out.write(user);
                // Password.
                out.write(passLength);
                out.write(pass);
                // Check the response.
                aux = read(in);
                if (aux != 0x01) {
                    throw new IOException(
                            "SOCKS5Connector: invalid proxy response");
                }
                aux = read(in);
                if (aux != 0x00) {
                    throw new IOException(
                            "SOCKS5Connector: authentication failed");
                }
            } else {
                if (aux != 0x00) {
                    throw new IOException(
                            "SOCKS5Connector: proxy requires authentication");
                }
            }
            // FTPConnection request.
            // Version 5.
            out.write(0x05);
            // CONNECT method
            out.write(0x01);
            // Reserved.
            out.write(0x00);
            // Address type -> domain.
            out.write(0x03);
            // Domain.
            byte[] domain = host.getBytes("UTF-8");
            if (domain.length > 0xff) {
                throw new IOException("SOCKS5Connector: domain name too long");
            }
            out.write(domain.length);
            out.write(domain);
            // Port number.
            out.write(port >> 8);
            out.write(port);

            // FTPConnection response
            // Version?
            aux = read(in);
            if (aux != 0x05) {
                throw new IOException("SOCKS5Connector: invalid proxy response");
            }
            // Status?
            aux = read(in);
            switch (aux) {
                case 0x00:
                    // Connected!
                    break;
                case 0x01:
                    throw new IOException("SOCKS5Connector: general failure");
                case 0x02:
                    throw new IOException(
                            "SOCKS5Connector: connection not allowed by ruleset");
                case 0x03:
                    throw new IOException("SOCKS5Connector: network unreachable");
                case 0x04:
                    throw new IOException("SOCKS5Connector: host unreachable");
                case 0x05:
                    throw new IOException(
                            "SOCKS5Connector: connection refused by destination host");
                case 0x06:
                    throw new IOException("SOCKS5Connector: TTL expired");
                case 0x07:
                    throw new IOException(
                            "SOCKS5Connector: command not supported / protocol error");
                case 0x08:
                    throw new IOException(
                            "SOCKS5Connector: address type not supported");
                default:
                    throw new IOException("SOCKS5Connector: invalid proxy response");
            }
            // Reserved.
            in.skip(1);
            // Address type.
            aux = read(in);
            if (aux == 0x01) {
                // IPv4.
                in.skip(4);
            } else if (aux == 0x03) {
                // Domain name.
                aux = read(in);
                in.skip(aux);
            } else if (aux == 0x04) {
                // IPv6.
                in.skip(16);
            } else {
                throw new IOException("SOCKS5Connector: invalid proxy response");
            }
            // Port number.
            in.skip(2);
            // Well done!
            connected = true;
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
                    "SOCKS5Connector: connection closed by the proxy");
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
