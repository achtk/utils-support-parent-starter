package com.chua.common.support.utils;


import com.chua.common.support.constant.Projects;
import com.chua.common.support.date.DateTime;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

import static com.chua.common.support.date.constant.DateFormatConstant.YYYYMMDDHHMMSS;


/**
 * id工具类
 *
 * @author CH
 * @version 1.0.0
 */
public class IdUtils {
    private static final Object LOCK = new Object();
    private static final String TIME_FORMAT = "yyyyMMddHHmmss";
    private static final String SYMBOL_MINUS = "-";

    static final String ADDRESS;

    static {
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException ignored) {
        }

        if (null == addr) {
            ADDRESS = "00000000";
        } else {
            String addrStr = addr.getHostAddress();
            if (null == addrStr) {
                ADDRESS = "00000000";
            } else {
                String[] addrArr = addrStr.split("\\.");
                StringBuilder sb = new StringBuilder();
                sb.append(StringUtils.leftPad(Integer.toHexString(Integer.valueOf(addrArr[0])), 2, "0"))
                        .append(StringUtils.leftPad(Integer.toHexString(Integer.valueOf(addrArr[1])), 2, "0"))
                        .append(StringUtils.leftPad(Integer.toHexString(Integer.valueOf(addrArr[2])), 2, "0"))
                        .append(StringUtils.leftPad(Integer.toHexString(Integer.valueOf(addrArr[3])), 2, "0"));

                ADDRESS = sb.toString();
            }
        }

    }

    static final String PID = Projects.getPid();
    static final String COMPUTER_UNIQUE_IDENTIFICATION = Projects.getComputerUniqueIdentificationString();

    /**
     * traceId
     *
     * @return traceId
     */
    public static String createTraceId() {
        return StringUtils.limit(PID, "0", 4)
                + "a"
                + ADDRESS
                + "i"
                + COMPUTER_UNIQUE_IDENTIFICATION
                + "t"
                + System.currentTimeMillis()
                + String.format("%04d", RandomUtils.randomInt());
    }

    /**
     * 生成的数据指纹字符串
     *
     * @return md5 encrypt
     */
    public static String createDataFinger() {
        String md5 = createMd5();
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(md5.getBytes());
    }

    /**
     * 生成的md5算法字符串
     *
     * @return md5 encrypt
     */
    public static String createMd5() {
        return createMd5(createUuid() + System.nanoTime());
    }

    /**
     * 生成的md5算法字符串
     *
     * @param value 数据
     * @return md5 encrypt
     */
    public static String createMd5(final String value) {
        try {
            return Md5Utils.getInstance().getMd5String(value);
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * 生成的是不带-的字符串，类似于：b17f24ff026d40949c85a24f4f375d42
     *
     * @return UUID
     */
    public static String createSimpleUuid() {
        return createUuid().replace("-", "");
    }

    /**
     * 生成时间标识
     *
     * @return 时间
     */
    public static String createTimeId() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        LocalDateTime localDateTime = LocalDateTime.now();
        StringBuilder sb = new StringBuilder();
        sb.append(localDateTime.format(DateTimeFormatter.ofPattern(TIME_FORMAT)));
        synchronized (LOCK) {
            sb.append(System.nanoTime());
            sb.append(Strings.padEnd(random.nextInt() + "", 8, '0'));
        }
        return sb.toString();
    }

    /**
     * UUID + 时间
     *
     * @return UUID + 时间
     */
    public static String createTid() {
        return createUuid().concat(SYMBOL_MINUS).concat(DateTime.now().toString(YYYYMMDDHHMMSS)).concat(SYMBOL_MINUS).concat(System.nanoTime() + "");
    }

    /**
     * 生成的UUID是带-的字符串，类似于：a5c8a5e8-df2b-4706-bea4-08d0939410e3
     *
     * @return UUID
     */
    public static String createUuid() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        UUID uuid = new UUID(random.nextInt(), random.nextInt());
        return uuid.toString();
    }

    /**
     * 创建自增版本
     * <p>e.g.createVersion(3, 10, 11) = 0.1.1</p>
     * <p>e.g.createVersion(3, 11, 11) = 0.0.11</p>
     * <p>e.g.createVersion(3, 2, 11) = 1.2.1</p>
     *
     * @param versionNumber 版本序号
     * @param maxNumber     最大序号
     * @param currentNumber 当前序号
     * @return 版本
     */
    public static String createVersion(int versionNumber, long maxNumber, long currentNumber) {
        List<Long> temp = new ArrayList<>();
        for (int i = 1; i < versionNumber; i++) {
            temp.add(currentNumber % maxNumber);
            currentNumber = currentNumber / maxNumber;
        }
        temp.add(currentNumber);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = versionNumber - 1; i > -1; i--) {
            stringBuilder.append(".").append(temp.get(i));

        }
        return stringBuilder.substring(1);
    }

    /**
     * 生成的是不带-的字符串，类似于：b17f24ff026d40949c85a24f4f375d42
     *
     * @return UUID
     */
    public static String simpleUuid() {
        return createSimpleUuid();
    }


    /**
     * 生成时间标识
     *
     * @return 时间
     */
    public static String timeId() {
        return createTimeId();
    }

    /**
     * 生成的UUID是带-的字符串，类似于：a5c8a5e8-df2b-4706-bea4-08d0939410e3
     *
     * @return UUID
     */
    public static String uuid() {
        return createUuid();
    }
}
