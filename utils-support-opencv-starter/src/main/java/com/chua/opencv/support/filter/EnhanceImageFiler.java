package com.chua.opencv.support.filter;

import com.chua.opencv.support.utils.OpencvUtils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;

/**
 * enhance
 *
 * @author CH
 */
public class EnhanceImageFiler extends AbstractOpencvImageFilter {
    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        try {
            Mat source = OpencvUtils.toMat(src);
            Imgproc.GaussianBlur(source, source, new Size(0, 0), 10);
            Core.addWeighted(source, 1.5, source, -0.5, 0, source);
            try {
                return OpencvUtils.toBufferedImage(source);
            } finally {
                source.release();
            }
        } catch (Exception e) {
        }
        return null;
    }
}
