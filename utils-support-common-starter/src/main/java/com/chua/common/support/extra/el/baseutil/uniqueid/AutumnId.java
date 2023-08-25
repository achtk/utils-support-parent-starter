package com.chua.common.support.extra.el.baseutil.uniqueid;

import com.chua.common.support.extra.el.baseutil.StringUtil;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class AutumnId implements Uid {
    private static final int pid;
    private static final byte[] internal = new byte[5];
    private static final AtomicInteger count = new AtomicInteger(0);
    private static final int mask = 0x0000ffff;
    private static volatile AutumnId INSTANCE;

    static {
        String s = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
        pid = Integer.valueOf(s);
        // 本机mac地址的hash 32个bit
        int maxHash;
        try {
            maxHash = StringUtil.toHexString(NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress()).hashCode();
        } catch (Exception e) {
            maxHash = new Random().nextInt();
        }
        internal[0] = (byte) (pid >>> 8);
        internal[1] = (byte) pid;
        internal[2] = (byte) (maxHash >>> 16);
        internal[3] = (byte) (maxHash >>> 8);
        internal[4] = (byte) (maxHash);
    }

    private AutumnId() {
    }

    public static final AutumnId instance() {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        synchronized (count) {
            if (INSTANCE != null) {
                return INSTANCE;
            }
            INSTANCE = new AutumnId();
            return INSTANCE;
        }
    }

    @Override
    public byte[] generateBytes() {
        byte[] result = new byte[12];
        long time = System.currentTimeMillis() - base;
        result[0] = (byte) (time >>> 32);
        result[1] = (byte) (time >>> 24);
        result[2] = (byte) (time >>> 16);
        result[3] = (byte) (time >>> 8);
        result[4] = (byte) time;
        result[5] = internal[0];
        result[6] = internal[1];
        result[7] = internal[2];
        result[8] = internal[3];
        result[9] = internal[4];
        int tmp = count.incrementAndGet() & mask;
        result[10] = (byte) (tmp >>> 8);
        result[11] = (byte) (tmp);
        return result;
    }

    @Override
    public String generate() {
        return StringUtil.toHexString(generateBytes());
    }

    @Override
    public long generateLong() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String generateDigits() {
        byte[] result = generateBytes();
        long tmp = ((long) result[0] & 0xff) << 40;
        tmp |= ((long) result[1] & 0xff) << 32;
        tmp |= ((long) result[2] & 0xff) << 24;
        tmp |= ((long) result[3] & 0xff) << 16;
        tmp |= ((long) result[4] & 0xff) << 8;
        tmp |= ((long) result[5] & 0xff);
        char[] digs = new char[16];
        digs[0] = ByteTool.toDigit((int) ((tmp >>> 42) & short_mask));
        digs[1] = ByteTool.toDigit((int) ((tmp >>> 36) & short_mask));
        digs[2] = ByteTool.toDigit((int) ((tmp >>> 30) & short_mask));
        digs[3] = ByteTool.toDigit((int) ((tmp >>> 24) & short_mask));
        digs[4] = ByteTool.toDigit((int) ((tmp >>> 18) & short_mask));
        digs[5] = ByteTool.toDigit((int) ((tmp >>> 12) & short_mask));
        digs[6] = ByteTool.toDigit((int) ((tmp >>> 6) & short_mask));
        digs[7] = ByteTool.toDigit((int) ((tmp) & short_mask));
        tmp = ((long) result[6] & 0xff) << 40;
        tmp |= ((long) result[7] & 0xff) << 32;
        tmp |= ((long) result[8] & 0xff) << 24;
        tmp |= ((long) result[9] & 0xff) << 16;
        tmp |= ((long) result[10] & 0xff) << 8;
        tmp |= ((long) result[11] & 0xff);
        digs[8] = ByteTool.toDigit((int) ((tmp >>> 42) & short_mask));
        digs[9] = ByteTool.toDigit((int) ((tmp >>> 36) & short_mask));
        digs[10] = ByteTool.toDigit((int) ((tmp >>> 30) & short_mask));
        digs[11] = ByteTool.toDigit((int) ((tmp >>> 24) & short_mask));
        digs[12] = ByteTool.toDigit((int) ((tmp >>> 18) & short_mask));
        digs[13] = ByteTool.toDigit((int) ((tmp >>> 12) & short_mask));
        digs[14] = ByteTool.toDigit((int) ((tmp >>> 6) & short_mask));
        digs[15] = ByteTool.toDigit((int) ((tmp) & short_mask));
        return new String(digs);
    }
}
