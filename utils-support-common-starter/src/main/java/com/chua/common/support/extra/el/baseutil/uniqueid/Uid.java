package com.chua.common.support.extra.el.baseutil.uniqueid;
/**
 * 基础类
 * @author CH
 */
public interface Uid {
    /**
     * 该数字代表2019-01-01所具备的毫秒数，以该毫秒数作为基准
     */
    long BASE = 1548989749033L;
    int SHORT_MASK = 0x3f;

    byte[] generateBytes();

    String generate();

    long generateLong();

    String generateDigits();
}
