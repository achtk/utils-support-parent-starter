package com.chua.common.support.image.converter;

import com.chua.common.support.image.AsciiImage;
import com.chua.common.support.image.matrix.GrayscaleMatrix;
import com.chua.common.support.image.matrix.TiledGrayscaleMatrix;
import com.chua.common.support.image.strategy.BestCharacterFitStrategy;
import com.chua.common.support.utils.ArrayUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;

/**
 * 用于将图像转换为 ascii 艺术的类。输出和转换
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/6/11
 */
public abstract class AbstractAsciiConverter<Output> {

    /**
     * The character cache.
     */
    protected AsciiImage characterCache;

    /**
     * The character fit strategy used to determine the best character for each
     * source image tile.
     */
    protected BestCharacterFitStrategy characterFitStrategy;

    /**
     * The output.
     */
    protected Output output;

    /**
     * Instantiates a new ascii converter.
     *
     * @param characterCache       the character cache
     * @param characterFitStrategy the character fit strategy
     */
    public AbstractAsciiConverter(final AsciiImage characterCache,
                                  final BestCharacterFitStrategy characterFitStrategy) {
        this.characterCache = characterCache;
        this.characterFitStrategy = characterFitStrategy;
    }

    /**
     * Produces an output that is an ascii art of the supplied image.
     *
     * @param source the source
     * @return the buffered image
     */
    public Output convertImage(final BufferedImage source) {
        // dimension of each tile
        Dimension tileSize = this.characterCache.getCharacterImageSize();

        // round the width and height so we avoid partial characters
        int outputImageWidth = (source.getWidth() / tileSize.width)
                * tileSize.width;
        int outputImageHeight = (source.getHeight() / tileSize.height)
                * tileSize.height;

        // extract pixels from source image
        int[] imagePixels = source.getRGB(0, 0, outputImageWidth,
                outputImageHeight, null, 0, outputImageWidth);

        // process the pixels to a grayscale matrix
        GrayscaleMatrix sourceMatrix = new GrayscaleMatrix(imagePixels,
                outputImageWidth, outputImageHeight);

        // divide matrix into tiles for easy processing
        TiledGrayscaleMatrix tiledMatrix = new TiledGrayscaleMatrix(
                sourceMatrix, tileSize.width, tileSize.height);

        this.output = initializeOutput(outputImageWidth, outputImageHeight);

        // compare each tile to every character to determine best fit
        for (int i = 0; i < tiledMatrix.getTileCount(); i++) {

            GrayscaleMatrix tile = tiledMatrix.getTile(i);

            float minError = Float.MAX_VALUE;
            Map.Entry<Character, GrayscaleMatrix> bestFit = null;

            for (Map.Entry<Character, GrayscaleMatrix> charImage : characterCache) {
                GrayscaleMatrix charPixels = charImage.getValue();

                float error = this.characterFitStrategy.calculateError(
                        charPixels, tile);

                if (error < minError) {
                    minError = error;
                    bestFit = charImage;
                }
            }

            int tileX = ArrayUtils.convert1DtoX(i, tiledMatrix.getTilesX());
            int tileY = ArrayUtils.convert1DtoY(i, tiledMatrix.getTilesX());

            // copy character to output
            addCharacterToOutput(bestFit, imagePixels, tileX, tileY,
                    outputImageWidth);
        }

        finalizeOutput(imagePixels, outputImageWidth, outputImageHeight);

        return this.output;

    }

    /**
     * Gets the character fit strategy.
     *
     * @return the character fit strategy
     */
    public BestCharacterFitStrategy getCharacterFitStrategy() {
        return this.characterFitStrategy;
    }

    /**
     * Sets the character fit strategy.
     *
     * @param characterFitStrategy new character fit strategy
     */
    public void setCharacterFitStrategy(
            final BestCharacterFitStrategy characterFitStrategy) {
        this.characterFitStrategy = characterFitStrategy;
    }

    /**
     * Sets the character cache.
     *
     * @param characterCache new character cache
     */
    public void setCharacterCache(final AsciiImage characterCache) {
        this.characterCache = characterCache;
    }

    /**
     * Override this to insert the character at a specified position in the
     * output.
     *
     * @param characterEntry    character choosen as best fit
     * @param sourceImagePixels source image pixels. Can be
     * @param tileX             the tile x
     * @param tileY             the tile y
     * @param imageWidth        the image width
     */
    protected abstract void addCharacterToOutput(
            final Map.Entry<Character, GrayscaleMatrix> characterEntry,
            final int[] sourceImagePixels, final int tileX, final int tileY,
            final int imageWidth);

    /**
     * Override this if any action needs to be done at the end of the
     * conversion.
     *
     * @param sourceImagePixels source image pixels data. Can be
     * @param imageWidth        source image width
     * @param imageHeight       source image height
     */
    protected abstract void finalizeOutput(final int[] sourceImagePixels,
                                           final int imageWidth, final int imageHeight);

    /**
     * Override this to return an empty output object that will be filled during
     * the ascii art conversion.
     *
     * @param imageWidth  source image width
     * @param imageHeight source image height
     * @return the output
     */
    protected abstract Output initializeOutput(final int imageWidth,
                                               final int imageHeight);
}
