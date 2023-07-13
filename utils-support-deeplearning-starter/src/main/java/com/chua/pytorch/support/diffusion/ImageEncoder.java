package com.chua.pytorch.support.diffusion;

import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.ndarray.NDArray;
import ai.djl.translate.TranslateException;
import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.utils.StringUtils;
import com.chua.pytorch.support.AbstractPytorchIODetector;
import com.chua.pytorch.support.utils.LocationUtils;

import java.util.Collections;
import java.util.List;

/**
 * 图片
 * @author CH
 */
public class ImageEncoder  extends AbstractPytorchIODetector<Image, NDArray> {
    public ImageEncoder(DetectionConfiguration configuration) {
        super(configuration,
                new EncoderTranslator(),
                "PyTorch",
                "vae_encoder_model",
                StringUtils.defaultString(configuration.modelPath(), "pytorch_cpu"),
                "",
                true);
    }

    @Override
    public List<PredictResult> detect(Object face) {
        try (Predictor<Image, NDArray> predictor = model.newPredictor()) {
            NDArray predict = predictor.predict(LocationUtils.getImage(face));
            return Collections.singletonList(new PredictResult().setNdArray(predict));
        } catch (TranslateException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Class<NDArray> outType() {
        return NDArray.class;
    }

    @Override
    protected Class<Image> inType() {
        return Image.class;
    }
}
