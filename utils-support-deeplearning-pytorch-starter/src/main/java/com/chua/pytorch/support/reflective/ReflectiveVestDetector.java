package com.chua.pytorch.support.reflective;

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
 * 反光衣检测
 *
 * @author CH
 */
@Spi("ReflectiveVestDetector")
public class ReflectiveVestDetector extends AbstractPytorchDetector<DetectedObjects> {

    public ReflectiveVestDetector(DetectionConfiguration configuration) {
        super(configuration,
                new LargeHelmetHeadPersonTranslator(configuration),
                "PyTorch",
                null,
                StringUtils.defaultString(configuration.modelPath(), "reflective_clothes"),
                "https://aias-home.oss-cn-beijing.aliyuncs.com/models/reflective_clothes.zip",
                true);
    }

    @Override
    protected Class<DetectedObjects> type() {
        return DetectedObjects.class;
    }

    @Override
    protected List<PredictResult> toDetect(DetectedObjects detections, Image img) {
        List<PredictResult> results = new LinkedList<>();

        List<DetectedObjects.DetectedObject> items = detections.items();
        for (DetectedObjects.DetectedObject item : items) {
            if (item.getProbability() < 0.55) {
                continue;
            }

            PredictResult predictResult = LocationUtils.convertPredictResult(item, img);
            results.add(predictResult);
        }
        return results;
    }
}
