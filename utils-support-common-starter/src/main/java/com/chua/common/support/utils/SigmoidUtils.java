package com.chua.common.support.utils;

/**
 * sigmoid
 * @author CH
 */
public class SigmoidUtils {

    /**
     * Sigmoid
     * @param value value
     * @return 结果
     */
    public static double sigmoid(double value) {
        double ey = Math.pow(Math.E, -value);
        return 1 / (1 + ey);
    }

    /**
     * Sigmoid 求导
     * @param value value
     * @return 导
     */
    public static double sigmoidDerivative(double value) {
        double a = sigmoid(value);
        double b = 1 - sigmoid(value);
        return a * b;
    }
}
