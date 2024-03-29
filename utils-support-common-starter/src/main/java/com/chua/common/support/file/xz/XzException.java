/*
 * XZIOException
 *
 * Author: Lasse Collin <lasse.collin@tukaani.org>
 *
 * This file has been put into the public domain.
 * You can do whatever you want with this file.
 */

package com.chua.common.support.file.xz;

/**
 * Generic {@link java.io.IOException IOException} specific to this package.
 * The other IOExceptions in this package extend
 * from <code>XZIOException</code>.
 *
 * @author Administrator
 */
public class XzException extends java.io.IOException {
    private static final long serialVersionUID = 3L;

    public XzException() {
        super();
    }

    public XzException(String s) {
        super(s);
    }
}
