package com.chua.pytorch.support.reflective;

import com.alibaba.fastjson2.JSONObject;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.pytorch.support.yolo.YoloTranslator;

/**
 * @author CH
 */
public class LargeHelmetHeadPersonTranslator extends YoloTranslator {
    public LargeHelmetHeadPersonTranslator(DetectionConfiguration configuration) {
        super(YoloTranslator.builder(new JSONObject()
                        .fluentPut("width", configuration.width() <= 0 ? 640 : configuration.width())
                        .fluentPut("height", configuration.height() <= 0 ? 640 : configuration.height())
                        .fluentPut("resize", configuration.resize())
                        .fluentPut("threshold", configuration.threshold() <= 0f ? 0.2f:configuration.threshold())
                        .fluentPut("nmsThreshold", configuration.nmsThreshold() <= 0f ? 0.5f:configuration.nmsThreshold())
                        .fluentPut("rescale", configuration.rescale())
                )
        );
    }
}
