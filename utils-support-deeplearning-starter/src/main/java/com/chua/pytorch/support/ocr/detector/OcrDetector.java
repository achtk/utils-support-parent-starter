package com.chua.pytorch.support.ocr.detector;

import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.output.DetectedObjects;
import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.pytorch.support.AbstractPytorchDetector;
import com.chua.pytorch.support.utils.LocationUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ocr檢測
 *
 * @author CH
 */
public class OcrDetector extends AbstractPytorchDetector<DetectedObjects> {
    public OcrDetector(DetectionConfiguration configuration) {
        super(configuration,
                new PpWordDetectionTranslator(new ConcurrentHashMap<>(), configuration),
                "PaddlePaddle",
                null,
               "ch_PP-OCRv3_det_infer",
                "https://aias-home.oss-cn-beijing.aliyuncs.com/models/ocr_models/ch_PP-OCRv3_det_infer.zip",
                true);
    }

    @Override
    protected Class<DetectedObjects> type() {
        return DetectedObjects.class;
    }

    @Override
    protected List<PredictResult> toDetect(DetectedObjects o, Image img) {
        List<PredictResult> results = new LinkedList<>();
        List<Classifications.Classification> items = o.items();
        for (Classifications.Classification item : items) {
            PredictResult predictResult = LocationUtils.convertPredictResult(item, img);
            predictResult.setBoundingBox(((DetectedObjects.DetectedObject) item).getBoundingBox());
            results.add(predictResult);
        }
        return results;
    }
}
