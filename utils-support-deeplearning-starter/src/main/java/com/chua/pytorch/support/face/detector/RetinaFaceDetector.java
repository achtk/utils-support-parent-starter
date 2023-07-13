package com.chua.pytorch.support.face.detector;

import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.output.BoundingBox;
import ai.djl.modality.cv.output.DetectedObjects;
import com.chua.common.support.annotations.Spi;
import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.pytorch.support.AbstractPytorchDetector;
import com.chua.pytorch.support.utils.LocationUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * RetinaFaceDetection
 *
 * @author CH
 */
@Spi("Retina")
public class RetinaFaceDetector extends AbstractPytorchDetector<DetectedObjects> {


    public RetinaFaceDetector(DetectionConfiguration configuration) {
        super(configuration,
                new FaceDetectionTranslator(),
                "PyTorch",
                "retinaface",
                 "retinaface.pt",
                "https://resources.djl.ai/test-models/pytorch/retinaface.zip",
                false);
    }

    @Override
    protected Class<DetectedObjects> type() {
        return DetectedObjects.class;
    }

    @Override
    protected List<PredictResult> toDetect(DetectedObjects detectedObjects, Image img) {
        List<PredictResult> results = new LinkedList<>();

        int numberOfObjects = detectedObjects.getNumberOfObjects();
        for (int i = 0; i < numberOfObjects; i++) {
            Classifications.Classification classification = detectedObjects.item(i);
            PredictResult result = LocationUtils.convertPredictResult(classification, img);
            BoundingBox boundingBox = ((DetectedObjects.DetectedObject) (classification)).getBoundingBox();
            result.setBoundingBox(boundingBox);

            results.add(result);
        }
        return results;
    }


}
