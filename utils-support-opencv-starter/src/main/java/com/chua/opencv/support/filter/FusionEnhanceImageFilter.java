package com.chua.opencv.support.filter;

import com.chua.opencv.support.models.FusionEnhance;
import com.chua.opencv.support.utils.OpencvUtils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.awt.image.BufferedImage;

/**
 * 增强水下图像
 * @author CH
 */
@NoArgsConstructor
@AllArgsConstructor
public class FusionEnhanceImageFilter extends AbstractOpencvImageFilter {

    private int level = 5;

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        Mat image = OpencvUtils.toMat(src);
        Mat enhance = FusionEnhance.enhance(image, level);
        try {
            enhance.convertTo(enhance, CvType.CV_8UC1);
            return OpencvUtils.toBufferedImage(enhance);
        } finally {
            image.release();
        }
    }
}
