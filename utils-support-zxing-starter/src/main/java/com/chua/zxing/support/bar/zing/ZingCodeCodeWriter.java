package com.chua.zxing.support.bar.zing;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.lang.bar.BarCodeBuilder;
import com.chua.common.support.lang.bar.BarCodeLogoShape;
import com.chua.common.support.lang.bar.BarCodeWriter;
import com.chua.common.support.utils.StringUtils;
import com.chua.zxing.support.bar.BaseBarCodeWriter;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.chua.zxing.support.bar.zing.ZingQrcodeEyesRenderStrategy.POINT_BORDER;


/**
 * 条形码/二维码输出
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/5/24
 */
@Spi("zing-code")
public class ZingCodeCodeWriter extends BaseBarCodeWriter {


    protected BarCodeBuilder barCodeBuilder;

    @Override
    public BarCodeWriter config(BarCodeBuilder barCodeBuilder) {
        this.barCodeBuilder = barCodeBuilder;
        return this;
    }

    @Override
    public void toStream(OutputStream outputStream) throws Exception {
        BitMatrix bitMatrix = toBitMatrix();
        BufferedImage image = toBufferedImage(bitMatrix);
        //render image margin
        image = setRadius(image,
                barCodeBuilder.borderRadius,
                barCodeBuilder.borderSize,
                barCodeBuilder.borderColor,
                barCodeBuilder.borderStyle,
                barCodeBuilder.borderDashGranularity,
                barCodeBuilder.margin);


        renderLogo(image);
        image = renderBgImage(image);
        ImageIO.write(image, barCodeBuilder.outType, outputStream);

    }

    /**
     * 背景图片
     *
     * @param image image
     * @return image
     */
    private BufferedImage renderBgImage(BufferedImage image) throws IOException {
        if (StringUtils.isNullOrEmpty(barCodeBuilder.bgImage)) {
            return image;
        }

        // 首先先画背景图片
        BufferedImage backgroundImage = createBackground();
        BufferedImage tmpImg = new BufferedImage(barCodeBuilder.width, barCodeBuilder.height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D graphics = tmpImg.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.drawImage(backgroundImage, 0, 0, null);
        graphics.drawImage(transparentImage(image, 255), 0, 0, null);

        return tmpImg;
    }

    /**
     * 背景图
     *
     * @return 背景图
     * @throws IOException ex
     */
    private BufferedImage createBackground() throws IOException {
        BufferedImage image = null;
        if (!barCodeBuilder.bgImage.startsWith("http")) {
            image = ImageIO.read(new File(barCodeBuilder.bgImage));
        } else {
            image = ImageIO.read(new URL(barCodeBuilder.bgImage));
        }

        BufferedImage rs = new BufferedImage(barCodeBuilder.width, barCodeBuilder.height, image.getType());
        Graphics2D graphics = rs.createGraphics();
        graphics.drawImage(image.getScaledInstance(barCodeBuilder.width, barCodeBuilder.height, Image.SCALE_SMOOTH), 0, 0, null);
        graphics.dispose();
        return transparentImage(rs, (int) (barCodeBuilder.alpha * 255));
    }

    /**
     * 设置图片背景透明
     *
     * @param srcImage image
     * @param alpha    a
     * @return bi
     * @throws IOException ex
     */
    public static BufferedImage transparentImage(BufferedImage srcImage,
                                                 int alpha) throws IOException {
        int imgHeight = srcImage.getHeight();
        int imgWidth = srcImage.getWidth();
        int c = srcImage.getRGB(3, 3);
        BufferedImage tmpImg = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_4BYTE_ABGR);
        for (int i = 0; i < imgWidth; ++i) {
            for (int j = 0; j < imgHeight; ++j) {
                //把背景设为透明
                if (srcImage.getRGB(i, j) == c) {
                    tmpImg.setRGB(i, j, c & 0x00ffffff);
                }
                //设置透明度
                else {
                    int rgb = srcImage.getRGB(i, j);
                    Color c1 = new Color(rgb);
                    Color c2 = new Color(c1.getRed(), c1.getGreen(), c1.getBlue(), alpha);
                    tmpImg.setRGB(i, j, c2.getRGB());
                }
            }
        }
        return tmpImg;
    }


    /**
     * logo
     *
     * @param image source
     */
    private void renderLogo(BufferedImage image) throws IOException {
        if (StringUtils.isNullOrEmpty(barCodeBuilder.logoPath)) {
            return;
        }
        BufferedImage srcImage = image;

        BufferedImage logoImage = null;
        if (!barCodeBuilder.logoPath.startsWith("http")) {
            logoImage = ImageIO.read(new File(barCodeBuilder.logoPath));
        } else {
            logoImage = ImageIO.read(new URL(barCodeBuilder.logoPath));
        }

        final int ratio = barCodeBuilder.ratio;
        final int logoWidth = logoImage.getWidth(), logoHeight = logoImage.getHeight();
        float ratioWidthOfCodeImage = srcImage.getWidth() / (float) ratio;
        float ratioHeightOfCodeImage = srcImage.getHeight() / (float) ratio;
        float width = logoWidth > ratioWidthOfCodeImage ? ratioWidthOfCodeImage : logoWidth;
        float height = logoHeight > ratioHeightOfCodeImage ? ratioHeightOfCodeImage : logoHeight;
        //get logo panel position
        int padding = barCodeBuilder.padding * 2;
        int margin = barCodeBuilder.margin * 2;
        float w = width + padding + margin, h = height + padding + margin;
        float positionX = (srcImage.getWidth() - w) / 2;
        float positionY = (srcImage.getHeight() - h) / 2;

        Shape shape = null;

        if (barCodeBuilder.logoShape == BarCodeLogoShape.RECTANGLE) {
            shape = new RoundRectangle2D.Float(
                    positionX,
                    positionY,
                    w,
                    h,
                    barCodeBuilder.panelArcWidth,
                    barCodeBuilder.panelArcHeight);
        } else if (barCodeBuilder.logoShape == BarCodeLogoShape.CIRCLE) {
            shape = new Ellipse2D.Float(positionX, positionY, w, h);
        }

        if (shape == null) {
            return;
        }

        Graphics2D graphics = srcImage.createGraphics();
        graphics.setColor(getColor(barCodeBuilder.panelColor));
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.fill(shape);

        positionX += barCodeBuilder.margin;
        positionY += barCodeBuilder.margin;

        if (barCodeBuilder.logoShape == BarCodeLogoShape.RECTANGLE) {
            shape = new RoundRectangle2D.Float(positionX, positionY, width + padding, height + padding,
                    barCodeBuilder.arcWidth, barCodeBuilder.arcHeight);
        } else if (barCodeBuilder.logoShape == BarCodeLogoShape.CIRCLE) {
            shape = new Ellipse2D.Float(positionX, positionY, width + padding, height + padding);
            // Cut into a circle
            logoImage = clip(logoImage, Math.max(logoWidth, logoHeight));
        }
        graphics.setColor(getColor(barCodeBuilder.backgroundColor));
        graphics.fill(shape);
        // 1px offset is added to ensure center position..
        positionX += barCodeBuilder.padding + 1;

        // 1px offset is added to ensure center position..
        positionY += barCodeBuilder.padding + 1;
        graphics.drawImage(logoImage.getScaledInstance((int) width, (int) height, Image.SCALE_SMOOTH),
                (int) positionX, (int) positionY, null);

        //border
        graphics.setStroke(new BasicStroke(barCodeBuilder.borderSize));
        graphics.setColor(getColor(barCodeBuilder.borderColor));
        // anti-aliasing
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.draw(shape);

        //flush
        graphics.dispose();
        logoImage.flush();
        srcImage.flush();
    }

    /**
     * BitMatrix
     *
     * @return BitMatrix
     */
    public BitMatrix toBitMatrix() throws WriterException {
        MultiFormatWriter writer = new MultiFormatWriter();
        return writer.encode(barCodeBuilder.generate,
                BarcodeFormat.QR_CODE,
                barCodeBuilder.width,
                barCodeBuilder.height,
                createConfig());
    }

    /**
     * zing image
     *
     * @param bitMatrix bitMatrix
     * @return bi
     */
    private BufferedImage toBufferedImage(BitMatrix bitMatrix) throws WriterException {

        int width = bitMatrix.getWidth(), height = bitMatrix.getHeight();
        int modules = (4 - 1) * 4 + 21;
        int[] topLeftOnBit = bitMatrix.getTopLeftOnBit();

        ZingQrcodeEyesPosition position = new ZingQrcodeEyesPosition(modules, topLeftOnBit);

        int moduleHeight = position.getModuleHeight(height);
        int moduleWidth = position.getModuleWidth(width);

        int leftStartX = topLeftOnBit[0] + moduleWidth * POINT_BORDER.getStart();
        int leftEndX = topLeftOnBit[0] + moduleWidth * POINT_BORDER.getEnd();
        int topStartY = topLeftOnBit[1] + moduleHeight * POINT_BORDER.getStart();
        int topEndY = topLeftOnBit[1] + moduleHeight * POINT_BORDER.getEnd();
        int rightStartX = topLeftOnBit[0] + moduleWidth * (modules - POINT_BORDER.getEnd());
        int rightEndX = width - topLeftOnBit[0] - moduleWidth * POINT_BORDER.getStart();
        int bottomStartY = height - topLeftOnBit[1] - moduleHeight * POINT_BORDER.getEnd();
        int bottomEndY = height - topLeftOnBit[1] - moduleHeight * POINT_BORDER.getStart();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // custom color.
        int masterColor = getColor(barCodeBuilder.masterColor).getRGB();
        int slaveColor = getColor(barCodeBuilder.slaveColor).getRGB();

        for (int y = 0; y < height; y++) {

            for (int x = 0; x < width; x++) {
                // top left
                if (x >= leftStartX && x < leftEndX && y >= topStartY && y < topEndY) {
                }
                // top right
                else if (x >= rightStartX && x < rightEndX && y >= topStartY && y < topEndY) {
                }
                // bottom left
                else if (x >= leftStartX && x < leftEndX && y >= bottomStartY && y < bottomEndY) {
                }
                // non codeEyes region
                else {
                    image.setRGB(x, y, bitMatrix.get(x, y) ? masterColor : slaveColor);
                }
            }
        }

        position.setPosition(leftStartX, leftEndX, topStartY, topEndY, rightStartX, rightEndX, bottomStartY,
                bottomEndY);
        Color border = getColor(barCodeBuilder.codeEyesBorderColor);
        Color point = getColor(barCodeBuilder.codeEyesPointColor);
        BarCodeBuilder.QrCodeEyesFormat eyesFormat = barCodeBuilder.qrCodeEyesFormat;

        new ZingMultiFormatQrcodeEyesRenderer(bitMatrix)
                .render(image, eyesFormat, position, new Color(slaveColor), border, point);
        return image;
    }

    /**
     * 配置
     *
     * @return 配置
     */
    protected Map<EncodeHintType, ?> createConfig() {
        ErrorCorrectionLevel errorCorrectionLevel = ErrorCorrectionLevel.M;
        try {
            errorCorrectionLevel = ErrorCorrectionLevel.valueOf(barCodeBuilder.level);
        } catch (Exception ignored) {
        }
        Map<EncodeHintType, Object> hints = new ConcurrentHashMap<>(3);
        hints.put(EncodeHintType.ERROR_CORRECTION, errorCorrectionLevel);
        hints.put(EncodeHintType.MARGIN, 0);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        return hints;
    }

}
