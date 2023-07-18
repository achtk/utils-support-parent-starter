package com.chua.image.support.filter;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.annotations.SpiOption;
import com.chua.common.support.lang.process.ProgressBar;
import com.chua.common.support.utils.RandomUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.*;

/**
 * 图形马赛克
 *
 * @author CH
 * @version 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
@Accessors(fluent = true)
@Spi("GraphicsMosaic")
@SpiOption("马赛克滤镜")
public class ImageGraphicsMosaicFilter extends AbstractImageFilter {

    /**
     * 图片的高度
     */
    public static final int MOSAIC_HEIGHT = 32;
    /**
     * 图片的宽度
     */
    public static final int MOSAIC_WIDTH = 32;
    public static final int GRAY_LEVEL_INTERVAL = 256 / 4;
    private String mosaic;
    private int mosaicWidth = MOSAIC_WIDTH;
    private int mosaicHeight = MOSAIC_HEIGHT;
    private int grayInterval = GRAY_LEVEL_INTERVAL;

    public ImageGraphicsMosaicFilter(String mosaic) {
        this.mosaic = mosaic;
    }

    public ImageGraphicsMosaicFilter() {
        this(".");
    }

    @Override
    public synchronized BufferedImage filter(BufferedImage inputImg, BufferedImage dst) {

        File pDir = new File(mosaic);
        if (!pDir.isDirectory()) {
            return inputImg;
        }

        BufferedImage outputImg = new BufferedImage(
                inputImg.getWidth() * mosaicWidth,
                inputImg.getHeight() * mosaicHeight,
                BufferedImage.TYPE_INT_RGB);
        Map<String, List<BufferedImage>> cache = new HashMap<>(1 << 4);

        for (File file : pDir.listFiles()) {
            BufferedImage compress = null;
            if (file.isFile()) {
                BufferedImage read = null;
                try {
                    read = ImageIO.read(file);
                } catch (Throwable ignored) {
                }
                if (null != read) {
                    compress = compressImage(read, mosaicWidth, mosaicHeight);
                }
            }
            if (null != compress) {
                int gray = getAvgGray(compress);
                String fileName = "L" + (gray / grayInterval);
                cache.computeIfAbsent(fileName, it -> new ArrayList<>()).add(compress);
            }
        }
        log.info("开始绘制...");
        int rgb, gray;
        String k;
        int height = inputImg.getHeight();
        int width = inputImg.getWidth();
        int total = height * width;
        try (ProgressBar process = new ProgressBar("绘制进度", total)) {

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    rgb = inputImg.getRGB(x, y);
                    gray = ((77 * ((rgb & 0xff0000) >> 16))
                            + (150 * ((rgb & 0x00ff00) >> 8))
                            + (29 * (rgb & 0x0000ff))) >> 8;
                    k = "L" + (gray / grayInterval);
                    BufferedImage mosaicImg = null;
                    try {
                        process.stepBy(((y + 1) * height + x));
                        mosaicImg = getImage(cache, k);
                        if (null == mosaicImg) {
                            continue;
                        }
                        outputImg.getGraphics().drawImage(mosaicImg,
                                x * mosaicWidth,
                                y * mosaicHeight,
                                mosaicWidth,
                                mosaicHeight, null);
                    } catch (Throwable ignored) {
                    }
                }
            }
        }
        log.info("绘制完成...");
        return outputImg;
    }

    /**
     * 获取图片
     *
     * @param cache 源
     * @param k     灰度
     * @return 图片
     */
    private BufferedImage getImage(Map<String, List<BufferedImage>> cache, String k) {
        List<BufferedImage> bufferedImages = cache.get(k);
        if (null == bufferedImages) {
            return null;
        }
        int anInt = RandomUtils.randomInt(bufferedImages.size());
        return bufferedImages.get(anInt);
    }

    /**
     * 压缩图片
     *
     * @param src       图片
     * @param dstWidth  宽度
     * @param dstHeight 高度
     * @return 图片
     */
    public static BufferedImage compressImage(BufferedImage src, int dstWidth, int dstHeight) {
        BufferedImage compress = new BufferedImage(dstWidth, dstHeight, BufferedImage.TYPE_INT_RGB);
        compress.getGraphics().drawImage(
                src.getScaledInstance(dstWidth, dstHeight, Image.SCALE_SMOOTH),
                0, 0, dstWidth, dstHeight, null);
        return compress;
    }

    /**
     * 平均灰度值
     */
    public static int getAvgGray(BufferedImage img) {
        int x, y, temp, graySum = 0;
        for (y = 0; y < img.getHeight(); y++) {
            for (x = 0; x < img.getWidth(); x++) {
                temp = img.getRGB(x, y);
                graySum += (((77 * ((temp & 0xff0000) >> 16))
                        + (150 * ((temp & 0x00ff00) >> 8))
                        + (29 * (temp & 0x0000ff))) >> 8);
            }
        }
        return graySum / (img.getWidth() * img.getHeight());
    }

    static class FileNameList {

        private List<String> list;
        private int counter = 0;

        public FileNameList(List<String> list) {
            if (list != null) {
                this.list = list;
                Collections.shuffle(list);
            }
        }

        public String next() {
            if (list == null) {
                return null;
            }
            if (counter == list.size()) {
                counter = 0;
                Collections.shuffle(list);
            }
            return list.get(counter++);
        }
    }
}
