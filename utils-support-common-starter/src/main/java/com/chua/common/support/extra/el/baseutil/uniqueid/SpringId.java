package com.chua.common.support.extra.el.baseutil.uniqueid;

import com.chua.common.support.extra.el.baseutil.StringUtil;

import java.lang.management.ManagementFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 基础类
 *
 * @author CH
 */
public class SpringId implements Uid {
    private final static int COUNT_MASK = 0x0000ffff;
    static byte[] pid = new byte[2];
    static SpringId instance = new SpringId();

    static {
        String s = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
        int pid = Integer.valueOf(s);
        SpringId.pid[0] = (byte) (pid >>> 8);
        SpringId.pid[1] = (byte) pid;
    }

    private final AtomicInteger count = new AtomicInteger(0);

    private SpringId() {
    }

    public static SpringId getInstance() {
        return instance;
    }

    /**
     * 前四个字节用于表达距离2190101的秒数，紧接着2个字节用于存储pid，然后2个字节是自增数字。这意味着在一秒内最多支撑0x0000ffff的个数。
     *
     * @return
     */
    @Override
    public byte[] generateBytes() {
        long now = System.currentTimeMillis();
        int duration = (int) ((now - BASE) / 1000);
        byte[] result = new byte[8];
        result[0] = (byte) ((duration >>> 24) & 0xff);
        result[1] = (byte) ((duration >>> 16) & 0xff);
        result[2] = (byte) ((duration >>> 8) & 0xff);
        result[3] = (byte) ((duration) & 0xff);
        result[4] = pid[0];
        result[5] = pid[1];
        int incrementAndGet = count.incrementAndGet() & COUNT_MASK;
        result[6] = (byte) ((incrementAndGet >>> 8) & 0xff);
        result[7] = (byte) ((incrementAndGet) & 0xff);
        return result;
    }

    @Override
    public String generate() {
        return StringUtil.toHexString(generateBytes());
    }

    @Override
    public long generateLong() {
        byte[] result = generateBytes();
        long tmp = ((long) result[0] & 0xff) << 56;
        tmp |= ((long) result[1] & 0xff) << 48;
        tmp |= ((long) result[2] & 0xff) << 40;
        tmp |= ((long) result[3] & 0xff) << 32;
        tmp |= ((long) result[4] & 0xff) << 24;
        tmp |= ((long) result[5] & 0xff) << 16;
        tmp |= ((long) result[6] & 0xff) << 8;
        tmp |= ((long) result[7] & 0xff);
        return tmp;
    }

    @Override
    public String generateDigits() {
        long tmp = generateLong();
        char[] value = new char[11];
        value[0] = ByteTool.toDigit((int) ((tmp >>> 58) & SHORT_MASK));
        value[1] = ByteTool.toDigit((int) ((tmp >>> 52) & SHORT_MASK));
        value[2] = ByteTool.toDigit((int) ((tmp >>> 46) & SHORT_MASK));
        value[3] = ByteTool.toDigit((int) ((tmp >>> 40) & SHORT_MASK));
        value[4] = ByteTool.toDigit((int) ((tmp >>> 34) & SHORT_MASK));
        value[5] = ByteTool.toDigit((int) ((tmp >>> 28) & SHORT_MASK));
        value[6] = ByteTool.toDigit((int) ((tmp >>> 22) & SHORT_MASK));
        value[7] = ByteTool.toDigit((int) ((tmp >>> 16) & SHORT_MASK));
        value[8] = ByteTool.toDigit((int) ((tmp >>> 10) & SHORT_MASK));
        value[9] = ByteTool.toDigit((int) ((tmp >>> 4) & SHORT_MASK));
        value[10] = ByteTool.toDigit((int) ((tmp) & 0x000000000000000f));
        return String.valueOf(value);
    }
}
