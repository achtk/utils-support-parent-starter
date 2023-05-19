package com.chua.opencv.support.filter;

import com.chua.opencv.support.utils.Filters;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.awt.image.BufferedImage;

import static com.chua.opencv.support.utils.OpencvUtils.toBufferedImage;
import static com.chua.opencv.support.utils.OpencvUtils.toMat;

/**
 * 增强水下图像
 * @author CH
 */
@NoArgsConstructor
@AllArgsConstructor
public class GuidedFilterSmoothingImageFilter extends AbstractOpencvImageFilter {

    /**
     *  try r=2, 4, or 8
     */
    int r = 4;
    /**
     * try eps=0.01, 0.04, 0.16
     */
    double eps = 0.16;

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        Mat image = toMat(src);
        image.convertTo(image, CvType.CV_32F);
        Mat guide = image.clone();

        Mat q = Filters.GuidedImageFilter(image, guide, r, eps);
        q.convertTo(q, CvType.CV_8UC1);
        try {
            return toBufferedImage(q);
        } finally {
            q.release();
            guide.release();
            image.release();
        }
    }
}
