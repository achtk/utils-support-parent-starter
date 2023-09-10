package com.chua.oshi.support;

import com.chua.common.support.utils.NumberUtils;
import lombok.Data;

import java.io.Serializable;

/**
 * Cpu
 * @author CH
 */
@Data
public class Cpu implements Serializable {


    /**
     * 核心数
     */
    private int cpuNum = 0;

    /**
     * CPU总的使用率
     */
    private double total = 0;

    /**
     * CPU系统使用率
     */
    private double sys = 0;

    /**
     * CPU用户使用率
     */
    private double used = 0;

    /**
     * CPU当前等待率
     */
    private double wait = 0;

    /**
     * CPU当前空闲率
     */
    private double free = 0;


    public double getTotal() {
        return NumberUtils.round(NumberUtils.mul(total, 100), 2).doubleValue();
    }

    public double getSys() {
        return NumberUtils.round(NumberUtils.mul(sys / total, 100), 2).doubleValue();
    }

    public double getUsed() {
        return NumberUtils.round(NumberUtils.mul(used / total, 100), 2).doubleValue();
    }

    public double getWait() {
        return NumberUtils.round(NumberUtils.mul(wait / total, 100), 2).doubleValue();
    }

    public double getFree() {
        return NumberUtils.round(NumberUtils.mul(free / total, 100), 2).doubleValue();
    }
}
