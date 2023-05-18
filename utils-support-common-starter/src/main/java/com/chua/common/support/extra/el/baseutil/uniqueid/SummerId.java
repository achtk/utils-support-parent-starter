package com.chua.common.support.extra.el.baseutil.uniqueid;

import com.chua.common.support.extra.el.baseutil.StringUtil;
import com.chua.common.support.extra.el.baseutil.exception.UnSupportException;

import java.util.concurrent.atomic.AtomicInteger;

public class SummerId implements Uid
{

    private final static int           countMask = 0x0000ffff;
    private final        AtomicInteger count     = new AtomicInteger(0);
    private final        byte          workedId;

    public SummerId(int workerId)
    {
        if (workerId >= 0 && workerId <= 255)
        {
            this.workedId = (byte) (workerId & 0xff);
        }
        else
        {
            throw new UnSupportException("workerid的取值范围为0-255");
        }
    }

    @Override
    public String generate()
    {
        return StringUtil.toHexString(generateBytes());
    }

    @Override
    public long generateLong()
    {
        byte[] result = generateBytes();
        long   tmp    = ((long) result[0] & 0xff) << 56;
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
    public String generateDigits()
    {
        long   tmp   = generateLong();
        char[] value = new char[11];
        value[0] = ByteTool.toDigit((int) ((tmp >>> 58) & short_mask));
        value[1] = ByteTool.toDigit((int) ((tmp >>> 52) & short_mask));
        value[2] = ByteTool.toDigit((int) ((tmp >>> 46) & short_mask));
        value[3] = ByteTool.toDigit((int) ((tmp >>> 40) & short_mask));
        value[4] = ByteTool.toDigit((int) ((tmp >>> 34) & short_mask));
        value[5] = ByteTool.toDigit((int) ((tmp >>> 28) & short_mask));
        value[6] = ByteTool.toDigit((int) ((tmp >>> 22) & short_mask));
        value[7] = ByteTool.toDigit((int) ((tmp >>> 16) & short_mask));
        value[8] = ByteTool.toDigit((int) ((tmp >>> 10) & short_mask));
        value[9] = ByteTool.toDigit((int) ((tmp >>> 4) & short_mask));
        value[10] = ByteTool.toDigit((int) ((tmp) & 0x000000000000000f));
        return String.valueOf(value);
    }

    /**
     * 使用64个bit进行id生成 第一个bit不使用，默认为0 2-40bit是为毫秒是时间戳。足够使用17年 41-48bit是workerid的值
     * 49-64bit为序号，最大长度为0x0000ffff。 该算法可以在1毫秒内产生0x0000ffff个id。
     * 注意：该算法未进行超时保护。如果机器的能力超过了1毫秒0x0000ffff的id，则id会出现重复。但是这个数字已经十分大。基本不太可能。
     */
    @Override
    public byte[] generateBytes()
    {
        byte[] result = new byte[8];
        long   time   = System.currentTimeMillis() - base;
        result[0] = (byte) (time >>> 32);
        result[1] = (byte) (time >>> 24);
        result[2] = (byte) (time >>> 16);
        result[3] = (byte) (time >>> 8);
        result[4] = (byte) time;
        result[5] = workedId;
        int tmp = count.getAndIncrement() & countMask;
        result[6] = (byte) (tmp >>> 8);
        result[7] = (byte) (tmp);
        return result;
    }
}
