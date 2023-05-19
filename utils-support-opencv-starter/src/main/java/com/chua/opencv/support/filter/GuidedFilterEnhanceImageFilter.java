package com.chua.opencv.support.filter;

import com.chua.opencv.support.utils.Filters;
import lombok.NoArgsConstructor;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.chua.opencv.support.utils.OpencvUtils.toBufferedImage;
import static com.chua.opencv.support.utils.OpencvUtils.toMat;

/**
 * 增强水下图像
 * @author CH
 */
@NoArgsConstructor
public class GuidedFilterEnhanceImageFilter extends AbstractOpencvImageFilter {

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        Mat image = toMat(src);
        image.convertTo(image, CvType.CV_32F);
        List<Mat> img = new ArrayList<>();
        Core.split(image, img);
        int r = 16;
        double eps = 0.01;

        Mat qR = Filters.GuidedImageFilter(img.get(0), img.get(0), r, eps);
        Mat qG = Filters.GuidedImageFilter(img.get(1), img.get(1), r, eps);
        Mat qB = Filters.GuidedImageFilter(img.get(2), img.get(2), r, eps);

        Mat q = new Mat();
        Core.merge(new ArrayList<>(Arrays.asList(qR, qG, qB)), q);
        q.convertTo(q, CvType.CV_8UC1);
        try {
            return toBufferedImage(q);
        } finally {
            qG.release();
            qG.release();
            qR.release();
            image.release();
        }
    }
}
