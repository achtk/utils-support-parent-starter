package com.chua.opencv.support.filter;

import com.chua.opencv.support.models.DarkChannelPriorDehaze;
import com.chua.opencv.support.utils.OpencvUtils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.opencv.core.Mat;

import java.awt.image.BufferedImage;

/**
 * 黑暗管道
 *
 * @author CH
 */
@NoArgsConstructor
@AllArgsConstructor
public class DarkChannelImageFilter extends AbstractOpencvImageFilter {

    private double kernelRatio = 0.01;
    private double minAtmosphericLight = 240.0;
    private double eps = 0.000001;


    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        Mat image = OpencvUtils.toMat(src);
        Mat enhance = DarkChannelPriorDehaze.enhance(image, kernelRatio, minAtmosphericLight, eps);
        try {
            return OpencvUtils.toBufferedImage(enhance);
        } finally {
            image.release();
        }
    }
}
