package com.chua.common.support.image;

import com.chua.common.support.image.matrix.GrayscaleMatrix;

import java.awt.*;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * ascii
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/6/11
 */
public class AsciiImage implements Iterable<Map.Entry<Character, GrayscaleMatrix>> {
    /**
     * Some empirically chosen characters that give good results.
     */
    private static final char[] TO_CHAR_ARRAY = "$@B%8&WM#*oahkbdpqwmZO0QLCJUYXzcvunxrjft/\\|()1{}[]?-_+~<>i!lI;:,\"^`'. "
            .toCharArray();
    /**
     * A map of characters to their bitmaps.
     */
    protected final Map<Character, GrayscaleMatrix> imageCache;
    /**
     * Dimension of character image data.
     */
    private final Dimension characterImageSize;

    /**
     * Instantiates a new ascii img cache.
     *
     * @param characterImageSize the character image size
     * @param imageCache         the image cache
     */
    private AsciiImage(final Dimension characterImageSize,
                       final Map<Character, GrayscaleMatrix> imageCache,
                       final char[] characters) {
        this.characterImageSize = characterImageSize;
        this.imageCache = imageCache;
    }

    /**
     * Creates the cache with supplied font.
     *
     * @param font the font
     * @return the ascii img cache
     */
    public static AsciiImage create(final Font font) {
        return create(font, TO_CHAR_ARRAY);
    }

    /**
     * Initialize a new character cache with supplied font.
     *
     * @param font the font
     * @return the ascii img cache
     */
    public static AsciiImage create(final Font font, final char[] characters) {

        Dimension maxCharacterImageSize = calculateCharacterRectangle(font,
                characters);
        Map<Character, GrayscaleMatrix> imageCache = createCharacterImages(
                font, maxCharacterImageSize, characters);

        return new AsciiImage(maxCharacterImageSize, imageCache, characters);

    }

    /**
     * Calculate character rectangle for the given font metrics.
     *
     * @return the rectangle
     */
    private static Dimension calculateCharacterRectangle(final Font font,
                                                         final char[] characters) {
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.getGraphics();
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setFont(font);
        FontMetrics fm = graphics.getFontMetrics();

        Dimension maxCharacter = new Dimension();
        for (int i = 0; i < characters.length; i++) {
            String character = Character.toString(characters[i]);

            Rectangle characterRectangle = new TextLayout(character,
                    fm.getFont(), fm.getFontRenderContext()).getOutline(null)
                    .getBounds();

            if (maxCharacter.width < characterRectangle.getWidth()) {
                maxCharacter.width = (int) characterRectangle.getWidth();
            }

            if (maxCharacter.height < characterRectangle.getHeight()) {
                maxCharacter.height = (int) characterRectangle.getHeight();
            }
        }

        return maxCharacter;
    }

    /**
     * Creates the character images.
     *
     * @param font          the font
     * @param characterSize the character size
     * @return the map
     */
    private static Map<Character, GrayscaleMatrix> createCharacterImages(
            final Font font, final Dimension characterSize,
            final char[] characters) {
        // create each image
        BufferedImage img = new BufferedImage(characterSize.width,
                characterSize.height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.getGraphics();
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setFont(font);
        FontMetrics fm = graphics.getFontMetrics();

        Map<Character, GrayscaleMatrix> imageCache = new HashMap<>(16);

        for (char c : characters) {
            String character = Character.toString(c);

            g.setColor(Color.WHITE);
            g.fillRect(0, 0, characterSize.width, characterSize.height);
            g.setColor(Color.BLACK);

            Rectangle rect = new TextLayout(character, fm.getFont(),
                    fm.getFontRenderContext()).getOutline(null).getBounds();

            g.drawString(character, 0,
                    (int) (rect.getHeight() - rect.getMaxY()));

            int[] pixels = img.getRGB(0, 0, characterSize.width,
                    characterSize.height, null, 0, characterSize.width);
            GrayscaleMatrix matrix = new GrayscaleMatrix(pixels,
                    characterSize.width, characterSize.height);
            imageCache.put(c, matrix);
        }

        return imageCache;
    }

    /**
     * Gets the character image dimensions.
     *
     * @return character image dimensions
     */
    public Dimension getCharacterImageSize() {
        return characterImageSize;
    }

    /**
     * @see Iterable#iterator()
     */
    @Override
    public Iterator<Map.Entry<Character, GrayscaleMatrix>> iterator() {
        return imageCache.entrySet().iterator();
    }
}
