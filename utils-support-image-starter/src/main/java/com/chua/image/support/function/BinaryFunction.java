package com.chua.image.support.function;

/**
 * 二值化
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/6/11
 */
public interface BinaryFunction {
    /**
     * 二值化
     *
     * @param rgb 颜色
     * @return 是否二值化
     */
    boolean isBlack(int rgb);
}
