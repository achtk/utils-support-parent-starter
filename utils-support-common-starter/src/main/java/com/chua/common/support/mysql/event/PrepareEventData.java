package com.chua.common.support.mysql.event;

import java.util.Arrays;

/**
 * @author <a href="https://github.com/stevenczp">Steven Cheng</a>
 */
public class PrepareEventData implements EventData {
    private boolean onePhase;
    private int formatId;
    private int gtridLength;
    private int bqualLength;
    private byte[] data;
    private String gtrid;
    private String bqual;

    public boolean isOnePhase() {
        return onePhase;
    }

    public void setOnePhase(boolean onePhase) {
        this.onePhase = onePhase;
    }

    public int getFormatId() {
        return formatId;
    }

    public void setFormatId(int formatId) {
        this.formatId = formatId;
    }

    public int getGtridLength() {
        return gtridLength;
    }

    public void setGtridLength(int gtridLength) {
        this.gtridLength = gtridLength;
    }

    public int getBqualLength() {
        return bqualLength;
    }

    public void setBqualLength(int bqualLength) {
        this.bqualLength = bqualLength;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
        gtrid = new String(data, 0, gtridLength);
        bqual = new String(data, gtridLength, bqualLength);
    }

    public String getGtrid() {
        return gtrid;
    }

    public String getBqual() {
        return bqual;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("XAPrepareEventData{");
        sb.append("onePhase=").append(onePhase);
        sb.append(", formatID=").append(formatId);
        sb.append(", gtridLength=").append(gtridLength);
        sb.append(", bqualLength=").append(bqualLength);
        sb.append(", data=").append(Arrays.toString(data));
        sb.append(", gtrid='").append(gtrid).append('\'');
        sb.append(", bqual='").append(bqual).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
