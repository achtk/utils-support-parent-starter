package com.chua.common.support.extra.el.baseutil.uniqueid;

import com.chua.common.support.extra.el.baseutil.StringUtil;
import com.chua.common.support.utils.ByteUtils;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * 基础类
 * @author CH
 */
public class AutumnId implements Uid {
    private static final int PID;
    private static final byte[] INTERNAL = new byte[5];
    private static final AtomicInteger COUNT = new AtomicInteger(0);
    private static final int MASK = 0x0000ffff;
    private static volatile AutumnId INSTANCE;

    static {
        String s = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
        PID = Integer.valueOf(s);
        // 本机mac地址的hash 32个bit
        int maxHash;
        try {
            maxHash = StringUtil.toHexString(NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress()).hashCode();
        } catch (Exception e) {
            maxHash = new Random().nextInt();
        }
        INTERNAL[0] = (byte) (PID >>> 8);
        INTERNAL[1] = (byte) PID;
        INTERNAL[2] = (byte) (maxHash >>> 16);
        INTERNAL[3] = (byte) (maxHash >>> 8);
        INTERNAL[4] = (byte) (maxHash);
    }

    private AutumnId() {
    }

    public static final AutumnId instance() {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        synchronized (COUNT) {
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
        long time = System.currentTimeMillis() - BASE;
        result[0] = (byte) (time >>> 32);
        result[1] = (byte) (time >>> 24);
        result[2] = (byte) (time >>> 16);
        result[3] = (byte) (time >>> 8);
        result[4] = (byte) time;
        result[5] = INTERNAL[0];
        result[6] = INTERNAL[1];
        result[7] = INTERNAL[2];
        result[8] = INTERNAL[3];
        result[9] = INTERNAL[4];
        int tmp = COUNT.incrementAndGet() & MASK;
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
        digs[0] = ByteUtils.toDigit((int) ((tmp >>> 42) & SHORT_MASK));
        digs[1] = ByteUtils.toDigit((int) ((tmp >>> 36) & SHORT_MASK));
        digs[2] = ByteUtils.toDigit((int) ((tmp >>> 30) & SHORT_MASK));
        digs[3] = ByteUtils.toDigit((int) ((tmp >>> 24) & SHORT_MASK));
        digs[4] = ByteUtils.toDigit((int) ((tmp >>> 18) & SHORT_MASK));
        digs[5] = ByteUtils.toDigit((int) ((tmp >>> 12) & SHORT_MASK));
        digs[6] = ByteUtils.toDigit((int) ((tmp >>> 6) & SHORT_MASK));
        digs[7] = ByteUtils.toDigit((int) ((tmp) & SHORT_MASK));
        tmp = ((long) result[6] & 0xff) << 40;
        tmp |= ((long) result[7] & 0xff) << 32;
        tmp |= ((long) result[8] & 0xff) << 24;
        tmp |= ((long) result[9] & 0xff) << 16;
        tmp |= ((long) result[10] & 0xff) << 8;
        tmp |= ((long) result[11] & 0xff);
        digs[8] = ByteUtils.toDigit((int) ((tmp >>> 42) & SHORT_MASK));
        digs[9] = ByteUtils.toDigit((int) ((tmp >>> 36) & SHORT_MASK));
        digs[10] = ByteUtils.toDigit((int) ((tmp >>> 30) & SHORT_MASK));
        digs[11] = ByteUtils.toDigit((int) ((tmp >>> 24) & SHORT_MASK));
        digs[12] = ByteUtils.toDigit((int) ((tmp >>> 18) & SHORT_MASK));
        digs[13] = ByteUtils.toDigit((int) ((tmp >>> 12) & SHORT_MASK));
        digs[14] = ByteUtils.toDigit((int) ((tmp >>> 6) & SHORT_MASK));
        digs[15] = ByteUtils.toDigit((int) ((tmp) & SHORT_MASK));
        return new String(digs);
    }
}
