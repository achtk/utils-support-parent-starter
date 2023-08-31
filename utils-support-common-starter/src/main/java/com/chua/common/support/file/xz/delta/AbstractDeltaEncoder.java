/*
 * DeltaEncoder
 *
 * Author: Lasse Collin <lasse.collin@tukaani.org>
 *
 * This file has been put into the public domain.
 * You can do whatever you want with this file.
 */

package com.chua.common.support.file.xz.delta;

/**
 * @author Administrator
 */
public class AbstractDeltaEncoder extends AbstractDeltaCoder {
    public AbstractDeltaEncoder(int distance) {
        super(distance);
    }

    public void encode(byte[] in, int inOff, int len, byte[] out) {
        for (int i = 0; i < len; ++i) {
            byte tmp = history[(distance + pos) & DISTANCE_MASK];
            history[pos-- & DISTANCE_MASK] = in[inOff + i];
            out[i] = (byte)(in[inOff + i] - tmp);
        }
    }
}
