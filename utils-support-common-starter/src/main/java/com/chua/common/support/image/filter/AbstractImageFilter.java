package com.chua.common.support.image.filter;

import com.chua.common.support.constant.ImageType;
import com.chua.common.support.protocol.image.gif.GifDecoder;
import com.chua.common.support.protocol.image.gif.GifEncoder;
import com.chua.common.support.utils.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;


/**
 * 图像滤镜
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/6/11
 */
public abstract class AbstractImageFilter implements ImageFilter {
    protected int width;
    protected int height;
    protected byte[] rArr;
    protected byte[] gArr;
    protected byte[] bArr;
    protected SecureRandom randomNumbers = new SecureRandom();
    private String name;
    public static final double CLO_60 = 1.0 / 60.0;
    public static final double CLO_255 = 1.0 / 255.0;
    public int tr = 0, tg = 0, tb = 0;

    @Override
    public BufferedImage converter(BufferedImage image) throws IOException {
        initial(image);
        return filter(image, null);
    }

    protected void initial(BufferedImage image) {
        width = image.getWidth();
        height = image.getHeight();
        int[] input = new int[width * height];
        com.chua.common.support.utils.BufferedImageUtils.getRgb(image, 0, 0, width, height, input);
        int size = width * height;
        rArr = new byte[size];
        gArr = new byte[size];
        bArr = new byte[size];
        backFillData(input);
    }

    /**
     * 填充
     *
     * @param input 输入
     */
    private void backFillData(int[] input) {
        int c = 0, r = 0, g = 0, b = 0;
        int length = input.length;
        for (int i = 0; i < length; i++) {
            c = input[i];
            r = (c & 0xff0000) >> 16;
            g = (c & 0xff00) >> 8;
            b = c & 0xff;
            rArr[i] = (byte) r;
            gArr[i] = (byte) g;
            bArr[i] = (byte) b;
        }
    }


    @Override
    public OutputStream converter(InputStream image) throws IOException {
        try(InputStream is = image) {
            String imageFormat = getImageFormat();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            if (ImageType.GIF.name().equalsIgnoreCase(imageFormat) && !StringUtils.isNullOrEmpty(imageFormat)) {

                GifDecoder gifDecoder = new GifDecoder();
                GifEncoder gifEncoder = new GifEncoder();

                gifDecoder.read(image);

                gifEncoder.setRepeat(gifDecoder.getLoopCount());
                gifEncoder.start(out);

                int frameCount = gifDecoder.getFrameCount();
                for (int i = 0; i < frameCount; i++) {
                    BufferedImage frame = gifDecoder.getFrame(i);
                    gifEncoder.setDelay(gifDecoder.getDelay(i));
                    gifEncoder.addFrame(converter(frame));

                }
                gifEncoder.finish();

            } else {
                BufferedImage read = ImageIO.read(image);
                BufferedImage bufferedImage = converter(read);
                ImageIO.write(bufferedImage, getImageFormat(), out);
            }
            return out;
        }
    }

    @Override
    public String getImageFormat(String name) {
        this.name = name;
        return name;
    }

    @Override
    public String getImageFormat() {
        return name;
    }

    /**
     * 创建兼容的目标图像
     *
     * @param src        元数据
     * @param colorModel 颜色模型
     * @return 图像
     */
    public BufferedImage createCompatibleDestImage(BufferedImage src, ColorModel colorModel) {
        if (colorModel == null) {
            colorModel = src.getColorModel();
        }
        return new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(src.getWidth(), src.getHeight()), colorModel.isAlphaPremultiplied(), null);
    }

    /**
     * 滤镜
     *
     * @param src 目标
     * @param dst 源
     * @return 结果
     */
    abstract public BufferedImage filter(BufferedImage src, BufferedImage dst);

    /**
     * 获取获得边界 2 D
     *
     * @param src 元数据
     * @return 边界 2 D
     */
    public Rectangle2D getBounds2D(BufferedImage src) {
        return new Rectangle(0, 0, src.getWidth(), src.getHeight());
    }

    /**
     * 获得点 2 D
     *
     * @param srcPt 原始点
     * @param dstPt 目标点
     * @return 结果
     */
    public Point2D getPoint2D(Point2D srcPt, Point2D dstPt) {
        if (dstPt == null) {
            dstPt = new Point2D.Double();
        }
        dstPt.setLocation(srcPt.getX(), srcPt.getY());
        return dstPt;
    }

    /**
     * 从图像中获取 ARGB 像素的便捷方法。这试图避免 BufferedImage.getRGB 取消管理图像的性能损失.
     *
     * @param image  一个 BufferedImage 对象
     * @param x      像素块的左边缘
     * @param y      像素块的右边缘
     * @param width  像素阵列的宽度
     * @param height 像素阵列的高度
     * @param pixels 保存返回像素的数组。可能为空。
     * @return 像素
     * @see #setRgb
     */
    public int[] getRgb(BufferedImage image, int x, int y, int width, int height, int[] pixels) {
        int type = image.getType();
        if (type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB) {
            return (int[]) image.getRaster().getDataElements(x, y, width, height, pixels);
        }
        return image.getRGB(x, y, width, height, pixels, 0, width);
    }

    /**
     * 一种在图像中设置 ARGB 像素的便捷方法。这试图避免 BufferedImage.setRGB 取消管理图像的性能*损失。
     *
     * @param image  一个 BufferedImage 对象
     * @param x      像素块的左边缘
     * @param y      像素块的右边缘
     * @param width  像素阵列的宽度
     * @param height 像素阵列的高度
     * @param pixels 保存返回像素的数组。可能为空。
     * @see #getRgb
     */
    public void setRgb(BufferedImage image, int x, int y, int width, int height, int[] pixels) {
        int type = image.getType();
        if (type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB) {
            image.getRaster().setDataElements(x, y, width, height, pixels);
        } else {
            image.setRGB(x, y, width, height, pixels, 0, width);
        }
    }

    /**
     * 字节数组
     *
     * @param index 索引
     * @return 结果
     */
    public byte[] toColorByte(int index) {
        if (index == 0) {
            return rArr;
        } else if (index == 1) {
            return gArr;
        } else if (index == 2) {
            return bArr;
        } else {
            throw new IllegalArgumentException("invalid argument...");
        }
    }

    public BufferedImage toBitmap() {
        int[] pixels = new int[width * height];
        BufferedImage bitmap = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        setRgb(width, height, pixels, toColorByte(0), toColorByte(1), toColorByte(2));
        setRgb(bitmap, 0, 0, width, height, pixels);
        return bitmap;
    }

    public void putRgb(byte[] red, byte[] green, byte[] blue) {
        System.arraycopy(red, 0, rArr, 0, red.length);
        System.arraycopy(green, 0, gArr, 0, green.length);
        System.arraycopy(blue, 0, bArr, 0, blue.length);
    }

    public void setRgb(int width, int height, int[] pixels, byte[] r, byte[] g, byte[] b) {
        for (int i = 0; i < width * height; i++) {
            pixels[i] = 0xff000000 | ((r[i] & 0xff) << 16) | ((g[i] & 0xff) << 8) | b[i] & 0xff;
        }
    }


    /**
     * RGB色彩空间转换为HSL色彩空间
     */
    public double[] rgb2Hsl(int[] hsl) {
        double min, max, dif, sum;
        double f1, f2;
        double h, s, l;
        double[] hsl1 = {0.0, 0.0, 0.0};
        tr = hsl[0];
        tg = hsl[1];
        tb = hsl[2];
        min = tr;
        if (tg < min) {
            min = tg;
        }
        if (tb < min) {
            min = tb;
        }
        max = tr;
        f1 = 0.0;
        f2 = tg - tb;
        if (tg > max) {
            max = tg;
            f1 = 120.0;
            f2 = tb - tr;
        }
        if (tb > max) {
            max = tb;
            f1 = 240.0;
            f2 = tr - tg;
        }
        dif = max - min;
        sum = max + min;
        l = 0.5 * sum;
        if (dif == 0) {
            h = 0.0;
            s = 0.0;
        } else if (l < 127.5) {
            s = 255.0 * dif / sum;
        } else {
            s = 255.0 * dif / (510.0 - sum);
        }
        h = (f1 + 60.0 * f2) / dif;
        if (h < 0.0) {
            h += 360.0;
        }
        if (h > 360.0) {
            h -= 360.0;
        }
        hsl1[0] = h;
        hsl1[1] = s;
        hsl1[2] = l;
        return hsl1;
    }

    /**
     * HSL色彩空间转换为RGB色彩空间
     *
     * @param hsl
     * @return
     */
    public int[] hsl2Rgb(double[] hsl) {
        double h, s, l;
        h = hsl[0];
        s = hsl[1];
        l = hsl[2];
        int[] rgb1 = {0, 0, 0};
        double v1, v2, v3, h1;
        //HSL 转换为 RGB
        if (s == 0) {
            tr = (int) l;
            tg = (int) l;
            tb = (int) l;
        } else {
            if (l < 127.5) {
                v2 = CLO_255 / (255 + s);
            } else {
                v2 = l + s - CLO_255 * s * l;
            }
            v1 = 2 * l - v2;
            v3 = v2 - v1;
            h1 = h + 120.0;
            if (h1 >= 360.0) {
                h1 -= 360.0;
            }
            //计算tr
            if (h1 < 60) {
                tr = (int) (v1 + v3 * h1 * CLO_60);
            } else if (h1 < 180.0) {
                tr = (int) v2;
            } else if (h1 < 240.0) {
                tr = (int) (v1 + v3 * (4 - h1 * CLO_60));
            } else {
                tr = (int) v1;
            }
            //计算tg
            h1 = h;
            if (h1 < 60.0) {
                tg = (int) (v1 + v3 * h1 * CLO_60);
            } else if (h1 < 180.0) {
                tg = (int) v2;
            } else if (h1 < 240.0) {
                tg = (int) (v1 + v3 * (4 - h1 * CLO_60));
            } else {
                tg = (int) v1;
            }
            //计算tb
            h1 = h - 120.0;
            if (h1 < 0.0) {
                h1 += 360.0;
            }
            if (h1 < 60.0) {
                tb = (int) (v1 + v3 * h1 * CLO_60);
            } else if (h1 < 180.0) {
                tb = (int) v2;
            } else if (h1 < 240.0) {
                tb = (int) (v1 + v3 * (4 - h1 * CLO_60));
            } else {
                tb = (int) v1;
            }

        }
        rgb1[0] = tr;
        rgb1[1] = tg;
        rgb1[2] = tb;
        return rgb1;
    }


    public BufferedImage creatCompatibleDestImage(BufferedImage src, BufferedImage dest) {
        return new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
    }
}
