package com.chua.common.support.lang.algorithm;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.lang.profile.DelegateProfile;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;

/**
 * 像素点相似度
 *
 * @author CH
 * @since 2021-12-02
 */
@Slf4j
@Spi("pixel-similarity")
public class PixelSimilarityMatchingAlgorithm extends DelegateProfile implements ImageMatchingAlgorithm {
    @Override
    public double match(File source, File target) {
        int[] pixels1;
        int[] pixels2;
        try {
            //图像指纹序列
            pixels1 = getPixelDeviate(source);
            //图像指纹序列
            pixels2 = getPixelDeviate(target);
        } catch (IOException e) {
            return 0.0D;
        }
        // 获取两个图的汉明距离（假设另一个图也已经按上面步骤得到灰度比较数组）
        int hammingDistance = getHammingDistance(pixels1, pixels2);
        // 通过汉明距离计算相似度，取值范围 [0.0, 1.0]
        return calSimilarity(hammingDistance);
    }

    /**
     * 图片序列
     *
     * @param imageFile 图片
     * @return 序列
     */
    private int[] getPixelDeviate(File imageFile) throws IOException {
        // 获取图像
        Image image = ImageIO.read(imageFile);
        // 转换至灰度
        image = toGrayscale(image);
        // 缩小成32x32的缩略图
        image = scale(image);
        // 获取灰度像素数组
        int[] pixels = getPixels(image);
        // 获取平均灰度颜色
        int averageColor = getAverageOfPixelArray(pixels);
        // 获取灰度像素的比较数组（即图像指纹序列）
        return getPixelDeviateWeightsArray(pixels, averageColor);
    }


    /**
     * 将任意Image类型图像转换为BufferedImage类型
     *
     * @param srcImage 图片
     * @return BufferedImage
     */
    public BufferedImage convertToBufferedFrom(Image srcImage) {
        BufferedImage bufferedImage = new BufferedImage(srcImage.getWidth(null),
                srcImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bufferedImage.createGraphics();
        g.drawImage(srcImage, null, null);
        g.dispose();
        return bufferedImage;
    }

    /**
     * 转换至灰度图
     *
     * @param image 图片
     * @return 灰度图
     */
    public BufferedImage toGrayscale(Image image) {
        BufferedImage sourceBuffered = convertToBufferedFrom(image);
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        ColorConvertOp op = new ColorConvertOp(cs, null);
        return op.filter(sourceBuffered, null);
    }

    /**
     * 缩放至32x32像素缩略图
     *
     * @param image 图片
     * @return 缩略图
     */
    public Image scale(Image image) {
        image = image.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        return image;
    }

    /**
     * 获取像素数组
     *
     * @param image 图片
     * @return 像素数组
     */
    public int[] getPixels(Image image) {
        int width = image.getWidth(null);
        int height = image.getHeight(null);
        int[] pixels = convertToBufferedFrom(image).getRGB(0, 0, width, height,
                null, 0, width);
        return pixels;
    }

    /**
     * 获取灰度图的平均像素颜色值
     *
     * @param pixels 像素
     * @return 平均像素颜色值
     */
    public int getAverageOfPixelArray(int[] pixels) {
        Color color;
        long sumRed = 0;
        for (int i = 0; i < pixels.length; i++) {
            color = new Color(pixels[i], true);
            sumRed += color.getRed();
        }
        return (int) (sumRed / pixels.length);
    }

    /**
     * 获取灰度图的像素比较数组（平均值的离差）
     *
     * @param pixels       像素
     * @param averageColor 平均值的离差
     * @return 灰度图
     */
    public int[] getPixelDeviateWeightsArray(int[] pixels, final int averageColor) {
        Color color;
        int[] dest = new int[pixels.length];
        for (int i = 0; i < pixels.length; i++) {
            color = new Color(pixels[i], true);
            dest[i] = color.getRed() - averageColor > 0 ? 1 : 0;
        }
        return dest;
    }

    /**
     * 获取两个缩略图的平均像素比较数组的汉明距离（距离越大差异越大）
     *
     * @param a 缩略图
     * @param b 缩略图
     * @return 汉明距离
     */
    public int getHammingDistance(int[] a, int[] b) {
        int sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i] == b[i] ? 0 : 1;
        }
        return sum;
    }

    /**
     * 汉明距离计算相似度
     *
     * @param hammingDistance 汉明距离
     * @return 相似度
     */
    public double calSimilarity(int hammingDistance) {
        int length = 32 * 32;
        double similarity = (length - hammingDistance) / (double) length;
        // 使用指数曲线调整相似度结果
        similarity = Math.pow(similarity, 2);
        return similarity;
    }
}
