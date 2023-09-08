package com.chua.oshi.support;

import com.chua.common.support.utils.NumberUtils;
import lombok.Data;

import java.io.Serializable;

/**
 * mem
 *
 * @author CH
 */
@Data
public class Mem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 内存总量
     */
    private double total;

    /**
     * 已用内存
     */
    private double used;

    /**
     * 剩余内存
     */
    private double free;

    public double getTotal() {
        return NumberUtils.div(total, (1024 * 1024 * 1024), 2);
    }

    public double getUsed() {
        return NumberUtils.div(used, (1024 * 1024 * 1024), 2);
    }


    public double getFree() {
        return NumberUtils.div(free, (1024 * 1024 * 1024), 2);
    }

    public double getUsage() {
        return NumberUtils.mul(NumberUtils.div(used, total, 4), 100);
    }
}
