package com.chua.common.support.file.xz;

import com.chua.common.support.file.xz.simple.ARMThumb;

import java.io.InputStream;

/**
 * BCJ filter for little endian ARM-Thumb instructions.
 */
public class ARMThumbOptions extends BCJOptions {
    private static final int ALIGNMENT = 2;

    public ARMThumbOptions() {
        super(ALIGNMENT);
    }

    public AbstractFinishableOutputStream getOutputStream(AbstractFinishableOutputStream out,
                                                          ArrayCache arrayCache) {
        return new SimpleOutputStream(out, new ARMThumb(true, startOffset));
    }

    public InputStream getInputStream(InputStream in, ArrayCache arrayCache) {
        return new SimpleInputStream(in, new ARMThumb(false, startOffset));
    }

    FilterEncoder getFilterEncoder() {
        return new BCJEncoder(this, BCJCoder.ARMTHUMB_FILTER_ID);
    }
}
