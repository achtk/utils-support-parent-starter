package com.chua.common.support.image.converter;

import com.chua.common.support.image.AsciiImage;
import com.chua.common.support.image.matrix.GrayscaleMatrix;
import com.chua.common.support.image.strategy.BestCharacterFitStrategy;
import com.chua.common.support.utils.ArrayUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;

/**
 * Ascii 图像转换器
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/6/11
 */
public class AsciiToImageConverter extends AbstractAsciiConverter<BufferedImage> {
    /**
     * Instantiates a new ascii to image converter.
     *
     * @param characterCacher      the character cacher
     * @param characterFitStrategy the character fit strategy
     */
    public AsciiToImageConverter(final AsciiImage characterCacher,
                                 final BestCharacterFitStrategy characterFitStrategy) {
        super(characterCacher, characterFitStrategy);
    }

    /**
     * Copy image data over the source pixels image.
     * <p>
     * int[], int, int, int)
     */
    @Override
    public void addCharacterToOutput(
            final Map.Entry<Character, GrayscaleMatrix> characterEntry,
            final int[] sourceImagePixels, final int tileX, final int tileY, final int imageWidth) {
        int startCoordinateX = tileX
                * this.characterCache.getCharacterImageSize().width;
        int startCoordinateY = tileY
                * this.characterCache.getCharacterImageSize().height;

        // copy winner character
        for (int i = 0; i < characterEntry.getValue().getData().length; i++) {
            int xOffset = i % this.characterCache.getCharacterImageSize().width;
            int yOffset = i / this.characterCache.getCharacterImageSize().width;

            int component = (int) characterEntry.getValue().getData()[i];
            sourceImagePixels[ArrayUtils.convert2dTo1d(startCoordinateX
                    + xOffset, startCoordinateY + yOffset, imageWidth)] = new Color(
                    component, component, component).getRGB();
        }

    }

    /**
     * Write pixels to output image.
     */
    @Override
    protected void finalizeOutput(final int[] sourceImagePixels, final int imageWidth,
                                  final int imageHeight) {
        this.output.setRGB(0, 0, imageWidth, imageHeight, sourceImagePixels, 0,
                imageWidth);

    }

    /**
     * Create an empty buffered image.
     */
    @Override
    protected BufferedImage initializeOutput(final int imageWidth, final int imageHeight) {
        return new BufferedImage(imageWidth, imageHeight,
                BufferedImage.TYPE_INT_ARGB);
    }

}
