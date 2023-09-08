package com.chua.oshi.support;

import com.chua.common.support.lang.date.DateUtils;
import com.chua.common.support.lang.date.unit.DateUnit;
import com.chua.common.support.utils.NumberUtils;
import lombok.Data;

import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.util.Date;

/**
 * jvm
 *
 * @author CH
 */
@Data
public class Jvm implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前JVM占用的内存总数(M)
     */
    private double total;

    /**
     * JVM最大可用内存总数(M)
     */
    private double max;

    /**
     * JVM空闲内存(M)
     */
    private double free;

    /**
     * JDK版本
     */
    private String version;

    /**
     * JDK路径
     */
    private String home;

    public double getTotal() {
        return NumberUtils.div(total, (1024 * 1024), 2);
    }

    public double getMax() {
        return NumberUtils.div(max, (1024 * 1024), 2);
    }

    public double getFree() {
        return NumberUtils.div(free, (1024 * 1024), 2);
    }

    public double getUsed() {
        return NumberUtils.div(total - free, (1024 * 1024), 2);
    }

    public String getVersion() {
        return version;
    }

    public String getHome() {
        return home;
    }

    public double getUsage() {
        return NumberUtils.mul(NumberUtils.div(total - free, total, 4), 100);
    }
    /**
     * 获取JDK名称
     */
    public String getName() {
        return ManagementFactory.getRuntimeMXBean().getVmName();
    }

    /**
     * JDK启动时间
     */
    public String getStartTime() {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        Date date = new Date(time);
        return DateUtils.format(date);
    }

    /**
     * JDK运行时间
     */
    public String getRunTime() {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        Date date = new Date(time);

        //运行多少分钟
        long runMS = DateUtils.between(date, new Date(), DateUnit.MS);

        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;

        long day = runMS / nd;
        long hour = runMS % nd / nh;
        long min = runMS % nd % nh / nm;
        return day + "天" + hour + "小时" + min + "分钟";
    }
}