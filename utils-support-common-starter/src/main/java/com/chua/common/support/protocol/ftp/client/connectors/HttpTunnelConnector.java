package com.chua.common.support.protocol.ftp.client.connectors;


import com.chua.common.support.protocol.ftp.client.BaseFtpConnector;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * This one connects a remote ftp host via a HTTP 1.1 proxy which allows
 * tunneling through the HTTP CONNECT method.
 * <p>
 * The connector's default value for the
 * <em>useSuggestedAddressForDataConnections</em> flag is <em>false</em>.
 *
 * @author Carlo Pelliccia
 */
public class HttpTunnelConnector extends BaseFtpConnector {

    /**
     * The proxy host name.
     */
    private String proxyHost;

    /**
     * The proxy port.
     */
    private int proxyPort;

    /**
     * The proxyUser for proxy authentication.
     */
    private String proxyUser;

    /**
     * The proxyPass for proxy authentication.
     */
    private String proxyPass;

    /**
     * Builds the connector.
     *
     * @param proxyHost The proxy host name.
     * @param proxyPort The proxy port.
     * @param proxyUser The username for proxy authentication.
     * @param proxyPass The password for proxy authentication.
     */
    public HttpTunnelConnector(String proxyHost, int proxyPort,
                               String proxyUser, String proxyPass) {
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        this.proxyUser = proxyUser;
        this.proxyPass = proxyPass;
    }

    /**
     * Builds the connector.
     *
     * @param proxyHost The proxy host name.
     * @param proxyPort The proxy port.
     */
    public HttpTunnelConnector(String proxyHost, int proxyPort) {
        this(proxyHost, proxyPort, null, null);
    }

    private Socket httpConnect(String host, int port, boolean forDataTransfer) throws IOException {
        // The CRLF sequence.
        byte[] CRLF = "\r\n".getBytes("UTF-8");
        // The connect command line.
        String connect = "CONNECT " + host + ":" + port + " HTTP/1.1";
        String hostHeader = "Host: " + host + ":" + port;
        // A connection status flag.
        boolean connected = false;
        // The socket for the connection with the proxy.
        Socket socket = null;
        InputStream in = null;
        OutputStream out = null;
        // FTPConnection routine.
        try {
            if (forDataTransfer) {
                socket = tcpConnectForDataTransferChannel(proxyHost, proxyPort);
            } else {
                socket = tcpConnectForCommunicationChannel(proxyHost, proxyPort);
            }
            in = socket.getInputStream();
            out = socket.getOutputStream();
            // Send the CONNECT request.
            out.write(connect.getBytes("UTF-8"));
            out.write(CRLF);
            out.write(hostHeader.getBytes("UTF-8"));
            out.write(CRLF);
            // Auth headers
            if (proxyUser != null && proxyPass != null) {
                String header = "Proxy-Authorization: Basic "
                        + Base64.encode(proxyUser + ":" + proxyPass);
                out.write(header.getBytes("UTF-8"));
                out.write(CRLF);
            }
            out.write(CRLF);
            out.flush();
            // Get the proxy response.
            ArrayList responseLines = new ArrayList();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(in));
            for (String line = reader.readLine(); line != null
                    && line.length() > 0; line = reader.readLine()) {
                responseLines.add(line);
            }
            // Parse the response.
            int size = responseLines.size();
            if (size < 1) {
                throw new IOException(
                        "HTTPTunnelConnector: invalid proxy response");
            }
            String code = null;
            String response = (String) responseLines.get(0);
            if (response.startsWith("HTTP/") && response.length() >= 12) {
                code = response.substring(9, 12);
            } else {
                throw new IOException(
                        "HTTPTunnelConnector: invalid proxy response");
            }
            if (!"200".equals(code)) {
                StringBuffer msg = new StringBuffer();
                msg.append("HTTPTunnelConnector: connection failed\r\n");
                msg.append("Response received from the proxy:\r\n");
                for (int i = 0; i < size; i++) {
                    String line = (String) responseLines.get(i);
                    msg.append(line);
                    msg.append("\r\n");
                }
                throw new IOException(msg.toString());
            }
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

    public Socket connectForCommunicationChannel(String host, int port)
            throws IOException {
        return httpConnect(host, port, false);
    }

    public Socket connectForDataTransferChannel(String host, int port)
            throws IOException {
        return httpConnect(host, port, true);
    }

}
