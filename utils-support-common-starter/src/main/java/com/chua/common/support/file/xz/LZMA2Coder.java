/*
 * LZMA2Coder
 *
 * Author: Lasse Collin <lasse.collin@tukaani.org>
 *
 * This file has been put into the public domain.
 * You can do whatever you want with this file.
 */

package com.chua.common.support.file.xz;

abstract class LZMA2Coder implements FilterCoder {
    public static final long FILTER_ID = 0x21;

    public boolean changesSize() {
        return true;
    }

    public boolean nonLastOk() {
        return false;
    }

    public boolean lastOk() {
        return true;
    }
}
