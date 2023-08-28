package com.chua.common.support.protocol.ftp.server;

import com.chua.common.support.protocol.ftp.server.api.FtpListener;
import com.chua.common.support.protocol.ftp.server.api.FtpUserAuthenticator;
import com.chua.common.support.protocol.ftp.server.impl.FtpNoOpAuthenticator;

import javax.net.ssl.SSLContext;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * FTP Server
 * @author Guilherme Chaguri
 */
public class FtpServer implements Closeable {

    protected final List<FtpConnection> connections = Collections.synchronizedList(new ArrayList<FtpConnection>());
    protected final List<FtpListener> listeners = Collections.synchronizedList(new ArrayList<FtpListener>());

    protected FtpUserAuthenticator auth = null;
    protected int idleTimeout = 5 * 60 * 1000; 
    protected int bufferSize = 1024;
    protected SSLContext ssl = null;
    protected boolean explicitSecurity = true;

    protected ServerSocket socket = null;
    protected ServerThread serverThread = null;

    /**
     * Creates a new server
     */
    public FtpServer() {

    }

    /**
     * Creates a new server
     * @param auth An authenticator
     */
    public FtpServer(FtpUserAuthenticator auth) {
        setAuthenticator(auth);
    }

    /**
     * Gets the server address
     * @return The server address or {@code null} if the server is not running
     */
    public InetAddress getAddress() {
        return socket != null ? socket.getInetAddress() : null;
    }

    /**
     * Gets the server port
     * @return The server port or {@code -1} if the server is not running
     */
    public int getPort() {
        return socket != null ? socket.getLocalPort() : -1;
    }

    /**
     * Gets the current authenticator instance.
     * @return The authenticator
     */
    public FtpUserAuthenticator getAuthenticator() {
        return auth;
    }

    /**
     * Sets the authenticator instance.
     *
     * Not only you can have your own user database, but you can also
     * provide a different file system depending on the user.
     *
     * @param auth The authenticator
     * @see FtpNoOpAuthenticator
     */
    public void setAuthenticator(FtpUserAuthenticator auth) {
        if(auth == null) {
            throw new NullPointerException("The Authenticator is null");
        }
        this.auth = auth;
    }

    /**
     * Gets the SSL context
     * @return The context
     */
    public SSLContext getSslContext() {
        return ssl;
    }

    /**
     * Sets the SSL context for secure connections.
     *
     * This is required for supporting TLS/SLL.
     *
     * @param ssl The context
     */
    public void setSslContext(SSLContext ssl) {
        this.ssl = ssl;
    }

    /**
     * Sets whether the security will be explicit or implicit.
     *
     * A server in explicit mode will support both secure and insecure connections.
     * A server in implicit mode will only support secure connections.
     *
     * In order to support SSL, a context must be given with {@link #setSslContext(SSLContext)}
     *
     * @param explicit {@code true} to support all connections, {@code false} otherwise
     */
    public void setExplicitSsl(boolean explicit) {
        this.explicitSecurity = explicit;
    }

    /**
     * Sets the idle timeout in milliseconds
     *
     * Connections that are idle (no commands or transfers) for the specified time will be disconnected.
     *
     * The default and recommended time is 5 minutes.
     *
     * @param idleTimeout The time in milliseconds
     */
    public void setTimeout(int idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    /**
     * Sets the default buffer size in bytes
     *
     * The default value is 1024 bytes
     *
     * @param bufferSize The buffer size in bytes
     */
    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    /**
     * Adds an {@link FtpListener} to the server
     * @param listener The listener instance
     */
    public void addListener(FtpListener listener) {
        synchronized(listeners) {
            listeners.add(listener);
        }
    }

    /**
     * Removes an {@link FtpListener} to the server
     * @param listener The listener instance
     */
    public void removeListener(FtpListener listener) {
        synchronized(listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * Starts the FTP server asynchronously.
     *
     * @param port The server port
     * @throws IOException When an error occurs while starting the server
     */
    public void listen(int port) throws IOException {
        listen(null, port);
    }

    /**
     * Starts the FTP server asynchronously.
     *
     * @param address The server address or {@code null} for a local address
     * @param port The server port or {@code 0} to automatically allocate the port
     * @throws IOException When an error occurs while starting the server
     */
    public void listen(InetAddress address, int port) throws IOException {
        if(auth == null) {
            throw new NullPointerException("The Authenticator is null");
        }
        if(socket != null) {
            throw new IOException("Server already started");
        }

        socket = Utils.createServer(port, 50, address, ssl, !explicitSecurity);

        serverThread = new ServerThread();
        serverThread.setDaemon(true);
        serverThread.start();
    }

    /**
     * Starts the FTP server synchronously, blocking the current thread.
     *
     * Connections to the server will still create new threads.
     *
     * @param port The server port
     * @throws IOException When an error occurs while starting the server
     */
    public void listenSync(int port) throws IOException {
        listenSync(null, port);
    }

    /**
     * Starts the FTP server synchronously, blocking the current thread.
     *
     * Connections to the server will still create new threads.
     *
     * @param address The server address or {@code null} for a local address
     * @param port The server port or {@code 0} to automatically allocate the port
     * @throws IOException When an error occurs while starting the server
     */
    public void listenSync(InetAddress address, int port) throws IOException {
        if(auth == null) {
            throw new NullPointerException("The Authenticator is null");
        }
        if(socket != null) {
            throw new IOException("Server already started");
        }

        socket = Utils.createServer(port, 50, address, ssl, !explicitSecurity);

        while(!socket.isClosed()) {
            update();
        }
    }

    /**
     * Updates the server
     */
    protected void update() {
        try {
            addConnection(socket.accept());
        } catch(IOException ex) {
            
        }
    }

    /**
     * Creates a {@link FtpConnection} instance.
     *
     * Feel free to override this method with your own custom implementation
     *
     * @param socket The connection socket
     * @return The {@link FtpConnection} instance
     * @throws IOException When an error occurs
     */
    protected FtpConnection createConnection(Socket socket) throws IOException {
        return new FtpConnection(this, socket, idleTimeout, bufferSize);
    }

    /**
     * Called when a connection is created.
     *
     * @param socket The connection socket
     * @throws IOException When an error occurs
     */
    protected void addConnection(Socket socket) throws IOException {
        FtpConnection con = createConnection(socket);

        synchronized(listeners) {
            for(FtpListener l : listeners) {
                l.onConnected(con);
            }
        }
        synchronized(connections) {
            connections.add(con);
        }
    }

    /**
     * Called when a connection is terminated
     * @param con The FTP connection
     * @throws IOException When an error occurs
     */
    protected void removeConnection(FtpConnection con) throws IOException {
        synchronized(listeners) {
            for(FtpListener l : listeners) {
                l.onDisconnected(con);
            }
        }
        synchronized(connections) {
            connections.remove(con);
        }
    }

    /**
     * Starts disposing server resources.
     *
     * For a complete cleanup, use {@link #close()} instead
     */
    protected void dispose() {
        
        if(serverThread != null) {
            serverThread.interrupt();
            serverThread = null;
        }

        
        synchronized(connections) {
            for(FtpConnection con : connections) {
                try {
                    con.stop(true);
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
            connections.clear();
        }
    }

    /**
     * Stops the server and dispose its resources.
     * @throws IOException When an I/O error occurs
     */
    @Override
    public void close() throws IOException {
        dispose();

        if(socket != null) {
            socket.close();
            socket = null;
        }
    }

    /**
     * Thread that processes this server when listening asynchronously
     */
    private class ServerThread extends Thread {
        @Override
        public void run() {
            while(socket != null && !socket.isClosed()) {
                update();
            }
        }
    }

}
