package com.chua.pytorch.support.face.recognizer;

import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.translate.TranslateException;
import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.feature.detector.Detector;
import com.chua.pytorch.support.AbstractPytorchRecognizer;
import com.chua.pytorch.support.face.net.FaceLabelNet;
import com.chua.pytorch.support.utils.LocationUtils;

/**
 * PaddlePaddle
 *
 * @author CH
 */
public class PaddleFeatureRecognizer extends AbstractPytorchRecognizer<float[]> {
    private final FaceLabelNet faceLabelNet;

    public PaddleFeatureRecognizer(Detector detector, DetectionConfiguration configuration, FaceLabelNet faceLabelNet) {
        super(detector, configuration,
                new PaddlePaddleFaceFeatureTranslator(),
                "PaddlePaddle",
                "inference",
                "arcface_iresnet50_v1.0_infer",
                "https://aias-home.oss-cn-beijing.aliyuncs.com/models/sec_models/MobileFace.zip",
                true);

        this.faceLabelNet = faceLabelNet;
    }

    @Override
    public float[] predict(Object img) {
        Image image = LocationUtils.getImage(img);

        if (null == image) {
            return null;
        }

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
    protected PredictResult createPredictResult(Image subImage) {
        float[] feature = new float[0];
        try (Predictor<Image, float[]> predictor = model.newPredictor()) {
            try {
                feature = predictor.predict(subImage);
            } catch (TranslateException e) {
                e.printStackTrace();
            }
        }

        if (null == faceLabelNet) {
            return null;

        }
        PredictResult result = new PredictResult();
        //推理是属于哪个lab
        FaceLabelNet.MaxLab maxLab = null;
        for (int i = 0; i < faceLabelNet.features(); i++) {
            maxLab = faceLabelNet.predict(feature);
            if (null == maxLab) {
                continue;
            }

            result.setClsLabel(maxLab.maxLab);
            result.setClsScore(maxLab.maxSimilar);
            result.setScore(maxLab.maxSimilar);
            result.setText(maxLab.maxLab);
        }
        return result;
    }

    @Override
    protected Class<float[]> type() {
        return float[].class;
    }
}
