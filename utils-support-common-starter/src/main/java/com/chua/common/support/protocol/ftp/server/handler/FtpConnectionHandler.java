package com.chua.common.support.protocol.ftp.server.handler;

import com.chua.common.support.protocol.ftp.server.FtpConnection;
import com.chua.common.support.protocol.ftp.server.FtpServer;
import com.chua.common.support.protocol.ftp.server.Utils;
import com.chua.common.support.protocol.ftp.server.api.FtpUserAuthenticator;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import static com.chua.common.support.constant.NameConstant.SITE;
import static com.chua.common.support.net.NetUtils.*;

/**
 * Handles special connection-based commands
 * @author Guilherme Chaguri
 */
public class FtpConnectionHandler {

    private final FtpConnection con;

    private InetAddress address = null;
    private boolean authenticated = false;
    private String username = null;

    private boolean passive = false;
    private ServerSocket passiveServer = null;
    private String activeHost = null;
    private int activePort = 0;

    private boolean ascii = true;
    private boolean secureData = false;
    private boolean stop = false;

    public FtpConnectionHandler(FtpConnection connection) {
        this.con = connection;
    }

    public boolean shouldStop() {
        return stop;
    }

    public boolean isAsciiMode() {
        return ascii;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public String getUsername() {
        return username;
    }

    public Socket createDataSocket() throws IOException {
        if(passive && passiveServer != null) {
            return passiveServer.accept();
        } else if(secureData) {
            SSLSocketFactory factory = con.getServer().getSslContext().getSocketFactory();
            SSLSocket socket = (SSLSocket)factory.createSocket(activeHost, activePort);
            socket.setUseClientMode(false);
            return socket;
        } else {
            return new Socket(activeHost, activePort);
        }
    }

    public void onConnected() throws IOException {
        FtpUserAuthenticator auth = con.getServer().getAuthenticator();

        if(!auth.needsUsername(con)) {
            if(authenticate(auth, null)) {
                con.sendResponse(230, "Ready!");
            } else {
                con.sendResponse(421, "Authentication failed");
                con.close();
            }
        } else {
            con.sendResponse(220, "Waiting for authentication...");
        }
    }

    public void onDisconnected() throws IOException {
        if(passiveServer != null) {
            Utils.closeQuietly(passiveServer);
            passiveServer = null;
        }
    }

    public void registerCommands() {
        con.registerCommand("NOOP", "NOOP", this::noop, false); 
        con.registerCommand("HELP", "HELP <command>", this::help, false); 
        con.registerCommand("QUIT", "QUIT", this::quit, false); 
        con.registerCommand("REIN", "REIN", this::rein, false); 
        con.registerCommand("USER", "USER <username>", this::user, false); 
        con.registerCommand("PASS", "PASS <password>", this::pass, false); 
        con.registerCommand("ACCT", "ACCT <info>", this::acct, false); 
        con.registerCommand("SYST", "SYST", this::syst); 
        con.registerCommand("PASV", "PASV", this::pasv); 
        con.registerCommand("PORT", "PORT <address>", this::port); 
        con.registerCommand("TYPE", "TYPE <type>", this::type); 
        con.registerCommand("STRU", "STRU <type>", this::stru); 
        con.registerCommand("MODE", "MODE <mode>", this::mode); 
        con.registerCommand("STAT", "STAT", this::stat); 

        con.registerCommand("AUTH", "AUTH <mechanism>", this::auth, false); 
        con.registerCommand("PBSZ", "PBSZ <size>", this::pbsz, false); 
        con.registerCommand("PROT", "PROT <level>", this::prot, false); 

        con.registerCommand("LPSV", "LPSV", this::lpsv); 
        con.registerCommand("LPRT", "LPRT <address>", this::lprt); 

        con.registerCommand("EPSV", "EPSV", this::epsv); 
        con.registerCommand("EPRT", "EPRT <address>", this::eprt); 

        con.registerCommand("HOST", "HOST <address>", this::host, false); 

        con.registerFeature("base"); 
        con.registerFeature("secu"); 
        con.registerFeature("hist"); 
        con.registerFeature("nat6"); 
        con.registerFeature("TYPE A;AN;AT;AC;L;I"); 
        con.registerFeature("AUTH TLS"); 
        con.registerFeature("PBSZ"); 
        con.registerFeature("PROT"); 
        con.registerFeature("EPSV"); 
        con.registerFeature("EPRT"); 
        con.registerFeature("HOST"); 
    }

    private void noop() {
        con.sendResponse(200, "OK");
    }

    private void help(String[] cmd) {
        if(cmd.length < 1) {
            con.sendResponse(501, "Missing parameters");
        }

        String command = cmd[0].toUpperCase();
        String help;

        if(cmd.length > 1 && SITE.equals(command)) {
            help = "SITE " + con.getSiteHelpMessage(cmd[1].toUpperCase());
        } else {
            help = con.getHelpMessage(command);
        }
        con.sendResponse(214, help);
    }

    private void type(String type) throws IOException {
        type = type.toUpperCase();

        String a  = "A";
        String l  = "L";
        String i  = "I";
        if(type.startsWith(a)) {
            ascii = true;
        } else if(type.startsWith(l) || type.startsWith(i)) {
            ascii = false;
        } else {
            con.sendResponse(500, "Unknown type " + type);
            return;
        }
        con.sendResponse(200, "Type set to " + type);
    }

    private void stru(String structure) throws IOException {
        String f = "F";
        if(f.equalsIgnoreCase(structure)) {
            con.sendResponse(200, "The structure type was set to file");
            return;
        }
        con.sendResponse(504, "Unsupported structure type");
    }

    private void mode(String mode) throws IOException {
        String s = "S";
        if(s.equalsIgnoreCase(mode)) {
            con.sendResponse(200, "The mode was set to stream");
            return;
        }
        con.sendResponse(504, "Unsupported mode");
    }

    private void host(String host) throws IOException {
        if(authenticated) {
            con.sendResponse(503, "The user is already authenticated");
            return;
        }

        try {
            FtpUserAuthenticator auth = con.getServer().getAuthenticator();
            InetAddress address = InetAddress.getByName(host);

            if(auth.acceptsHost(con, address)) {
                this.address = address;
                con.sendResponse(220, "Host accepted");
            } else {
                this.address = null;
                con.sendResponse(504, "Host denied");
            }
        } catch(UnknownHostException ex) {
            con.sendResponse(501, "Invalid host");
        }
    }

    private void user(String username) throws IOException {
        if(authenticated) {
            con.sendResponse(230, "Logged in!");
            return;
        }

        this.username = username;

        FtpUserAuthenticator auth = con.getServer().getAuthenticator();
        if(auth.needsPassword(con, username, address)) {
            
            con.sendResponse(331, "Needs a password");
        } else {
            
            boolean success = authenticate(auth, null);

            if(success) {
                con.sendResponse(230, "Logged in!");
            } else {
                con.sendResponse(530, "Authentication failed");
                con.close();
            }
        }
    }

    private void pass(String password) throws IOException {
        if(authenticated) {
            con.sendResponse(230, "Logged in!");
            return;
        }

        
        boolean success = authenticate(con.getServer().getAuthenticator(), password);

        if(success) {
            con.sendResponse(230, "Logged in!");
        } else {
            con.sendResponse(530, "Authentication failed");
            con.close();
        }
    }

    private void acct(String info) {
        if(authenticated) {
            con.sendResponse(230, "Logged in!");
            return;
        }

        
        
        

        
        
        

        con.sendResponse(530, "Account information is not supported");
    }

    private void syst() {
        con.sendResponse(215, "UNIX Type: L8"); 
    }

    private void rein() {
        authenticated = false;
        username = null;
        address = null;
        con.sendResponse(220, "Ready for a new user");
    }

    private void quit() {
        con.sendResponse(221, "Closing connection...");
        stop = true;
    }

    private void pasv() throws IOException {
        FtpServer server = con.getServer();
        passiveServer = Utils.createServer(0, 5, server.getAddress(), server.getSslContext(), secureData);
        passive = true;

        String host = passiveServer.getInetAddress().getHostAddress();
        int port = passiveServer.getLocalPort();

        if(ANY_HOST.equals(host)) {
            
            host = InetAddress.getLocalHost().getHostAddress();
        }

        String[] addr = host.split("\\.");

        String address = addr[0] + "," + addr[1] + "," + addr[2] + "," + addr[3];
        String addressPort = port / 256 + "," + port % 256;

        con.sendResponse(227, "Enabled Passive Mode (" + address + "," + addressPort + ")");
    }

    private void port(String data) {
        String[] args = data.split(",");

        activeHost = args[0] + "." + args[1] + "." + args[2] + "." + args[3];
        activePort = Integer.parseInt(args[4]) * 256 + Integer.parseInt(args[5]);
        passive = false;

        if(passiveServer != null) {
            Utils.closeQuietly(passiveServer);
            passiveServer = null;
        }
        con.sendResponse(200, "Enabled Active Mode");
    }

    private void stat() throws IOException {
        con.sendResponse(211, "Sending the status...");

        String ip = con.getAddress().getHostAddress();
        String user = username != null ? "as " + username : "anonymously";
        String type = ascii ? "ASCII" : "Binary";

        String data = "";
        data += "Connected from " + ip + " (" + ip + ")\r\n";
        data += "Logged in " + user + "\r\n";
        data += "TYPE: " + type + ", STRUcture: File, MODE: Stream\r\n";
        data += "Total bytes transferred for session: " + con.getBytesTransferred() + "\r\n";
        con.sendData(data.getBytes("UTF-8"));

        con.sendResponse(211, "Status sent!");
    }

    private void auth(String mechanism) throws IOException {
        mechanism = mechanism.toUpperCase();

        if(TLS.equals(mechanism) || TLS_C.equals(mechanism) ||
                SLL.equals(mechanism) || TLS_P.equals(mechanism)) {
            

            SSLContext ssl = con.getServer().getSslContext();

            if(ssl == null) {
                con.sendResponse(431, "TLS/SSL is not available");
            } else if(con.isSslEnabled()) {
                con.sendResponse(503, "TLS/SSL is already enabled");
            } else {
                con.sendResponse(234, "Enabling TLS/SSL...");
                con.enableSsl(ssl);
            }

        } else {
            con.sendResponse(502, "Unsupported mechanism");
        }
    }

    private void pbsz(String size) {
        if(con.isSslEnabled()) {
            
            
            con.sendResponse(200, "The protection buffer size was set to 0");
        } else {
            con.sendResponse(503, "You can't set the protection buffer size in an insecure connection");
        }
    }

    private void prot(String level) {
        level = level.toUpperCase();

        String c = "C", p = "P", s = "S", e = "E";
        if(!con.isSslEnabled()) {
            con.sendResponse(503, "You can't update the protection level in an insecure connection");
        } else if(c.equals(level)) {
            secureData = false;
            con.sendResponse(200, "Protection level set to clear");
        } else if(p.equals(level)) {
            secureData = true;
            con.sendResponse(200, "Protection level set to private");
        } else if(s.equals(level) || e.equals(level)) {
            con.sendResponse(521, "Unsupported protection level");
        } else {
            con.sendResponse(502, "Unknown protection level");
        }
    }

    private void lpsv() throws IOException { 
        FtpServer server = con.getServer();
        passiveServer = Utils.createServer(0, 5, server.getAddress(), server.getSslContext(), secureData);
        passive = true;

        String host = passiveServer.getInetAddress().getHostAddress();
        int port = passiveServer.getLocalPort();

        if(ANY_HOST.equals(host)) {
            
            host = InetAddress.getLocalHost().getHostAddress();
        }

        String[] addr = host.split("\\.");

        String address = addr[0] + "," + addr[1] + "," + addr[2] + "," + addr[3];
        String addressPort = port / 256 + "," + port % 256;

        con.sendResponse(229, "Enabled Passive Mode (4,4," + address + ",2," + addressPort + ")");
    }

    private void lprt(String data) { 
        String[] args = data.split(",");

        int hostLength = Integer.parseInt(args[1]);
        int portLength = Integer.parseInt(args[hostLength + 2]);

        String host = "";
        for(int i = 0; i < hostLength; i++) {
            host += "." + args[i + 2];
        }
        activeHost = host.substring(1);

        int port = 0;
        for(int i = 0; i < portLength; i++) {
            int num = Integer.parseInt(args[i + hostLength + 3]);
            int pos = (portLength - i - 1) * 8;
            port |= num << pos;
        }
        activePort = port;

        passive = false;

        if(passiveServer != null) {
            Utils.closeQuietly(passiveServer);
            passiveServer = null;
        }
        con.sendResponse(200, "Enabled Active Mode");
    }

    private void epsv() throws IOException {
        FtpServer server = con.getServer();
        passiveServer = Utils.createServer(0, 5, server.getAddress(), server.getSslContext(), secureData);
        passive = true;

        con.sendResponse(229, "Enabled Passive Mode (|||" + passiveServer.getLocalPort() + "|)");
    }

    private void eprt(String data) {
        char delimiter = data.charAt(0);
        String[] args = data.split(String.format("\\%s", delimiter));

        activeHost = args[2];
        activePort = Integer.parseInt(args[3]);
        passive = false;

        if(passiveServer != null) {
            Utils.closeQuietly(passiveServer);
            passiveServer = null;
        }

        con.sendResponse(200, "Enabled Active Mode");
    }

    private boolean authenticate(FtpUserAuthenticator auth, String password) {
        try {
            con.setFileSystem(auth.authenticate(con, address, username, password));
            authenticated = true;
            return true;
        } catch(FtpUserAuthenticator.AuthException ex) {
            return false;
        } catch(Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
