package com.chua.opencv.support.filter;

import com.chua.opencv.support.utils.Filters;
import com.chua.opencv.support.utils.OpencvUtils;
import lombok.AllArgsConstructor;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.awt.image.BufferedImage;

/**
 * 增强水下图像
 * @author CH
 */
@AllArgsConstructor
public class GuidedFilterFeatheringImageFilter extends AbstractOpencvImageFilter {

    private String guidedImgPath;


    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        Mat image = OpencvUtils.toMat(src);
        image.convertTo(image, CvType.CV_32F);
        Mat guide = Imgcodecs.imread(guidedImgPath, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
        guide.convertTo(guide, CvType.CV_32F);
        int r = 60;
        double eps = 0.000001;
        Mat q = Filters.GuidedImageFilterColor(image, guide, r, eps, 1, -1);
        q.convertTo(q, CvType.CV_8UC1);
        try {
            return OpencvUtils.toBufferedImage(q);
        } finally {
            guide.release();
            image.release();
        }
    }
}
