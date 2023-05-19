package com.chua.pytorch.support.face.detector;

import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.output.DetectedObjects;
import com.chua.common.support.annotations.Spi;
import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.pytorch.support.AbstractPytorchDetector;
import com.chua.pytorch.support.utils.LocationUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * paddlepaddle
 *
 * @author CH
 */
@Spi("paddlepaddle")
public class PaddlePaddleFaceDetector extends AbstractPytorchDetector<DetectedObjects> {

    public PaddlePaddleFaceDetector(DetectionConfiguration configuration) throws Exception {
        super(configuration,
                new PaddlePaddleFaceTranslator(0.5f, 0.7f),
                "PaddlePaddle",
                "inference",
                 "face_detection",
                "https://aias-home.oss-cn-beijing.aliyuncs.com/models/face_mask/face_detection.zip",
                true);
    }

    @Override
    protected Class<DetectedObjects> type() {
        return DetectedObjects.class;
    }

    @Override
    protected List<PredictResult> toDetect(DetectedObjects detectedObjects, Image img) {
        List<DetectedObjects.DetectedObject> faces = detectedObjects.items();

        List<PredictResult> results = new LinkedList<>();
        for (DetectedObjects.DetectedObject face : faces) {
            results.add(LocationUtils.convertPredictResult(face, img));
        }
        return results;
    }


}
