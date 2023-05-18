package com.chua.common.support.extra.el.baseutil.uniqueid;

public interface Uid
{
    // 该数字代表2019-01-01所具备的毫秒数，以该毫秒数作为基准
    long base       = 1548989749033l;
    int  short_mask = 0x3f;

    byte[] generateBytes();

    String generate();

    long generateLong();

    String generateDigits();
}
