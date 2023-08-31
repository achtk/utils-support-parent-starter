/*
 * RangeCoder
 *
 * Authors: Lasse Collin <lasse.collin@tukaani.org>
 *          Igor Pavlov <http://7-zip.org/>
 *
 * This file has been put into the public domain.
 * You can do whatever you want with this file.
 */

package com.chua.common.support.file.xz.rangecoder;

import java.util.Arrays;

/**
 * 范围编码器
 *
 * @author Administrator
 * @date 2023/08/31
 */
public abstract class BaseRangeCoder {
    static final int SHIFT_BITS = 8;
    static final int TOP_MASK = 0xFF000000;
    static final int BIT_MODEL_TOTAL_BITS = 11;
    static final int BIT_MODEL_TOTAL = 1 << BIT_MODEL_TOTAL_BITS;
    static final short PROB_INIT = (short)(BIT_MODEL_TOTAL / 2);
    static final int MOVE_BITS = 5;

    public static final void initProbs(short[] probs) {
        Arrays.fill(probs, PROB_INIT);
    }
}
