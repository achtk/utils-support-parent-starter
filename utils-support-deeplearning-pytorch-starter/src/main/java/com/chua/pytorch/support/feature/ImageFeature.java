package com.chua.pytorch.support.feature;

import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.translate.TranslateException;
import com.chua.common.support.annotations.Spi;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.utils.StringUtils;
import com.chua.pytorch.support.FloatArrayPytorchFeature;
import com.chua.pytorch.support.utils.LocationUtils;

/**
 * 特征值
 *
 * @author CH
 */
@Spi("ImageFeature")
public class ImageFeature extends FloatArrayPytorchFeature<float[]> {

    public ImageFeature(DetectionConfiguration configuration) {
        super(configuration,
                new ImageTranslator(),
                StringUtils.defaultString(configuration.modelPath(), "CLIP-ViT-B-32-IMAGE"),
                "https://aias-home.oss-cn-beijing.aliyuncs.com/models/nlp_models/clip_series/CLIP-ViT-B-32-IMAGE.zip",
                true);
    }

    @Override
    public float[] predict(Object img) {
        Image image = LocationUtils.getImage(img);
        if (null == image) {
            return null;
        }

        try (Predictor<Image, float[]> predictor = model.newPredictor()) {
            return predictor.predict(image);
        } catch (TranslateException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Class<float[]> type() {
        return float[].class;
    }
}
