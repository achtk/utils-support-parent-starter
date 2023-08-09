package com.chua.mxnet.support.image;

import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.utils.StringUtils;
import com.chua.pytorch.support.AbstractPytorchDetector;

import java.util.LinkedList;
import java.util.List;

/**
 * 图片检测
 * Darknet53Classification
 *
 * @author CH
 */
public class Darknet53Classification extends AbstractPytorchDetector<Classifications> {
    public Darknet53Classification(DetectionConfiguration configuration) {
        super(configuration,
                new Darknet53Translator(configuration),
                "MXNet",
                null,
                StringUtils.defaultString(configuration.modelPath(), "darknet53"),
                "https://aias-home.oss-cn-beijing.aliyuncs.com/models/darknet53.zip",
                true);
    }

    @Override
    protected Class<Classifications> type() {
        return Classifications.class;
    }

    @Override
    protected List<PredictResult> toDetect(Classifications o, Image img) {
        List<PredictResult> rs = new LinkedList<>();
        for (Classifications.Classification classification : o.topK(configuration.top())) {
            PredictResult predictResult = new PredictResult();
            predictResult.setText(classification.getClassName());
            predictResult.setClsLabel(classification.getClassName());
            predictResult.setScore((float) classification.getProbability());
            rs.add(predictResult);
        }
        return rs;
    }
}
