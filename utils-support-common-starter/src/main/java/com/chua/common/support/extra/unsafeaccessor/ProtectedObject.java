package com.chua.common.support.extra.unsafeaccessor;

class ProtectedObject {
    boolean trusted;

    void checkTrusted() {
        if (!trusted) throw new SecurityException();
    }

    public ProtectedObject(boolean t) {
        this.trusted = t;
    }

    public ProtectedObject() {
        this(true);
    }
}
