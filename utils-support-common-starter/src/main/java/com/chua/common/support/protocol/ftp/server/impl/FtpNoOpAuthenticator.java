package com.chua.common.support.protocol.ftp.server.impl;

import com.chua.common.support.protocol.ftp.server.FtpConnection;
import com.chua.common.support.protocol.ftp.server.api.FtpFileSystem;
import com.chua.common.support.protocol.ftp.server.api.FtpUserAuthenticator;

import java.net.InetAddress;

/**
 * No Operation Authenticator
 *
 * Allows any user in with a predefined file system
 * @author Guilherme Chaguri
 */
public class FtpNoOpAuthenticator implements FtpUserAuthenticator {

    private final FtpFileSystem fs;

    /**
     * Creates the authenticator
     * @param fs A file system
     * @see FtpNativeFileSystem
     */
    public FtpNoOpAuthenticator(FtpFileSystem fs) {
        this.fs = fs;
    }

    @Override
    public boolean needsUsername(FtpConnection con) {
        return false;
    }

    @Override
    public boolean needsPassword(FtpConnection con, String username, InetAddress address) {
        return false;
    }

    @Override
    public FtpFileSystem authenticate(FtpConnection con, InetAddress address, String username, String password) throws AuthException {
        return fs;
    }
}
