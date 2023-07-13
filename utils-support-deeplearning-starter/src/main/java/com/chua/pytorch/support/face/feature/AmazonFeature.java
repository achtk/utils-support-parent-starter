package com.chua.pytorch.support.face.feature;

import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.translate.TranslateException;
import com.chua.common.support.annotations.Spi;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.pytorch.support.FloatArrayPytorchFeature;
import com.chua.pytorch.support.utils.LocationUtils;

/**
 * 特征值
 */
@Spi("amazon")
public class AmazonFeature extends FloatArrayPytorchFeature<float[]> {


    public AmazonFeature(DetectionConfiguration configuration) {
        super(configuration,
                new FaceFeatureTranslator(),
                "PyTorch",
                "face_feature",
                "amazon",
                "https://resources.djl.ai/test-models/pytorch/face_feature.zip",
                true);
    }

    @Override
    public float[] predict(Object img) {
        Image image = LocationUtils.getImage(img);
        try (Predictor<Image, float[]> predictor = model.newPredictor()) {
            try {
                return  predictor.predict(image);
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
