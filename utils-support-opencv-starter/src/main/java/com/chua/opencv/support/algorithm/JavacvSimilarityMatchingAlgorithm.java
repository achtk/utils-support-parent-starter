package com.chua.opencv.support.algorithm;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.constant.Projects;
import com.chua.common.support.lang.algorithm.ImageMatchingAlgorithm;
import com.chua.common.support.lang.profile.DelegateProfile;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.opencv.imgproc.Imgproc.*;


/**
 * javacv
 *
 * @author CH
 * @since 2021-12-02
 */
@Slf4j
@Spi("javacv-similarity")
public class JavacvSimilarityMatchingAlgorithm extends DelegateProfile implements ImageMatchingAlgorithm {
    static {
        Projects.installDependency("**/*/", "opencv_java", Projects.Dependency.builder().build());
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    //相关性阈值，应大于多少，越接近1表示越像，最大为1
    final static double HISTCMP_CORREL_THRESHOLD = 0.90;
    //卡方阈值，应小于多少，越接近0表示越像
    final static double HISTCMP_CHISQR_THRESHOLD = 0.5;
    //交叉阈值，应大于多少，数值越大表示越像
    final static double HISTCMP_INTERSECT_THRESHOLD = 2.5;
    //巴氏距离阈值，应小于多少，越接近0表示越像
    final static double HISTCMP_BHATTACHARYYA_THRESHOLD = 0.2;
    // HSV
    final static int[] CHANNELS = {0, 1};
    final static int H_BINS = 50;
    final static int S_BINS = 60;
    final static int V_BINS = 60;
    final static int[] HIST_SIZE = {H_BINS, S_BINS, V_BINS};
    final static float[] RANGE = {0, 255, 0, 180};

    @Override
    public double match(File source, File target) {
        Mat source1 = Imgcodecs.imread(source.getAbsolutePath());
        Mat target1 = Imgcodecs.imread(target.getAbsolutePath());

        //灰度图
        Imgproc.cvtColor(source1, source1, COLOR_BGR2HSV);
        Imgproc.cvtColor(target1, target1, COLOR_BGR2HSV);
        //直方图
        Mat hist1 = new Mat();
        Mat hist2 = new Mat();

        Imgproc.calcHist(Stream.of(source1).collect(Collectors.toList()), new MatOfInt(0), new Mat(), hist1, new MatOfInt(255), new MatOfFloat(0, 256));
        Imgproc.calcHist(Stream.of(target1).collect(Collectors.toList()), new MatOfInt(0), new Mat(), hist2, new MatOfInt(255), new MatOfFloat(0, 256));
        //标准化
        Core.normalize(hist1, hist1, 0, 1, Core.NORM_MINMAX, -1, new Mat());
        Core.normalize(hist2, hist2, 0, 1, Core.NORM_MINMAX, -1, new Mat());
        double result0, result1, result2, result3;
        result0 = Imgproc.compareHist(hist1, hist2, HISTCMP_CORREL);
        result1 = Imgproc.compareHist(hist1, hist2, HISTCMP_CHISQR);
        result2 = Imgproc.compareHist(hist1, hist2, HISTCMP_INTERSECT);
        result3 = Imgproc.compareHist(hist1, hist2, HISTCMP_BHATTACHARYYA);
        log.info("相关性（度量越高，匹配越准确 [基准：" + HISTCMP_CORREL_THRESHOLD + "]）,当前值:" + result0);
        log.info("卡方（度量越低，匹配越准确 [基准：" + HISTCMP_CHISQR_THRESHOLD + "]）,当前值:" + result1);
        log.info("交叉核（度量越高，匹配越准确 [基准：" + HISTCMP_INTERSECT_THRESHOLD + "]）,当前值:" + result2);
        log.info("巴氏距离（度量越低，匹配越准确 [基准：" + HISTCMP_BHATTACHARYYA_THRESHOLD + "]）,当前值:" + result3);
        //一共四种方式，有三个满足阈值就算匹配成功
        return result0;
    }
}
