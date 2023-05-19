package com.chua.opencv.support.filter;

import com.chua.opencv.support.models.ALTMRetinex;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.opencv.core.Mat;

import java.awt.image.BufferedImage;

import static com.chua.opencv.support.utils.OpencvUtils.toBufferedImage;
import static com.chua.opencv.support.utils.OpencvUtils.toMat;

/**
 * 暗部增强
 *
 * @author CH
 */
@NoArgsConstructor
@AllArgsConstructor
public class AltRetinueEnhanceImageFilter extends AbstractOpencvImageFilter {

    // Local Adaptation Parameters
    private int r = 10;
    private double eps = 0.01;
    private double eta = 36.0;
    private double lambda = 10.0;
    private double krnlRatio = 0.01;


    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        Mat mat = toMat(src);
        Mat enhance = ALTMRetinex.enhance(mat, r, eps, eta, lambda, krnlRatio);
        try {
            return toBufferedImage(enhance);
        } finally {
            mat.release();
        }
    }
}
