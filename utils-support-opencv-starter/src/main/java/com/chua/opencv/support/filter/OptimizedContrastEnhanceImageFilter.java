package com.chua.opencv.support.filter;

import com.chua.opencv.support.models.OptimizedContrastEnhance;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.awt.image.BufferedImage;

import static com.chua.opencv.support.utils.OpencvUtils.toBufferedImage;
import static com.chua.opencv.support.utils.OpencvUtils.toMat;

/**
 * 大气光散射
 * @author CH
 */
@NoArgsConstructor
@AllArgsConstructor
public class OptimizedContrastEnhanceImageFilter extends AbstractOpencvImageFilter {

    private int blkSize = 100;
    private int patchSize = 4;
    private double lambda = 5.0;
    private double eps = 1e-8;
    private int kernelSize = 10;

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        Mat image = toMat(src);
        Mat result = OptimizedContrastEnhance.enhance(image, blkSize, patchSize, lambda, eps, kernelSize);
        result.convertTo(result, CvType.CV_8UC1);
        try {
            return toBufferedImage(result);
        } finally {
            result.release();
            image.release();
        }
    }
}
