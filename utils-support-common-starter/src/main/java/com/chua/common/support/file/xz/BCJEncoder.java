package com.chua.common.support.file.xz;

class BCJEncoder extends BCJCoder implements FilterEncoder {
    private final BCJOptions options;
    private final long filterID;
    private final byte[] props;

    BCJEncoder(BCJOptions options, long filterID) {
        assert isBCJFilterID(filterID);
        int startOffset = options.getStartOffset();

        if (startOffset == 0) {
            props = new byte[0];
        } else {
            props = new byte[4];
            for (int i = 0; i < 4; ++i)
                props[i] = (byte)(startOffset >>> (i * 8));
        }

        this.filterID = filterID;
        this.options = (BCJOptions)options.clone();
    }

    public long getFilterId() {
        return filterID;
    }

    public byte[] getFilterProps() {
        return props;
    }

    public boolean supportsFlushing() {
        return false;
    }

    public AbstractFinishableOutputStream getOutputStream(AbstractFinishableOutputStream out,
                                                          ArrayCache arrayCache) {
        return options.getOutputStream(out, arrayCache);
    }
}
