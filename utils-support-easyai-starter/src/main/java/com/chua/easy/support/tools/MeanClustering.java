package com.chua.easy.support.tools;


import com.chua.easy.support.entity.RGBNorm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//K均值聚类
public class MeanClustering {
    protected List<double[]> matrixList = new ArrayList<>();//聚类集合
    private int length;//向量长度(模型需要返回)
    protected int speciesQuantity;//种类数量(模型需要返回)
    protected List<RGBNorm> matrices = new ArrayList<>();//均值K模型(模型需要返回)

    public List<RGBNorm> getMatrices() {
        return matrices;
    }

    public MeanClustering(int speciesQuantity) throws Exception {
        this.speciesQuantity = speciesQuantity;//聚类的数量
    }

    public void setColor(double[] color) throws Exception {
        if (matrixList.size() == 0) {
            matrixList.add(color);
            length = color.length;
        } else {
            if (length == color.length) {
                matrixList.add(color);
            } else {
                throw new Exception("vector length is different");
            }
        }
    }

    private void averageMatrix() {
        for (double[] rgb : matrixList) {//遍历当前集合
            double min = -1;
            int id = 0;
            for (int i = 0; i < speciesQuantity; i++) {
                RGBNorm rgbNorm = matrices.get(i);
                double dist = rgbNorm.getEDist(rgb);
                if (min == -1 || dist < min) {
                    min = dist;
                    id = i;
                }
            }
            //进簇
            RGBNorm rgbNorm = matrices.get(id);
            rgbNorm.setColor(rgb);
        }
        //重新计算均值
        for (RGBNorm rgbNorm : matrices) {
            rgbNorm.norm();
        }
    }

    private boolean isNext() {
        boolean isNext = false;
        for (RGBNorm rgbNorm : matrices) {
            isNext = rgbNorm.compare();
            if (isNext) {
                break;
            }
        }
        return isNext;
    }

    private void clear() {
        for (RGBNorm rgbNorm : matrices) {
            rgbNorm.clear();
        }
    }

    public void start() throws Exception {//开始聚类
        if (matrixList.size() > 1) {
            Random random = new Random();
            for (int i = 0; i < speciesQuantity; i++) {//初始化均值向量
                int index = random.nextInt(matrixList.size());
                double[] rgb = matrixList.get(index);
                RGBNorm rgbNorm = new RGBNorm(rgb, length);
                //要进行深度克隆
                matrices.add(rgbNorm);
            }
            //进行两者的比较
            boolean isNext;
            for (int i = 0; i < 60; i++) {
                //System.out.println("聚类：" + i);
                averageMatrix();
                isNext = isNext();
                if (isNext && i < 59) {
                    clear();
                } else {
                    break;
                }
            }
        } else {
            throw new Exception("matrixList number less than 2");
        }
    }
}
