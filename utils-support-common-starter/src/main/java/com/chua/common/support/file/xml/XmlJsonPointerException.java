package com.chua.common.support.file.xml;

/*
Public Domain.
*/

/**
 * The JSONPointerException is thrown by {@link XmlJsonPointer} if an error occurs
 * during evaluating a pointer.
 *
 * @author JSON.org
 * @version 2016-05-13
 */
public class XmlJsonPointerException extends XmlJsonException {
    private static final long serialVersionUID = 8872944667561856751L;

    public XmlJsonPointerException(String message) {
        super(message);
    }

    public XmlJsonPointerException(String message, Throwable cause) {
        super(message, cause);
    }

}
