package com.chua.common.support.image.filter;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.awt.*;
import java.awt.image.BufferedImage;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

/**
 * 马赛克滤镜
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/6/11
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class ImageMosaicFilter extends AbstractImageFilter {

    private int size = 8;

    public ImageMosaicFilter() {
    }

    public ImageMosaicFilter(int size) {
        this.size = size;
    }

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage image1) {
        BufferedImage spinImage = new BufferedImage(src.getWidth(), src.getHeight(), TYPE_INT_RGB);
        // 马赛克格尺寸太大或太小
        if (src.getWidth() < size || src.getHeight() < size || size <= 0) {
            return src;
        }

        // 方向绘制个数
        int xcount = 0;
        // y方向绘制个数
        int ycount = 0;
        if (src.getWidth() % size == 0) {
            xcount = src.getWidth() / size;
        } else {
            xcount = src.getWidth() / size + 1;
        }
        if (src.getHeight() % size == 0) {
            ycount = src.getHeight() / size;
        } else {
            ycount = src.getHeight() / size + 1;
        }
        //坐标
        int x = 0;
        int y = 0;
        // 绘制马赛克(绘制矩形并填充颜色)
        Graphics gs = spinImage.getGraphics();
        for (int i = 0; i < xcount; i++) {
            for (int j = 0; j < ycount; j++) {
                //马赛克矩形格大小
                int mwidth = size;
                int mheight = size;
                //横向最后一个比较特殊，可能不够一个size
                if (i == xcount - 1) {
                    mwidth = src.getWidth() - x;
                }
                if (j == ycount - 1) {
                    mheight = src.getHeight() - y;
                }
                // 矩形颜色取中心像素点RGB值
                int centerX = x;
                int centerY = y;
                if (mwidth % 2 == 0) {
                    centerX += mwidth / 2;
                } else {
                    centerX += (mwidth - 1) / 2;
                }
                if (mheight % 2 == 0) {
                    centerY += mheight / 2;
                } else {
                    centerY += (mheight - 1) / 2;
                }
                Color color = new Color(src.getRGB(centerX, centerY));
                gs.setColor(color);
                gs.fillRect(x, y, mwidth, mheight);
                // 计算下一个矩形的y坐标
                y = y + size;
            }
            // 还原y坐标
            y = 0;
            // 计算x坐标
            x = x + size;
        }
        gs.dispose();
        return spinImage;
    }

}
