package com.chua.common.support.lang.id;

/**
 * 雪花算法
 * <img src="SnowFlake.png"/>
 *
 * @author CH
 * @since 2022-05-11
 */
public class SnowFlake {
    /**
     * 起始时间戳
     */
    private final static long START_STAMP = 1480166465631L;

    /**
     * 每部分的位数
     * 序列号占用位数
     */
    private final static long MACHINE_BIT = 5;
    /**
     * 机器id占用位数
     */
    private final static long SEQUENCE_BIT = 12;
    /**
     * 机房id占用位数
     */
    private final static long DATACENTER_BIT = 5;

    /**
     * 每部分最大值
     */
    private final static long MAX_DATACENTER_NUM = ~(-1L << DATACENTER_BIT);
    private final static long MAX_MACHINE_NUM = ~(-1L << MACHINE_BIT);
    private final static long MAX_SEQUENCE = ~(-1L << SEQUENCE_BIT);

    /**
     * 每部分向左的位移
     */
    private final static long MACHINE_LEFT = SEQUENCE_BIT;
    private final static long DATACENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    private final static long TIMESTAMP_LEFT = DATACENTER_LEFT + DATACENTER_BIT;

    /**
     * 机房id
     */
    private final long machineId;
    /**
     * 机器id
     */
    private final long datacenterId;
    /**
     * 序列号
     */
    private long lastTimestamp = -1L;
    /**
     * 上次的时间戳
     */
    private long sequence = 0L;

    public SnowFlake(long datacenterId, long machineId) {
        if (datacenterId > MAX_DATACENTER_NUM || datacenterId < 0) {
            throw new IllegalArgumentException("datacenterId can't be greater than MAX_DATACENTER_NUM or less than 0");
        }
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException("machineId can't be greater than MAX_MACHINE_NUM or less than 0");
        }
        this.datacenterId = datacenterId;
        this.machineId = machineId;
    }

    /**
     * 产生下一个ID
     *
     * @return 产生下一个ID
     */
    public synchronized long getNextId() {
        long currStmp = getNewTimeStamp();
        if (currStmp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards.Refusing to generate id");
        }
        if (currStmp == lastTimestamp) {
            // 若在相同毫秒内 序列号自增
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // 同一毫秒的序列数已达到最大
            if (sequence == 0L) {
                currStmp = getNextMill();
            }
        } else {
            // 若在不同毫秒内 则序列号置为0
            sequence = 0L;
        }
        lastTimestamp = currStmp;

        // 时间戳部分
        // 序列号部分
        return (currStmp - START_STAMP) << TIMESTAMP_LEFT
                // 机房id部分
                | datacenterId << DATACENTER_LEFT
                // 机器id部分
                | machineId << MACHINE_LEFT
                | sequence;
    }

    /**
     * 获取新的毫秒数
     *
     * @return 获取新的毫秒数
     */
    private long getNextMill() {
        long mill = getNewTimeStamp();
        while (mill <= lastTimestamp) {
            mill = getNewTimeStamp();
        }
        return mill;
    }

    /**
     * 获取当前的毫秒数
     *
     * @return 获取当前的毫秒数
     */
    private long getNewTimeStamp() {
        return System.currentTimeMillis();
    }
}
