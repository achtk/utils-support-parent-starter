package com.chua.image.support.filter;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.annotations.SpiOption;
import com.chua.common.support.utils.RandomUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

/**
 * 重组
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/6/16
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(fluent = true)
@Spi("Reorganization")
@SpiOption("重组滤镜")
public class ImageReorganizationFilter extends AbstractImageFilter {

    private int cols;
    private int rows;

    public ImageReorganizationFilter() {
        this(8, 8);
    }

    public ImageReorganizationFilter(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;
    }

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        int chunkWidth = src.getWidth() / cols;
        int chunkHeight = src.getHeight() / rows;
        int count = 0;

        BufferedImage newSrc = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());

        LinkedList<BufferedImage> images = new LinkedList<>();
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                images.add(new BufferedImage(chunkWidth, chunkHeight, src.getType()));
                Graphics2D gr = images.get(count++).createGraphics();
                gr.drawImage(src, 0, 0, chunkWidth, chunkHeight,
                        chunkWidth * y, chunkHeight * x,
                        chunkWidth * y + chunkWidth, chunkHeight * x + chunkHeight, null);
                gr.rotate(random());
                gr.dispose();
            }
        }
        Graphics2D graphics = newSrc.createGraphics();
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                BufferedImage bufferedImage = images.poll();
                graphics.drawImage(bufferedImage,
                        chunkWidth * x, chunkHeight * y,
                        chunkWidth * x + chunkWidth, chunkHeight * y + chunkHeight,
                        null);
            }
        }
        graphics.dispose();

        return newSrc;
    }

    /**
     * 随机旋转
     *
     * @return 角度
     */
    private double random() {
        int anInt = RandomUtils.randomInt(4);
        return anInt * 90D;
    }
}
