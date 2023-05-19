package com.chua.pytorch.support.face.feature;

import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.translate.TranslateException;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.pytorch.support.FloatArrayPytorchFeature;
import com.chua.pytorch.support.face.recognizer.PaddlePaddleFaceFeatureTranslator;
import com.chua.pytorch.support.utils.LocationUtils;

public class PaddleFeature extends FloatArrayPytorchFeature<float[]> {

    public PaddleFeature(DetectionConfiguration configuration) throws Exception {
        super(configuration,
                new PaddlePaddleFaceFeatureTranslator(),
                "PaddlePaddle",
                "inference",
                "arcface_iresnet50_v1.0_infer",
                "https://aias-home.oss-cn-beijing.aliyuncs.com/models/sec_models/MobileFace.zip",
                false);
    }

    @Override
    public float[] predict(Object img) {
        Image image = LocationUtils.getImage(img);
        try (Predictor<Image, float[]> predictor = model.newPredictor()) {
            try {
                return predictor.predict(image);
            } catch (TranslateException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected Class<float[]> type() {
        return float[].class;
    }
}
