package com.chua.example.bytes;

import com.chua.common.support.binary.Bit;
import com.chua.common.support.utils.ByteUtils;

import static com.chua.common.support.constant.NumberConstant.NUM_4;

/**
 * ais日期
 *
 * @author CH
 */
public class AisDate {

    private byte[] date;

    public AisDate(int dateStr) {
        this(ByteUtils.asBytes(dateStr));
    }
    public AisDate(byte[] date) {
        this.date = date;
        if(date.length < NUM_4) {
            throw new IllegalArgumentException("byte 数组不得小于20");
        }
    }


    /**
     * 获取月份
     *
     * @return int
     */
    public int getMonth() {
        Bit bit = Bit.of(date);
        return bit.sub(16, 19).asByte();
    }

    /**
     * 获取日期
     *
     * @return int
     */
    public int getDay() {
        Bit bit = Bit.of(date);
        return bit.sub(11, 15).asByte();
    }

    /**
     * 获取小时
     *
     * @return int
     */
    public int getHour() {
        Bit bit = Bit.of(date);
        return bit.sub(6, 10).asByte();
    }

    /**
     * 获取分
     *
     * @return int
     */
    public int getMin() {
        Bit bit = Bit.of(date);
        return bit.sub(0, 5).asByte();
    }
}
