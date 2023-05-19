package com.chua.opencv.support.filter;

import com.chua.opencv.support.models.RemoveBackScatter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.awt.image.BufferedImage;

import static com.chua.opencv.support.utils.OpencvUtils.toBufferedImage;
import static com.chua.opencv.support.utils.OpencvUtils.toMat;

/**
 * 移除黑色杂边
 * @author CH
 */
@NoArgsConstructor
@AllArgsConstructor
public class RemoveBackScatterImageFilter extends AbstractOpencvImageFilter {

    private int blkSize = 10 * 10;
    private int patchSize = 8;
    private double lambda = 10;
    private double gamma = 1.7;
    private int r = 10;
    private final double eps = 1e-6;
    private final int level = 5;


    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        Mat image = toMat(src);
        Mat fusion = RemoveBackScatter.enhance(image, blkSize, patchSize, lambda, gamma, r, eps, level);
        fusion.convertTo(fusion, CvType.CV_8UC1);
        try {
            return toBufferedImage(fusion);
        } finally {
            fusion.release();
            image.release();
        }
    }
}
