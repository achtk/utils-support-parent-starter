package com.chua.common.support.protocol.ftp.server.api;

import com.chua.common.support.protocol.ftp.server.FtpConnection;

import java.net.InetAddress;

/**
 * Represents an user authenticator.
 * You can implement your existing user database
 *
 * @author Guilherme Chaguri
 */
public interface FtpUserAuthenticator {

    /**
     * Whether the user is allowed to connect through the specified host.
     *
     * The client will not send the host address if {@link #needsUsername(FtpConnection)} returns {@code false}
     * or the client does not support custom hosts.
     *
     * @param con The FTP connection
     * @param host The host address
     * @return Whether the specified host is accepted
     */
    default boolean acceptsHost(FtpConnection con, InetAddress host) {
        return true;
    }

    /**
     * Whether this authenticator requires a username.
     *
     * @param con The FTP connection
     * @return {@code true} if this authenticator requires a username
     */
    boolean needsUsername(FtpConnection con);

    /**
     * Whether this authenticator requires a password.
     *
     * Only affects when {@link #needsUsername(FtpConnection)} is also {@code true}
     *
     * @param con The FTP connection
     * @param username The username
     * @param host The host address or {@code null} if it's not specified by the client
     * @return {@code true} if this authenticator requires a password
     */
    boolean needsPassword(FtpConnection con, String username, InetAddress host);

    /**
     * Authenticates a user synchronously.
     *
     * You can use a custom file system depending on the user.
     *
     * If the {@code host} is {@code null}, you can use a "default host" or a union of all hosts combined
     * as some clients might not support custom hosts.
     *
     * @param con The FTP connection
     * @param host The host address or {@code null} when the client didn't specified the hostname
     * @param username The username or {@code null} when {@link #needsUsername(FtpConnection)} returns false
     * @param password The password or {@code null} when {@link #needsPassword(FtpConnection, String, InetAddress)} returns false
     * @return A file system if the authentication succeeded
     * @throws AuthException When the authentication failed
     */
    FtpFileSystem authenticate(FtpConnection con, InetAddress host, String username, String password) throws AuthException;

    /**
     * The exception that should be thrown when the authentication fails
     */
    class AuthException extends RuntimeException { }

}
