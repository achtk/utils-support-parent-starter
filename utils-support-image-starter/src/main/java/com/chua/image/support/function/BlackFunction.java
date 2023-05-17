package com.chua.image.support.function;


/**
 * 黑色
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/6/11
 */
public class BlackFunction implements BinaryFunction {

    @Override
    public boolean isBlack(int rgb) {
        return rgb == 0xff000000;
    }
}
