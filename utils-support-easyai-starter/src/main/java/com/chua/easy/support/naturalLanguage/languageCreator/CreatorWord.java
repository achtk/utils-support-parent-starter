package com.chua.easy.support.naturalLanguage.languageCreator;

import com.chua.easy.support.MatrixTools.Matrix;
import com.chua.easy.support.i.OutBack;

public class CreatorWord implements OutBack {
    private int id;
    private double maxOut = -2;

    public int getId() {
        return id;
    }

    public double getMaxOut() {
        return maxOut;
    }

    public void clear() {
        maxOut = -2;
    }

    @Override
    public void getBack(double out, int id, long eventId) {
        if (out > maxOut) {
            maxOut = out;
            this.id = id;
        }
    }

    @Override
    public void getBackMatrix(Matrix matrix, long eventId) {

    }

    @Override
    public void getWordVector(int id, double w) {

    }
}
