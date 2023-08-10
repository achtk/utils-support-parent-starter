package com.chua.tensorflow.support.objects;

import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.output.DetectedObjects;
import com.chua.common.support.annotations.Spi;
import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.utils.StringUtils;
import com.chua.pytorch.support.AbstractPytorchDetector;
import com.chua.pytorch.support.utils.LocationUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * 图片检测
 * Darknet53Classification
 *
 * @author CH
 */
@Spi("ObjectsClassification")
public class ObjectsClassification extends AbstractPytorchDetector<DetectedObjects> {
    /**
     * width: 256
     *
     * @param configuration
     */
    public ObjectsClassification(DetectionConfiguration configuration) {
        super(configuration,
                new ObjectsTranslator(configuration),
                "TensorFlow",
                null,
                StringUtils.defaultString(configuration.modelPath(), "tf_mobilenetv2"),
                "https://aias-home.oss-cn-beijing.aliyuncs.com/models/tf_mobilenetv2.zip",
                true);
    }

    @Override
    protected Class<DetectedObjects> type() {
        return DetectedObjects.class;
    }

    @Override
    protected List<PredictResult> toDetect(DetectedObjects o, Image img) {
        List<PredictResult> rs = new LinkedList<>();
        for (DetectedObjects.Classification classification : o.topK(configuration.top())) {
            PredictResult predictResult = new PredictResult();
            predictResult.setText(classification.getClassName());
            predictResult.setClsLabel(classification.getClassName());
            if (classification instanceof DetectedObjects.DetectedObject) {
                predictResult.setBoundingBox(LocationUtils.toBoundingBox(((DetectedObjects.DetectedObject) classification).getBoundingBox()));
            }
            predictResult.setScore((float) classification.getProbability());
            rs.add(predictResult);
        }
        return rs;
    }
}
