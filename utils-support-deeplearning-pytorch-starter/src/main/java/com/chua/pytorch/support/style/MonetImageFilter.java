package com.chua.pytorch.support.style;

import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import com.chua.common.support.annotations.Spi;
import com.chua.common.support.annotations.SpiOption;
import com.chua.common.support.image.filter.AbstractImageFilter;
import com.chua.pytorch.support.common.LearningConfig;
import com.chua.pytorch.support.utils.LocationUtils;

import javax.annotation.Resource;
import java.awt.image.BufferedImage;

/**
 * MONET
 *
 * @author CH
 */
@Spi("Monet")
@SpiOption("莫奈滤镜")
public class MonetImageFilter extends AbstractImageFilter {

    private static final StyleTransfer STYLE_TRANSFER;
    @Resource
    private LearningConfig learningConfig;

    static {
        STYLE_TRANSFER = new StyleTransfer(StyleTransfer.Artist.MONET);
    }


    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        STYLE_TRANSFER.setLearningConfiguration(learningConfig);

        try (ZooModel<Image, Image> model = ModelZoo.loadModel(STYLE_TRANSFER.getCriteria());
             Predictor<Image, Image> predictor = model.newPredictor()) {
            Image output = predictor.predict(LocationUtils.getImage(src));
            return (BufferedImage) output.getWrappedImage();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
