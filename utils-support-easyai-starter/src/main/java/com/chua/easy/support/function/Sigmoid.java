package com.chua.easy.support.function;


import com.chua.easy.support.i.ActiveFunction;

/**
 * @param
 * @DATA
 * @Author LiDaPeng
 * @Description
 */
public class Sigmoid implements ActiveFunction {
    @Override
    public double function(double x) {
        return 1 / (1 + Math.exp(-x));
    }

    @Override
    public double functionG(double out) {
        return out * (1 - out);
    }
}
