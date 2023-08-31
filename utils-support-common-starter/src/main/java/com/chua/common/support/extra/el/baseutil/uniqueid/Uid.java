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

    /**
     * 生成字节
     *
     * @return {@link byte[]}
     */
    byte[] generateBytes();

    /**
     * 生成
     *
     * @return {@link String}
     */
    String generate();

    /**
     * 生成时间
     *
     * @return long
     */
    long generateLong();

    /**
     * 生成数字
     *
     * @return {@link String}
     */
    String generateDigits();
}
