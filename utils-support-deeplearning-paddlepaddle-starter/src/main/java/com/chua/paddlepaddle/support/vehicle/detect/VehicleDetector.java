package com.chua.paddlepaddle.support.vehicle.detect;

import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.output.DetectedObjects;
import com.chua.common.support.annotations.Spi;
import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.utils.StringUtils;
import com.chua.pytorch.support.AbstractPytorchDetector;
import com.chua.pytorch.support.utils.LocationUtils;
import lombok.SneakyThrows;

import java.util.LinkedList;
import java.util.List;

/**
 * 车牌
 *
 * @author CH
 */
@Spi("VehicleDetector")
public class VehicleDetector extends AbstractPytorchDetector<DetectedObjects> {

    @SneakyThrows
    public VehicleDetector(DetectionConfiguration configuration) {
        super(configuration,
                new VehicleTranslator(),
                "PaddlePaddle",
                "inference",
                StringUtils.defaultString(configuration.modelPath(), "vehicle"),
                "https://aias-home.oss-cn-beijing.aliyuncs.com/models/vehicle.zip",
                true);
    }

    @Override
    public void close() throws Exception {
        model.close();
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
