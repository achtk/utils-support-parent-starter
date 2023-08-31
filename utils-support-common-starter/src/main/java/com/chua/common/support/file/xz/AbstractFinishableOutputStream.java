/*
 * FinishableOutputStream
 *
 * Author: Lasse Collin <lasse.collin@tukaani.org>
 *
 * This file has been put into the public domain.
 * You can do whatever you want with this file.
 */

package com.chua.common.support.file.xz;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Output stream that supports finishing without closing
 * the underlying stream.
 * @author Administrator
 */
public abstract class AbstractFinishableOutputStream extends OutputStream {
    /**
     * Finish the stream without closing the underlying stream.
     * No more data may be written to the stream after finishing.
     * <p>
     * The <code>finish</code> method of <code>FinishableOutputStream</code>
     * does nothing. Subclasses should override it if they need finishing
     * support, which is the case, for example, with compressors.
     *
     * @throws      IOException ex
     */
    public void finish() throws IOException {}
}
