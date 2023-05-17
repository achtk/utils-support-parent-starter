package com.chua.common.support.jsoup;

import java.io.IOException;

/**
 * @author Administrator
 */
public class UncheckedException extends RuntimeException {
    public UncheckedException(IOException cause) {
        super(cause);
    }

    public UncheckedException(String message) {
        super(new IOException(message));
    }

    public IOException ioException() {
        return (IOException) getCause();
    }
}
