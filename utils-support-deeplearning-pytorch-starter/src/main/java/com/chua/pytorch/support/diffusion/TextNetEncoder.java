package com.chua.pytorch.support.diffusion;

import ai.djl.inference.Predictor;
import ai.djl.ndarray.NDList;
import ai.djl.translate.NoopTranslator;
import ai.djl.translate.TranslateException;
import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.utils.StringUtils;
import com.chua.pytorch.support.AbstractPytorchIODetector;

import java.util.Collections;
import java.util.List;

public class TextNetEncoder extends AbstractPytorchIODetector<NDList, NDList> {
    public TextNetEncoder(DetectionConfiguration configuration) {
        super(configuration,
                new NoopTranslator(),
                "PyTorch",
                "GPU".equalsIgnoreCase(configuration.device()) ? "unet_traced_model_gpu0" : "unet_traced_model",
                StringUtils.defaultString(configuration.modelPath(), "pytorch_cpu"),
                "",
                true);
    }

    @Override
    public List<PredictResult> predict(Object face) {
        try (Predictor<NDList, NDList> predictor = model.newPredictor()) {
            NDList predict = predictor.predict((NDList) face);
            return Collections.singletonList(new PredictResult().setNdArray(predict));
        } catch (TranslateException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Class<NDList> outType() {
        return NDList.class;
    }

    @Override
    protected Class<NDList> inType() {
        return NDList.class;
    }
}
