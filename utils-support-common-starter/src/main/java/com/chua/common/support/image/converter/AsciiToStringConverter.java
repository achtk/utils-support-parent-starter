package com.chua.common.support.image.converter;

import com.chua.common.support.image.AsciiImage;
import com.chua.common.support.image.matrix.GrayscaleMatrix;
import com.chua.common.support.image.strategy.BestCharacterFitStrategy;

import java.util.Map;

/**
 * Ascii 到字符串转换器
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/6/11
 */
public class AsciiToStringConverter extends AbstractAsciiConverter<StringBuffer> {
    /**
     * Instantiates a new ascii to string converter.
     *
     * @param characterCacher      the character cacher
     * @param characterFitStrategy the character fit strategy
     */
    public AsciiToStringConverter(final AsciiImage characterCacher,
                                  final BestCharacterFitStrategy characterFitStrategy) {
        super(characterCacher, characterFitStrategy);
    }

    /**
     * Append choosen character to StringBuffer.
     */
    @Override
    public void addCharacterToOutput(
            final Map.Entry<Character, GrayscaleMatrix> characterEntry,
            final int[] sourceImagePixels, final int tileX, final int tileY,
            final int imageWidth) {

        this.output.append(characterEntry.getKey());

        // append new line at the end of the row
        if ((tileX + 1)
                * this.characterCache.getCharacterImageSize().getWidth() == imageWidth) {
            this.output.append(System.lineSeparator());
        }

    }

    /**
     * Creates an empty string buffer;
     */
    @Override
    protected StringBuffer initializeOutput(final int imageWidth,
                                            final int imageHeight) {
        return new StringBuffer();
    }

    /**
     *
     */
    @Override
    protected void finalizeOutput(final int[] sourceImagePixels,
                                  final int imageWidth, int imageHeight) {

    }
}
