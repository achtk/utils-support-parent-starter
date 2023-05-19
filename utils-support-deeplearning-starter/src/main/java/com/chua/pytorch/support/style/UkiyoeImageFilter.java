package com.chua.pytorch.support.style;

import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import com.chua.common.support.image.filter.AbstractImageFilter;
import com.chua.pytorch.support.utils.LocationUtils;

import java.awt.image.BufferedImage;

/**
 * UKIYOE
 *
 * @author CH
 */
public class UkiyoeImageFilter extends AbstractImageFilter {

    private static final StyleTransfer STYLE_TRANSFER;

    static {
        STYLE_TRANSFER = new StyleTransfer(StyleTransfer.Artist.UKIYOE);
    }


    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        try (ZooModel<Image, Image> model = ModelZoo.loadModel(STYLE_TRANSFER.getCriteria());
             Predictor<Image, Image> predictor = model.newPredictor()) {
            Image output = predictor.predict(LocationUtils.getImage(src));
            return (BufferedImage) output.getWrappedImage();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
