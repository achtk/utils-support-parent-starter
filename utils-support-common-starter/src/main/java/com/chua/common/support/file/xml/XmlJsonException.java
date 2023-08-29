package com.chua.common.support.file.xml;

/*
Public Domain.
 */

/**
 * The JSONException is thrown by the JSON.org classes when things are amiss.
 *
 * @author JSON.org
 * @version 2015-12-09
 */
public class XmlJsonException extends RuntimeException {
    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 0;

    /**
     * Constructs a JSONException with an explanatory message.
     *
     * @param message Detail about the reason for the exception.
     */
    public XmlJsonException(final String message) {
        super(message);
    }

    /**
     * Constructs a JSONException with an explanatory message and cause.
     *
     * @param message Detail about the reason for the exception.
     * @param cause   The cause.
     */
    public XmlJsonException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new JSONException with the specified cause.
     *
     * @param cause The cause.
     */
    public XmlJsonException(final Throwable cause) {
        super(cause.getMessage(), cause);
    }

}
