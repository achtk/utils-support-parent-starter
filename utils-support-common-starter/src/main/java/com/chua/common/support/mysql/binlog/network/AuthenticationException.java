package com.chua.common.support.mysql.binlog.network;

/**
 * @author <a href="mailto:stanley.shyiko@gmail.com">Stanley Shyiko</a>
 */
public class AuthenticationException extends ServerException {

    public AuthenticationException(String message, int errorCode, String sqlState) {
        super(message, errorCode, sqlState);
    }

    public AuthenticationException(String message) {
        super(message, 0, "HY000");
    }
}
