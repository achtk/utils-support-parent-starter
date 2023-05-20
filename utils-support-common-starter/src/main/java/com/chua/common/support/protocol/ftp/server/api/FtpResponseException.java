package com.chua.common.support.protocol.ftp.server.api;

import com.chua.common.support.protocol.ftp.server.FtpConnection;

import java.io.IOException;

/**
 * This exception can be thrown to send a response to the client.
 * Throwing this exception is the same as calling {@link FtpConnection#sendResponse(int, String)}
 * @author Guilherme Chaguri
 */
public class FtpResponseException extends IOException {

    private final int code;

    public FtpResponseException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
