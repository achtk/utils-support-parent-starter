package com.chua.paddlepaddle.support.ocr.rotation;

import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.pytorch.support.AbstractPytorchDetector;
import com.chua.pytorch.support.utils.LocationUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * 旋转检测
 *
 * @author CH
 */
public class OcrDirectionDetector extends AbstractPytorchDetector<Classifications> {
    public OcrDirectionDetector(DetectionConfiguration configuration) {
        super(configuration,
                new PpWordRotateTranslator(),
                "PaddlePaddle",
                null,
                "ch_ppocr_mobile_v2.0_cls_infer",
                "https://aias-home.oss-cn-beijing.aliyuncs.com/models/ocr_models/ch_ppocr_mobile_v2.0_cls_infer.zip",
                true);
    }

    @Override
    protected Class<Classifications> type() {
        return Classifications.class;
    }

    @Override
    protected List<PredictResult> toDetect(Classifications o, Image img) {
        List<PredictResult> results = new LinkedList<>();
        Classifications.Classification classification = o.best();
        results.add(LocationUtils.convertPredictResult(classification, img));
        return results;
    }
}
