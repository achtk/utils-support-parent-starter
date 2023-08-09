package com.chua.mxnet.support.safetyhelmet;

import com.alibaba.fastjson2.JSONObject;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.pytorch.support.yolo.YoloTranslator;

/**
 * https://github.com/njvisionpower/Safety-Helmet-Wearing-Dataset
 *
 * @author CH
 */
public class SafetyHelmetTranslator extends YoloTranslator {

    public SafetyHelmetTranslator(DetectionConfiguration configuration) {
        super(YoloTranslator.builder(new JSONObject()
                        .fluentPut("width", configuration.width() <= 0 ? 640 : configuration.width())
                        .fluentPut("height", configuration.height() <= 0 ? 640 : configuration.height())
                        .fluentPut("resize", configuration.resize())
                        .fluentPut("rescale", configuration.rescale())
                        .fluentPut("threshold", configuration.threshold() <= 0f ? 0.2f : configuration.threshold())
                        .fluentPut("normalize", configuration.normalize())
                )
        );
    }
}
