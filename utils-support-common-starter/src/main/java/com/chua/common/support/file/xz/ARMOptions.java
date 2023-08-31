package com.chua.common.support.file.xz;

import com.chua.common.support.file.xz.simple.ARM;

import java.io.InputStream;

/**
 * BCJ filter for little endian ARM instructions.
 */
public class ARMOptions extends BCJOptions {
    private static final int ALIGNMENT = 4;

    public ARMOptions() {
        super(ALIGNMENT);
    }

    public AbstractFinishableOutputStream getOutputStream(AbstractFinishableOutputStream out,
                                                          ArrayCache arrayCache) {
        return new SimpleOutputStream(out, new ARM(true, startOffset));
    }

    public InputStream getInputStream(InputStream in, ArrayCache arrayCache) {
        return new SimpleInputStream(in, new ARM(false, startOffset));
    }

    FilterEncoder getFilterEncoder() {
        return new BCJEncoder(this, BCJCoder.ARM_FILTER_ID);
    }
}
