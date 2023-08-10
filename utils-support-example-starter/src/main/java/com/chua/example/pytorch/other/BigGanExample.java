package com.chua.example.pytorch.other;

import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.utils.BufferedImageUtils;
import com.chua.pytorch.support.biggan.BigGAN512;

import java.awt.image.BufferedImage;
import java.util.List;

public class BigGanExample {


    public static void main(String[] args) {
        BigGAN512 gan = new BigGAN512(DetectionConfiguration.builder().cachePath("E:\\workspace\\environment").build());
        List<PredictResult> detect = gan.predict(11);
        System.out.println(BufferedImageUtils.writeToFile((BufferedImage) detect.get(0).getNdArray(), "Z://1.jpg"));
    }
}
