/*
 * SPARCOptions
 *
 * Author: Lasse Collin <lasse.collin@tukaani.org>
 *
 * This file has been put into the public domain.
 * You can do whatever you want with this file.
 */

package com.chua.common.support.file.xz;

import com.chua.common.support.file.xz.simple.SPARC;

import java.io.InputStream;

/**
 * BCJ filter for SPARC.
 */
public class SPARCOptions extends BCJOptions {
    private static final int ALIGNMENT = 4;

    public SPARCOptions() {
        super(ALIGNMENT);
    }

    public AbstractFinishableOutputStream getOutputStream(AbstractFinishableOutputStream out,
                                                          ArrayCache arrayCache) {
        return new SimpleOutputStream(out, new SPARC(true, startOffset));
    }

    public InputStream getInputStream(InputStream in, ArrayCache arrayCache) {
        return new SimpleInputStream(in, new SPARC(false, startOffset));
    }

    FilterEncoder getFilterEncoder() {
        return new BCJEncoder(this, BCJCoder.SPARC_FILTER_ID);
    }
}
