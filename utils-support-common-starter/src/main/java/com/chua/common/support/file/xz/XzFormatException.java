package com.chua.common.support.file.xz;

/**
 * Thrown when the input data is not in the XZ format.
 *
 * @author Administrator
 */
public class XzFormatException extends XzException {
    private static final long serialVersionUID = 3L;

    /**
     * Creates a new exception with the default error detail message.
     */
    public XzFormatException() {
        super("Input is not in the XZ format");
    }
}
