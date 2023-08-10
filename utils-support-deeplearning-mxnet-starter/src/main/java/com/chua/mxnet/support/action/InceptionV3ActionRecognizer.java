package com.chua.mxnet.support.action;

import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.translator.ImageClassificationTranslator;
import ai.djl.translate.Translator;
import com.chua.common.support.annotations.Spi;
import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.utils.StringUtils;
import com.chua.pytorch.support.AbstractPytorchDetector;
import com.chua.pytorch.support.utils.LocationUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 动作识别
 * @author CH
 */
@Spi("InceptionV3ActionRecognizer")
@Deprecated
public final class InceptionV3ActionRecognizer extends AbstractPytorchDetector<Classifications> {
    public InceptionV3ActionRecognizer(DetectionConfiguration configuration) {
        super(configuration,
                translator(),
                "MXNet",
                null,
                StringUtils.defaultString(configuration.modelPath(), "inceptionv3_ucf101"),
                "https://aias-home.oss-cn-beijing.aliyuncs.com/models/inceptionv3_ucf101.zip",
                true);
    }

    @Override
    protected Class<Classifications> type() {
        return Classifications.class;
    }

    @Override
    protected List<PredictResult> toDetect(Classifications detections, Image img) {
        List<PredictResult> results = new LinkedList<>();

        List<Classifications.Classification> items = detections.items();
        for (Classifications.Classification item : items) {
            if (item.getProbability() < 0.55) {
                continue;
            }

            PredictResult predictResult = LocationUtils.convertPredictResult(item, img);
            results.add(predictResult);
        }
        return results;
    }

    public static Translator<Image, Classifications> translator() {
        Map<String, Object> arguments = new ConcurrentHashMap<>();
        arguments.put("width", 299);
        arguments.put("height", 299);
        arguments.put("resize", true);
        arguments.put("normalize", true);
        arguments.put("synsetFileName", "classes.txt");
        arguments.put("applySoftmax", true);

        return ImageClassificationTranslator.builder(arguments).build();
    }
}
