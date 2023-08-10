package com.chua.pytorch.support.fire;

import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.output.DetectedObjects;
import com.chua.common.support.annotations.Spi;
import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.utils.MapUtils;
import com.chua.common.support.utils.StringUtils;
import com.chua.pytorch.support.AbstractPytorchDetector;
import com.chua.pytorch.support.utils.LocationUtils;
import com.chua.pytorch.support.yolo.YoloV5Translator;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * FireSmoke
 *
 * @author CH
 */
@Spi("FireSmokeDetector")
public class FireSmokeDetector extends AbstractPytorchDetector<DetectedObjects> {

    static final Map<String, Object> DEFAULT_ARGUMENTS = new ConcurrentHashMap<>();

    static {
        DEFAULT_ARGUMENTS.put("width", 640);
        DEFAULT_ARGUMENTS.put("height", 640);
        DEFAULT_ARGUMENTS.put("resize", true);
        DEFAULT_ARGUMENTS.put("rescale", true);
        DEFAULT_ARGUMENTS.put("threshold", 0.2);
        DEFAULT_ARGUMENTS.put("nmsThreshold", 0.5);
        DEFAULT_ARGUMENTS.put("synset", "fire,smoke");
    }


    public FireSmokeDetector(DetectionConfiguration configuration) {
        super(configuration,
                YoloV5Translator.builder(MapUtils.compute(configuration.ext(), DEFAULT_ARGUMENTS))
                        .build(),
                "PyTorch",
                null,
                StringUtils.defaultString(configuration.modelPath(), "fire_smoke"),
                "https://aias-home.oss-cn-beijing.aliyuncs.com/models/fire_smoke.zip",
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
            if (item.getProbability() < 0.15) {
                continue;
            }

            PredictResult predictResult = LocationUtils.convertPredictResult(item, img);
            results.add(predictResult);
        }
        return results;
    }
}
