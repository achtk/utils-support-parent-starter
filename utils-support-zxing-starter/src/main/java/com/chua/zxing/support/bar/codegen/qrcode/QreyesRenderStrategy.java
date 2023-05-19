package com.chua.zxing.support.bar.codegen.qrcode;

public enum QreyesRenderStrategy {

    POINT(2, 5), POINT_BORDER(0, 7);

    private int start;

    private int end;

    private QreyesRenderStrategy(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

}
