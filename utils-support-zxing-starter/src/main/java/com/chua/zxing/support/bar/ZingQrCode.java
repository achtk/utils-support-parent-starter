package com.chua.zxing.support.bar;

import com.chua.common.support.lang.bar.AbstractQrCode;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * google bar实现
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/5/24
 */
public class ZingQrCode extends AbstractQrCode {


    private static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;

    @Override
    public File toFile(String outPath) {
        return barType == BarType.BAR_CODE ? barCode(outPath) : barCode2(outPath);
    }

    @Override
    public void toStream(OutputStream stream) {
        if (barType == BarType.BAR_CODE) {
            barCode(stream);
        } else {
            barCode2(stream);
        }
    }

    /**
     * 二维码
     *
     * @param outPath 输出文件
     * @return 条码
     */
    private File barCode2(String outPath) {
        File file = new File(outPath);
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(word, BarcodeFormat.QR_CODE, width, height, createConfig());
            Path path = file.toPath();
            //写到指定路径下
            MatrixToImageWriter.writeToPath(bitMatrix, type, path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return createLogo(file);
    }

    /**
     * 二维码
     *
     * @param stream 输出文件
     */
    private void barCode2(OutputStream stream) {
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(word, BarcodeFormat.QR_CODE, width, height, createConfig());
            //写到指定路径下
            MatrixToImageWriter.writeToStream(bitMatrix, type, stream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        createLogo(stream);
    }

    /**
     * logo
     *
     * @param file 文件
     * @return this
     */
    private File createLogo(File file) {
        if (null == logo) {
            return file;
        }
        try {
            BufferedImage bufferedImage = inLogo(ImageIO.read(file));
            ImageIO.write(bufferedImage, type, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    protected BufferedImage inLogo(BufferedImage bufferedImage) {
        //*************添加logo*****************
        try {
            //获取画笔
            Graphics2D graphics = bufferedImage.createGraphics();
            //读取logo图片
            BufferedImage logo = ImageIO.read(this.logo);
            //设置二维码大小，太大了会覆盖二维码，此处为20%
            int logoWidth = Math.min(logo.getWidth(), bufferedImage.getWidth() * 2 / 10);
            int logoHeight = Math.min(logo.getHeight(), bufferedImage.getHeight() * 2 / 10);
            //设置logo图片放置的位置，中心
            int x = (bufferedImage.getWidth() - logoWidth) / 2;
            int y = (bufferedImage.getHeight() - logoHeight) / 2;

            int arch = 20;
            //设置复合
            graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            //设置渲染提示
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            //开始合并并绘制图片
            graphics.drawImage(logo, x, y, logoWidth, logoHeight, null);
            // 设置基本属性
            BasicStroke stroke = new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            // 设置笔画对象
            graphics.setStroke(stroke);
            // 指定弧度的圆角矩形
            RoundRectangle2D.Float round = new RoundRectangle2D.Float(x, y, logoWidth, logoHeight, arch, arch);
            // 设置底色
            graphics.setColor(Color.WHITE);
            // 绘制圆弧矩形
            graphics.draw(round);
            // 设置logo 有一道灰色边框
            BasicStroke stroke2 = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            // 设置笔画对象
            graphics.setStroke(stroke2);
            // 设置边框
            RoundRectangle2D.Float round2 = new RoundRectangle2D.Float(x + 2, y + 2, logoWidth - 4, logoHeight - 4, arch, arch);
            // 设置边框颜色
            graphics.setColor(new Color(128, 128, 128));
            // 绘制圆弧矩形
            graphics.draw(round2);

            graphics.dispose();
            logo.flush();
            bufferedImage.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bufferedImage;
    }

    /**
     * logo
     *
     * @param stream 文件
     * @return this
     */
    private void createLogo(OutputStream stream) {
        if (null == logo) {
            return;
        }
        //*************添加logo*****************
        try {
            BufferedImage bufferedImage = inLogo(ImageIO.read(parse(stream)));
            ImageIO.write(bufferedImage, type, stream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 定义二维码参数
     *
     * @return 参数
     */
    private Map<EncodeHintType, Object> createConfig() {
        //定义二维码参数
        Map<EncodeHintType, Object> hints = new HashMap<>(DEFAULT_INITIAL_CAPACITY);
        //设置编码
        hints.put(EncodeHintType.CHARACTER_SET, charset.name());
        // 指定纠错等级,纠错级别（L 7%、M 15%、Q 25%、H 30%）
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        //设置边距默认是5
        hints.put(EncodeHintType.MARGIN, 1);
        return hints;
    }

    /**
     * 条码
     *
     * @param outPath 输出文件
     * @return 条码
     */
    private File barCode(String outPath) {
        File file = new File(outPath);
        //定义位图矩阵BitMatrix
        try {
            // 使用code_128格式进行编码生成100*25的条形码
            BitMatrix matrix = new MultiFormatWriter().encode(word, BarcodeFormat.CODE_128, width, height, createConfig());
            MatrixToImageWriter.writeToPath(matrix, type, file.toPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return createLogo(file);
    }

    /**
     * 条码
     *
     * @param stream 输出文件
     * @return 条码
     */
    private void barCode(OutputStream stream) {
        //定义位图矩阵BitMatrix
        try {
            // 使用code_128格式进行编码生成100*25的条形码
            BitMatrix matrix = new MultiFormatWriter().encode(word, BarcodeFormat.CODE_128, width, height, createConfig());
            MatrixToImageWriter.writeToStream(matrix, type, stream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        createLogo(stream);
    }


    public ByteArrayInputStream parse(final OutputStream out) throws Exception {
        if (out instanceof ByteArrayOutputStream) {
            final ByteArrayInputStream swapStream = new ByteArrayInputStream(((ByteArrayOutputStream) out).toByteArray());
            return swapStream;

        }
        return null;
    }
}
