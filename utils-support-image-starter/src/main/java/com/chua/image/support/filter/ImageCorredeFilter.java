package com.chua.image.support.filter;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.annotations.SpiOption;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.awt.image.BufferedImage;

/**
 * 腐蚀
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/6/12
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Spi("Correde")
@SpiOption("腐蚀滤镜")
@Accessors(chain = true)
public class ImageCorredeFilter extends AbstractImageFilter {

    /**
     * 结构元素
     */
    private static int[] sData = {
            0, 0, 0,
            0, 1, 0,
            0, 1, 1
    };
    private int threshold = 5;

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        int width = src.getWidth();
        int height = src.getHeight();
        if (null == dst) {
            dst = createCompatibleDestImage(src, null);
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                ///边缘不进行操作，边缘内才操作
                if (y > 0 && x > 0 && y < height - 1 && x < width - 1) {
                    int max = 0;
                    ///对结构元素进行遍历
                    for (int k = 0; k < sData.length; k++) {
                        ///商表示x偏移量
                        int x1 = k / 3;
                        ///余数表示y偏移量
                        int y1 = k % 3;


                        if (sData[k] != 0) {
                            ///不为0时，必须全部大于阈值，否则就设置为0并结束遍历
                            if (src.getRGB(y - 1 + x1, x - 1 + y1) >= threshold) {
                                if (src.getRGB(y - 1 + x1, x - 1 + y1) > max) {
                                    max = src.getRGB(y - 1 + x1, x - 1 + y1);
                                }
                            } else {
                                // 与结构元素不匹配,赋值0,结束遍历
                                max = 0;
                                break;
                            }
                        }
                    }
                    // 此处可以设置阈值，当max小于阈值的时候就赋为0
                    dst.setRGB(y, x, max);
                } else {
                    dst.setRGB(y, x, src.getRGB(y, x));
                }
            }
        }
        return dst;
    }

}
