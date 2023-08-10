package com.chua.pytorch.support.biggan;

import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.translate.TranslateException;
import com.chua.common.support.annotations.Spi;
import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.pytorch.support.AbstractPytorchIODetector;

import java.util.LinkedList;
import java.util.List;

/**
 * biggan
 *
 * @author CH
 */
@Spi("gan128")
public class BigGAN128 extends AbstractPytorchIODetector<Long, Image> {
    public BigGAN128(DetectionConfiguration configuration) {
        this(configuration, 127, 0.4f);
    }

    public BigGAN128(DetectionConfiguration configuration, int size, float truncation) {
        super(configuration,
                new BigGANTranslator(size, truncation),
                "PyTorch",
                null,
                "biggan" + (size % 2 == 0 ? size : size + 1) + ".pt",
                "https://aias-home.oss-cn-beijing.aliyuncs.com/models/biggan" + size + ".zip",
                false);
    }


    @Override
    public List<PredictResult> predict(Object type) {
        List<PredictResult> results = new LinkedList<>();
        try (Predictor<Long, Image> predictor = model.newPredictor()) {
            Image image = predictor.predict(Converter.convertIfNecessary(type, Long.class));
            PredictResult predictResult = new PredictResult().setNdArray(image.getWrappedImage());
            results.add(predictResult);
        } catch (TranslateException e) {
            throw new RuntimeException(e);
        }
        return results;
    }

    @Override
    protected Class<Image> outType() {
        return Image.class;
    }

    @Override
    protected Class<Long> inType() {
        return Long.class;
    }
}
