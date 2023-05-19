package com.chua.pytorch.support.face.filter;

import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.image.filter.AbstractImageFilter;
import com.chua.pytorch.support.face.detector.RetinaFaceDetector;
import com.chua.pytorch.support.utils.LocationUtils;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * face标注
 * @author CH
 */
public class FaceImageFilter extends AbstractImageFilter {

    static   RetinaFaceDetector lightFaceDetector = null;
    static boolean isLoaded = false;

    void refresh() {

        if(!isLoaded) {
            isLoaded = true;
            try {
                lightFaceDetector = new RetinaFaceDetector(DetectionConfiguration.builder().build());
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        refresh();
        if(null == lightFaceDetector) {
            return src;
        }
        List<PredictResult> detect = lightFaceDetector.detect(src);
        return LocationUtils.saveBoundingBoxImage(detect, src);
    }
}
