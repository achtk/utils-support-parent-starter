package com.chua.oshi.support;

import lombok.Data;
import oshi.hardware.CentralProcessor;

import java.io.Serializable;
import java.text.DecimalFormat;

/**
 * Cpu
 * @author CH
 */
@Data
public class Cpu implements Serializable {
    private static final DecimalFormat LOAD_FORMAT = new DecimalFormat("#.00");

    private final CpuTicks ticks;
    /**
     * CPU核心数
     */
    private Integer cpuNum;

    /**
     * CPU总的使用率
     */
    private double toTal;

    /**
     * CPU系统使用率
     */
    private double sys;

    /**
     * CPU用户使用率
     */
    private double user;

    /**
     * CPU当前等待率
     */
    private double wait;

    /**
     * CPU当前空闲率
     */
    private double free;

    /**
     * CPU型号信息
     */
    private String cpuModel;


    public Cpu(CentralProcessor processor, int sleepTime) {
        final CpuTicks ticks = new CpuTicks(processor, sleepTime);
        this.ticks = ticks;

        this.cpuNum = processor.getLogicalProcessorCount();
        this.cpuModel = processor.toString();

        final long totalCpu = ticks.totalCpu();
        this.toTal = totalCpu;
        this.sys = formatDouble(ticks.cSys, totalCpu);
        this.user = formatDouble(ticks.user, totalCpu);
        this.wait = formatDouble(ticks.ioWait, totalCpu);
        this.free = formatDouble(ticks.idle, totalCpu);
    }

    private static double formatDouble(long tick, long totalCpu) {
        if (0 == totalCpu) {
            return 0D;
        }
        return Double.parseDouble(LOAD_FORMAT.format(tick <= 0 ? 0 : (100d * tick / totalCpu)));
    }
}
