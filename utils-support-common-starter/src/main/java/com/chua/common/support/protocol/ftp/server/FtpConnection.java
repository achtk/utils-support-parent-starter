package com.chua.common.support.protocol.ftp.server;

import com.chua.common.support.protocol.ftp.server.api.FtpCommandInfo;
import com.chua.common.support.protocol.ftp.server.api.FtpCommandInfo.ArgsArrayCommand;
import com.chua.common.support.protocol.ftp.server.api.FtpCommandInfo.Command;
import com.chua.common.support.protocol.ftp.server.api.FtpCommandInfo.NoArgsCommand;
import com.chua.common.support.protocol.ftp.server.api.FtpFileSystem;
import com.chua.common.support.protocol.ftp.server.api.FtpResponseException;
import com.chua.common.support.protocol.ftp.server.api.FtpUserAuthenticator;
import com.chua.common.support.protocol.ftp.server.handler.FtpConnectionHandler;
import com.chua.common.support.protocol.ftp.server.handler.FtpFileHandler;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.*;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_MINUS_CHAR;
import static com.chua.common.support.constant.NumberConstant.NUM_2;

/**
 * Represents a FTP user connected to the server
 *
 * @author Guilherme Chaguri
 */
public class FtpConnection implements Closeable {

    protected final Map<String, FtpCommandInfo> commands = new HashMap<>();
    protected final Map<String, FtpCommandInfo> siteCommands = new HashMap<>();
    protected final List<String> features = new ArrayList<>();
    protected final Map<String, String> options = new HashMap<>();

    protected final FtpServer server;
    protected Socket con;
    protected BufferedReader reader;
    protected BufferedWriter writer;
    protected final ConnectionThread thread;
    protected final ArrayDeque<Socket> dataConnections = new ArrayDeque<>();

    protected FtpConnectionHandler conHandler;
    protected FtpFileHandler ftpFileHandler;

    protected long bytesTransferred = 0;
    protected boolean responseSent = true;
    protected int timeout = 0;
    protected int bufferSize = 0;
    protected long lastUpdate = 0;

    /**
     * Creates a new FTP connection.
     *
     * Initialized by a {@link FtpServer}
     *
     * @param server The server which received the connection
     * @param con The connection socket
     * @param idleTimeout The timeout in milliseconds
     * @param bufferSize The buffer size in bytes
     * @throws IOException When an I/O error occurs
     */
    public FtpConnection(FtpServer server, Socket con, int idleTimeout, int bufferSize) throws IOException {
        this.server = server;
        this.con = con;
        this.reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        this.writer = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));

        this.timeout = idleTimeout;
        this.bufferSize = bufferSize;
        this.lastUpdate = System.currentTimeMillis();
        con.setSoTimeout(timeout);

        this.conHandler = new FtpConnectionHandler(this);
        this.ftpFileHandler = new FtpFileHandler(this);

        this.thread = new ConnectionThread();
        this.thread.start();

        registerCommand("SITE", "SITE <command>", this::site);
        registerCommand("FEAT", "FEAT", this::feat, false);
        registerCommand("OPTS", "OPTS <option> [value]", this::opts);

        registerFeature("feat");
        registerFeature("UTF8");
        registerOption("UTF8", "ON");

        this.conHandler.registerCommands();
        this.ftpFileHandler.registerCommands();
        this.conHandler.onConnected();
    }

    /**
     * Creates a new FTP connection.
     *
     * @param server The server which received the connection
     * @param con The connection socket
     * @param idleTimeout The timeout in milliseconds
     * @throws IOException When an I/O error occurs
     * @deprecated Use {@link #FtpConnection(FtpServer, Socket, int, int)} instead
     */
    @Deprecated
    public FtpConnection(FtpServer server, Socket con, int idleTimeout) throws IOException {
        this(server, con, idleTimeout, 1024);
    }

    /**
     * Gets the server which the connection belongs
     * @return The {@link FtpServer} that received this connection
     */
    public FtpServer getServer() {
        return server;
    }

    /**
     * Gets the connection address
     * @return The {@link InetAddress} of this connection
     */
    public InetAddress getAddress() {
        return con.getInetAddress();
    }

    /**
     * Gets the amount of bytes sent or received
     * @return The number of bytes
     */
    public long getBytesTransferred() {
        return bytesTransferred;
    }

    /**
     * Gets whether the connection is authenticated
     * @return {@code true} when it's authenticated, {@code false} otherwise
     */
    public boolean isAuthenticated() {
        return conHandler.isAuthenticated();
    }

    /**
     * Gets the username of the connection.
     * @return The username or {@code null}
     */
    public String getUsername() {
        return conHandler.getUsername();
    }

    /**
     * Whether the connection is in ASCII instead of Binary
     * @return {@code true} for ASCII, {@code false} for Binary
     */
    public boolean isAsciiMode() {
        return conHandler.isAsciiMode();
    }

    /**
     * The file system of the connection. May be {@code null} when it's still authenticating
     * @return The current file system
     */
    public FtpFileSystem getFileSystem() {
        return ftpFileHandler.getFileSystem();
    }

    /**
     * Sets the new file system for this connection.
     *
     * Calling this method can result into desynchronization for the connection.
     *
     * Use an {@link FtpUserAuthenticator} for custom file systems.
     *
     * @param fs The new file system
     */
    public void setFileSystem(FtpFileSystem fs) {
        ftpFileHandler.setFileSystem(fs);
    }

    public boolean isSslEnabled() {
        return con instanceof SSLSocket;
    }

    public void enableSsl(SSLContext context) throws IOException {
        SSLSocketFactory factory = context.getSocketFactory();
        con = factory.createSocket(con, con.getInetAddress().getHostAddress(), con.getPort(), true);
        ((SSLSocket)con).setUseClientMode(false);

        reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
    }

    /**
     * Sends a response to the connection
     * @param code The response code
     * @param response The response message
     */
    public void sendResponse(int code, String response) {
        if(con.isClosed()) {
            return;
        }

        if(response == null || response.isEmpty()) {
            response = "Unknown";
        }

        try {
            if (response.charAt(0) == SYMBOL_MINUS_CHAR) {
                writer.write(code + response + "\r\n");
            } else {
                writer.write(code + " " + response + "\r\n");
            }
            writer.flush();
        } catch(IOException ex) {
            Utils.closeQuietly(this);
        }
        responseSent = true;
    }

    /**
     * Sends an array of bytes through a data connection
     * @param data The data to be sent
     * @throws FtpResponseException When an error occurs
     */
    public void sendData(byte[] data) throws FtpResponseException {
        if(con.isClosed()) {
            return;
        }

        Socket socket = null;
        try {
            socket = conHandler.createDataSocket();
            dataConnections.add(socket);
            OutputStream out = socket.getOutputStream();

            Utils.write(out, data, data.length, conHandler.isAsciiMode());
            bytesTransferred += data.length;

            out.flush();
            Utils.closeQuietly(out);
            Utils.closeQuietly(socket);
        } catch(SocketException ex) {
            throw new FtpResponseException(426, "Transfer aborted");
        } catch(IOException ex) {
            throw new FtpResponseException(425, "An error occurred while transferring the data");
        } finally {
            onUpdate();
            if(socket != null) {
                dataConnections.remove(socket);
            }
        }
    }

    /**
     * Sends a stream through a data connection
     * @param in The input stream
     * @throws FtpResponseException When an error occurs
     */
    public void sendData(InputStream in) throws FtpResponseException {
        if(con.isClosed()) {
            return;
        }

        Socket socket = null;
        try {
            socket = conHandler.createDataSocket();
            dataConnections.add(socket);
            OutputStream out = socket.getOutputStream();

            byte[] buffer = new byte[bufferSize];
            int len;
            while((len = in.read(buffer)) != -1) {
                Utils.write(out, buffer, len, conHandler.isAsciiMode());
                bytesTransferred += len;
            }

            out.flush();
            Utils.closeQuietly(out);
            Utils.closeQuietly(in);
            Utils.closeQuietly(socket);
        } catch(SocketException ex) {
            throw new FtpResponseException(426, "Transfer aborted");
        } catch(IOException ex) {
            throw new FtpResponseException(425, "An error occurred while transferring the data");
        } finally {
            onUpdate();
            if(socket != null) {
                dataConnections.remove(socket);
            }
        }
    }

    /**
     * Receives a stream through the data connection
     * @param out The output stream
     * @throws FtpResponseException When an error occurs
     */
    public void receiveData(OutputStream out) throws FtpResponseException {
        if(con.isClosed()) {
            return;
        }

        Socket socket = null;
        try {
            socket = conHandler.createDataSocket();
            dataConnections.add(socket);
            InputStream in = socket.getInputStream();

            byte[] buffer = new byte[bufferSize];
            int len;
            while((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
                bytesTransferred += len;
            }

            out.flush();
            Utils.closeQuietly(out);
            Utils.closeQuietly(in);
            Utils.closeQuietly(socket);
        } catch(SocketException ex) {
            throw new FtpResponseException(426, "Transfer aborted");
        } catch(IOException ex) {
            throw new FtpResponseException(425, "An error occurred while transferring the data");
        } finally {
            onUpdate();
            if(socket != null) {
                dataConnections.remove(socket);
            }
        }
    }

    /**
     * Aborts all data transfers
     */
    public void abortDataTransfers() {
        while(!dataConnections.isEmpty()) {
            Socket socket = dataConnections.poll();
            if(socket != null) {
                Utils.closeQuietly(socket);
            }
        }
    }

    /**
     * Registers a feature line for the FEAT command
     * @param feat The feature name
     */
    public void registerFeature(String feat) {
        if(!features.contains(feat)) {
            features.add(feat);
        }
    }

    /**
     * Registers an option for the OPTS command
     * @param option The option name
     * @param value The default value
     */
    public void registerOption(String option, String value) {
        options.put(option.toUpperCase(), value);
    }

    /**
     * Gets an option which may be modified by a OPTS command
     * @param option The option name
     * @return The option value
     */
    public String getOption(String option) {
        return options.get(option.toUpperCase());
    }

    public void registerSiteCommand(String label, String help, Command cmd) {
        addSiteCommand(label, help, cmd);
    }

    public void registerSiteCommand(String label, String help, NoArgsCommand cmd) {
        addSiteCommand(label, help, cmd);
    }

    public void registerSiteCommand(String label, String help, ArgsArrayCommand cmd) {
        addSiteCommand(label, help, cmd);
    }

    public void registerCommand(String label, String help, Command cmd) {
        addCommand(label, help, cmd, true);
    }

    public void registerCommand(String label, String help, NoArgsCommand cmd) {
        addCommand(label, help, cmd, true);
    }

    public void registerCommand(String label, String help, ArgsArrayCommand cmd) {
        addCommand(label, help, cmd, true);
    }

    public void registerCommand(String label, String help, Command cmd, boolean needsAuth) {
        addCommand(label, help, cmd, needsAuth);
    }

    public void registerCommand(String label, String help, NoArgsCommand cmd, boolean needsAuth) {
        addCommand(label, help, cmd, needsAuth);
    }

    public void registerCommand(String label, String help, ArgsArrayCommand cmd, boolean needsAuth) {
        addCommand(label, help, cmd, needsAuth);
    }

    /**
     * Internally registers a SITE sub-command
     * @param label The command name
     * @param help The help message
     * @param cmd The command function
     */
    protected void addSiteCommand(String label, String help, Command cmd) {
        siteCommands.put(label.toUpperCase(), new FtpCommandInfo(cmd, help, true));
    }

    /**
     * Internally registers a command
     * @param label The command name
     * @param help The help message
     * @param cmd The command function
     * @param needsAuth Whether authentication is required to run this command
     */
    protected void addCommand(String label, String help, Command cmd, boolean needsAuth) {
        commands.put(label.toUpperCase(), new FtpCommandInfo(cmd, help, needsAuth));
    }

    /**
     * Gets the help message from a SITE command
     * @param label The command name
     * @return The help message or {@code null} if the command was not found
     */
    public String getSiteHelpMessage(String label) {
        FtpCommandInfo info = siteCommands.get(label);
        return info != null ? info.help : null;
    }

    /**
     * Gets the help message from a command
     * @param label The command name
     * @return The help message or {@code null} if the command was not found
     */
    public String getHelpMessage(String label) {
        FtpCommandInfo info = commands.get(label);
        return info != null ? info.help : null;
    }

    protected void onUpdate() {
        lastUpdate = System.currentTimeMillis();
    }

    /**
     * Processes commands
     * @param cmd The command and its arguments
     */
    protected void process(String cmd) {
        int firstSpace = cmd.indexOf(' ');
        if(firstSpace < 0) {
            firstSpace = cmd.length();
        }

        FtpCommandInfo info = commands.get(cmd.substring(0, firstSpace).toUpperCase());

        if(info == null) {
            sendResponse(502, "Unknown command");
            return;
        }

        processCommand(info, firstSpace != cmd.length() ? cmd.substring(firstSpace + 1) : "");
    }

    /**
     * SITE command
     * @param cmd The command and its arguments
     */
    protected void site(String cmd) {
        int firstSpace = cmd.indexOf(' ');
        if(firstSpace < 0) {
            firstSpace = cmd.length();
        }

        FtpCommandInfo info = siteCommands.get(cmd.substring(0, firstSpace).toUpperCase());

        if(info == null) {
            sendResponse(504, "Unknown site command");
            return;
        }

        processCommand(info, firstSpace != cmd.length() ? cmd.substring(firstSpace + 1) : "");
    }

    /**
     * FEAT command
     */
    protected void feat() {
        StringBuilder list = new StringBuilder();
        list.append("- Supported Features:\r\n");

        for(String feat : features) {
            list.append(' ').append(feat).append("\r\n");
        }

        sendResponse(211, list.toString());
        sendResponse(211, "End");
    }

    /**
     * OPTS command
     * @param opts The option
     */
    protected void opts(String[] opts) {
        if(opts.length < 1) {
            sendResponse(501, "Missing parameters");
            return;
        }

        String option = opts[0].toUpperCase();

        if (!options.containsKey(option)) {
            sendResponse(501, "No option found");
        } else if (opts.length < NUM_2) {
            sendResponse(200, options.get(option));
        } else {
            options.put(option, opts[1].toUpperCase());
            sendResponse(200, "Option updated");
        }
    }

    protected void processCommand(FtpCommandInfo info, String args) {
        if(info.needsAuth && !conHandler.isAuthenticated()) {
            sendResponse(530, "Needs authentication");
            return;
        }

        responseSent = false;

        try {
            info.command.run(info, args);
        } catch(FtpResponseException ex) {
            sendResponse(ex.getCode(), ex.getMessage());
        } catch(FileNotFoundException ex) {
            sendResponse(550, ex.getMessage());
        } catch(IOException ex) {
            sendResponse(450, ex.getMessage());
        } catch(Exception ex) {
            sendResponse(451, ex.getMessage());
            ex.printStackTrace();
        }

        if(!responseSent) {
            sendResponse(200, "Done");
        }
    }

    /**
     * Updates the connection
     */
    protected void update() {
        if(conHandler.shouldStop()) {
            Utils.closeQuietly(this);
            return;
        }

        String line;

        try {
            line = reader.readLine();
        } catch(SocketTimeoutException ex) {
            // Check if the socket has timed out
            if(!dataConnections.isEmpty() && (System.currentTimeMillis() - lastUpdate) >= timeout) {
                Utils.closeQuietly(this);
            }
            return;
        } catch(SocketException e) {
            Utils.closeQuietly(this);
            return;
        } catch(IOException ex) {
            return;
        }

        if(line == null) {
            Utils.closeQuietly(this);
            return;
        }

        if(line.isEmpty()) {
            return;
        }

        process(line);
    }

    /**
     * Stops the connection, but does not removes it from the list.
     *
     * For a complete cleanup, use {@link #close()} instead
     * @param close Whether it will close the connection
     * @throws IOException When an I/O error occurs
     */
    protected void stop(boolean close) throws IOException {
        if(!thread.isInterrupted()) {
            thread.interrupt();
        }

        conHandler.onDisconnected();

        if(close) {
            con.close();
        }
    }

    /**
     * Interrupts and disposes the connection
     * @param close Whether it will close the connection
     * @throws IOException When an I/O error occurs
     */
    protected void close(boolean close) throws IOException {
        stop(close);

        server.removeConnection(this);
    }

    /**
     * Interrupts and disposes the connection
     * @throws IOException When an I/O error occurs
     */
    @Override
    public void close() throws IOException {
        close(true);
    }

    /**
     * Thread that processes this connection
     */
    private class ConnectionThread extends Thread {
        @Override
        public void run() {
            while(!con.isClosed()) {
                update();
            }

            try {
                close(false);
            } catch(IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
