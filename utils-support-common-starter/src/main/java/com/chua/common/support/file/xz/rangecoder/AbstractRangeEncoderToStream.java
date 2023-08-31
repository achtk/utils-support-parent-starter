/*
 * RangeEncoderToStream
 *
 * Authors: Lasse Collin <lasse.collin@tukaani.org>
 *          Igor Pavlov <http://7-zip.org/>
 *
 * This file has been put into the public domain.
 * You can do whatever you want with this file.
 */

package com.chua.common.support.file.xz.rangecoder;

import java.io.IOException;
import java.io.OutputStream;

public final class AbstractRangeEncoderToStream extends AbstractRangeEncoder {
    private final OutputStream out;

    public AbstractRangeEncoderToStream(OutputStream out) {
        this.out = out;
        reset();
    }

    void writeByte(int b) throws IOException {
        out.write(b);
    }
}
