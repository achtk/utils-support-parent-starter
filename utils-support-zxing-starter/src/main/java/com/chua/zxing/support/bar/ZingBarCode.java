package com.chua.zxing.support.bar;

import com.chua.common.support.lang.bar.BarCode;
import com.chua.common.support.lang.date.DateUtils;
import com.chua.common.support.utils.StringUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.oned.Code39Writer;
import com.google.zxing.oned.Code93Writer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * google bar实现
 *
 * @author CH
 * @version 1.0.0
 */
public class ZingBarCode implements BarCode {
    private String rightUpWords;
    private int height;
    private int width;
    private int margin;
    private String correction;
    private String leftDownWords;
    private String rightDownWords;
    private String barcode;
    /**
     * 加文字 条形码
     */
    private static final int WORDHEIGHT = 240;
    private boolean openDate;

    @Override
    public BarCode rightUpWords(String word) {
        this.rightUpWords = word;
        return this;
    }

    @Override
    public BarCode rightDownWords(String word) {
        this.rightDownWords = word;
        return this;
    }

    @Override
    public BarCode leftDownWords(String word) {
        this.leftDownWords = word;
        return this;
    }

    @Override
    public BarCode errorCorrection(String correction) {
        this.correction = correction;
        return this;
    }

    @Override
    public BarCode margin(int margin) {
        this.margin = margin;
        return this;
    }

    @Override
    public BarCode width(int width) {
        this.width = width;
        return this;
    }

    @Override
    public BarCode height(int height) {
        this.height = height;
        return this;
    }

    @Override
    public BarCode openDate() {
        this.openDate = true;
        return this;
    }

    @Override
    public BarCode writer(String barcode) {
        this.barcode = barcode;
        return this;
    }

    @Override
    public File toFile(String va, String outPath) {
        File outFile = new File(outPath);
        try {
            toStream(va, new FileOutputStream(outFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return outFile;
    }

    @Override
    public void toStream(String va, OutputStream stream) {
        Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
        // 设置编码方式
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        try {
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.valueOf(correction));
        } catch (IllegalArgumentException e) {
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        }
        hints.put(EncodeHintType.MARGIN, margin);

        try {
            Writer writer = createWrite();
            // 编码内容, 编码类型, 宽度, 高度, 设置参数
            BitMatrix bitMatrix = writer.encode(va, BarcodeFormat.valueOf(barcode), width, height, hints);
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            write(bufferedImage, stream);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    /**
     * 输出
     *
     * @param bufferedImage 流
     * @param stream        输出
     */
    private void write(BufferedImage bufferedImage, OutputStream stream) {
    }

    /**
     * 把带logo的二维码下面加上文字
     *
     * @param image   条形码图片
     * @param equipNo 设备编号
     * @return 返回BufferedImage
     * @author myc
     */
    public BufferedImage insertWords(BufferedImage image,
                                     String equipNo) {

        BufferedImage outImage = new BufferedImage(width, WORDHEIGHT, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = outImage.createGraphics();

        // 抗锯齿
        setGraphics2D(g2d);
        // 设置白色
        setColorWhite(g2d);
        //设置边框
        setDrawRect(g2d);
        // 设置虚线边框
        setDrawRectDottedLine(g2d);

        // 画条形码到新的面板
        g2d.drawImage(image, 10, 40, image.getWidth() - 20, image.getHeight(), null);
        // 画文字到新的面板
        Color color = new Color(0, 0, 0);
        g2d.setColor(color);
        // 字体、字型、字号
        g2d.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        //文字长度
        String str = equipNo.replace("", "  ").trim();
        int strWidth = g2d.getFontMetrics().stringWidth(str);
        //总长度减去文字长度的一半  （居中显示）
        //            int wordStartX=(width - strWidth) / 2;
        int wordStartX = (width - strWidth) / 2;
        //height + (outImage.getHeight() - height) / 2 + 12
        int wordStartY = height + 20;
        //左上角文字长度
        String printDate = "打印日期 " + DateUtils.format(DateUtils.current(), "yyyy-MM-dd");
        int leftUpWordsWidth = width - g2d.getFontMetrics().stringWidth(printDate);

        // 画文字-上部分
        if (!StringUtils.isNullOrEmpty(rightUpWords)) {
            g2d.drawString(rightUpWords, 20, 30);
        }

        if (openDate) {
            g2d.drawString(printDate, leftUpWordsWidth - 20, 30);
        }

        //文字-下部分
        g2d.drawString(str, wordStartX, wordStartY + 38);

        if (!StringUtils.isNullOrEmpty(rightDownWords)) {
            String[] split = rightDownWords.split("\r\n");
            for (int i = 0; i < split.length; i++) {
                String s = split[i];
                g2d.drawString(s, 20, wordStartY + 56 + i * 20);
            }
        }

        if (!StringUtils.isNullOrEmpty(leftDownWords)) {
            //左下角第一 文字长度
            int leftDownFirstWordsWidth = width - 20 - g2d.getFontMetrics().stringWidth(leftDownWords);
            String[] split = leftDownWords.split("\r\n");
            for (int i = 0; i < split.length; i++) {
                String s = split[i];
                g2d.drawString(s, leftDownFirstWordsWidth, wordStartY + 56 + i * 20);
            }
        }
        g2d.dispose();
        outImage.flush();
        return outImage;
    }


    /**
     * 设置 Graphics2D 属性  （抗锯齿）
     *
     * @param g2d Graphics2D提供对几何形状、坐标转换、颜色管理和文本布局更为复杂的控制
     */
    private static void setGraphics2D(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
        Stroke s = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
        g2d.setStroke(s);
    }

    /**
     * 设置背景为白色
     *
     * @param g2d Graphics2D提供对几何形状、坐标转换、颜色管理和文本布局更为复杂的控制
     */
    private static void setColorWhite(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        //填充整个屏幕
        g2d.fillRect(0, 0, 600, 600);
        //设置笔刷
        g2d.setColor(Color.BLACK);
    }

    /**
     * 设置边框
     *
     * @param g2d Graphics2D提供对几何形状、坐标转换、颜色管理和文本布局更为复杂的控制
     */
    private static void setDrawRect(Graphics2D g2d) {
        //设置笔刷
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawRect(5, 5, 425, 220);
    }

    /**
     * 设置边框虚线点
     *
     * @param g2d Graphics2D提供对几何形状、坐标转换、颜色管理和文本布局更为复杂的控制
     */
    private static void setDrawRectDottedLine(Graphics2D g2d) {
        //设置笔刷
        g2d.setColor(Color.BLUE);
        BasicStroke stroke = new BasicStroke(0.5f, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND, 0.5f, new float[]{1, 4}, 0.5f);
        g2d.setStroke(stroke);
        g2d.drawRect(0, 0, 435, 230);
    }

    /**
     * 写入器
     *
     * @return 写入器
     */
    private Writer createWrite() {
        if ("CODE_128".equals(barcode)) {
            return new Code128Writer();
        }

        if ("CODE_39".equals(barcode)) {
            return new Code39Writer();
        }


        if ("CODE_93".equals(barcode)) {
            return new Code93Writer();
        }

        return null;

    }
}
