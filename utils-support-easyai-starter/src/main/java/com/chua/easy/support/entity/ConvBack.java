package com.chua.easy.support.entity;


import com.chua.easy.support.MatrixTools.Matrix;
import com.chua.easy.support.i.OutBack;

/**
 * @param
 * @DATA
 * @Author LiDaPeng
 * @Description
 */
public class ConvBack implements OutBack {
    private Matrix matrix;

    public Matrix getMatrix() {
        return matrix;
    }

    @Override
    public void getBack(double out, int id, long eventId) {
    }

    @Override
    public void getBackMatrix(Matrix matrix, long eventId) {
        this.matrix = matrix;
    }

    @Override
    public void getWordVector(int id, double w) {

    }
}
