package com.chua.pytorch.support.resolution;

import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.TranslateException;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.feature.resolver.AbstractResolver;
import com.chua.common.support.utils.StringUtils;
import com.chua.pytorch.support.utils.LocationUtils;
import com.google.common.base.Joiner;

import java.util.List;

/**
 * SuperResolution
 *
 * @author CH
 */
public class SuperResolver extends AbstractResolver<Image> {

    private final ZooModel<Image, Image> model;

    public SuperResolver(DetectionConfiguration configuration) throws Exception {
        super(configuration);
        List<String> model1 = LocationUtils.getUrl(StringUtils.defaultString(configuration.modelPath(), "tf2_1"), "https://aias-home.oss-cn-beijing.aliyuncs.com/models/esrgan-tf2_1.zip", true);
        Criteria<Image, Image> criteria =
                Criteria.builder()
                        .setTypes(Image.class, Image.class)
                        .optModelUrls(Joiner.on(',').join(model1))
                        // .optModelUrls("/Users/calvin/Documents/build/tf_models/esrgan-tf2_1")
                        .optOption("Tags", "serve")
                        .optEngine("TensorFlow") // Use TensorFlow engine
                        .optOption("SignatureDefKey", "serving_default")
                        .optTranslator(new SuperResolutionTranslator())
                        .optProgress(new ProgressBar())
                        .build();

        this.model = criteria.loadModel();
    }

    @Override
    public Image resolve(Image img) {
        try (Predictor<Image, Image> predictor = model.newPredictor()) {
            return predictor.predict(img);
        } catch (TranslateException e) {
            throw new RuntimeException(e);
        }
    }
}
