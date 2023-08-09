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
 * UKIYOE
 *
 * @author CH
 */
@Spi("Ukiyoe")
@SpiOption("浮世绘滤镜")
public class UkiyoeImageFilter extends AbstractImageFilter {

    private static final StyleTransfer STYLE_TRANSFER;
    @Resource
    private LearningConfig learningConfig;

    static {
        STYLE_TRANSFER = new StyleTransfer(StyleTransfer.Artist.UKIYOE);
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
