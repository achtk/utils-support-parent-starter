package com.chua.opencv.support.filter;

import com.chua.opencv.support.utils.Filters;
import lombok.AllArgsConstructor;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

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
@AllArgsConstructor
public class GuidedFilterFlashImageFilter extends AbstractOpencvImageFilter {

    private String guidedImgPath;


    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        Mat image = toMat(src);
        image.convertTo(image, CvType.CV_32F);
        Mat guide = Imgcodecs.imread(guidedImgPath, Imgcodecs.CV_LOAD_IMAGE_COLOR);
        guide.convertTo(guide, CvType.CV_32F);
        List<Mat> img = new ArrayList<>();
        List<Mat> gid = new ArrayList<>();
        Core.split(image, img);
        Core.split(guide, gid);

        int r = 8;
        double eps = 0.02 * 0.02;
        Mat qR = Filters.GuidedImageFilter(img.get(0), gid.get(0), r, eps);
        Mat qG = Filters.GuidedImageFilter(img.get(1), gid.get(1), r, eps);
        Mat qB = Filters.GuidedImageFilter(img.get(2), gid.get(2), r, eps);
        Mat q = new Mat();
        Core.merge(new ArrayList<>(Arrays.asList(qR, qG, qB)), q);
        q.convertTo(q, CvType.CV_8UC1);
        try {
            return toBufferedImage(q);
        } finally {
            for (Mat mat : gid) {
                mat.release();
            }

            for (Mat mat : img) {
                mat.release();
            }
            qR.release();
            qB.release();
            qR.release();
            guide.release();
            image.release();
        }
    }
}
