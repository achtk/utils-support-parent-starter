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
