package com.chua.common.support.lang.process;

import com.chua.common.support.unit.size.CapacitySize;
import com.chua.common.support.utils.NumberUtils;

/**
 * 进度分格
 *
 * @author CH
 */
public enum ProgressStyle implements ProgressStyleAdaptor {
    /**
     * 原始
     */
    ORGION() {
        @Override
        public String format(String num) {
            return num;
        }
    },
    /**
     * 原始
     */
    LOADING() {
        @Override
        public String format(String num) {
            if (!NumberUtils.isDecimals(num)) {
                return num;
            }
            return CapacitySize.format(NumberUtils.toBigDecimal(num).longValue());
        }
    },
    /**
     * 文件大小
     */
    SIZE() {
        @Override
        public String format(String num) {
            if (!NumberUtils.isDecimals(num)) {
                return num;
            }
            return CapacitySize.format(NumberUtils.toBigDecimal(num).longValue());
        }
    }
}
