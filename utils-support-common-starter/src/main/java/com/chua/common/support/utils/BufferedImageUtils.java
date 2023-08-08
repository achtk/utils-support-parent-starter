package com.chua.common.support.utils;


import com.chua.common.support.constant.CommonConstant;
import com.chua.common.support.constant.NumberConstant;
import com.chua.common.support.protocol.image.ImagePoint;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Base64;
import java.util.List;

/**
 * BufferedImage工具类
 *
 * @author CH
 * @since 2021-10-12
 */
public class BufferedImageUtils {

    /**
     * 获取图片
     *
     * @param source 文件流
     * @return 图片
     */
    public static BufferedImage getBufferedImage(byte[] source) {
        if (null == source) {
            return null;
        }
        try (ByteArrayInputStream bais = new ByteArrayInputStream(source)) {
            return ImageIO.read(bais);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 获取图片
     *
     * @param inputStream 文件流
     * @return 图片
     */
    public static BufferedImage getBufferedImage(InputStream inputStream) {
        if (null == inputStream) {
            return null;
        }
        try {
            return ImageIO.read(inputStream);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 获取图片
     *
     * @param file 文件
     * @return 图片
     */
    public static BufferedImage getBufferedImage(File file) {
        if (null == file || !file.exists()) {
            return null;
        }
        try {
            return ImageIO.read(file);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 获取宽高
     *
     * @param file 图片
     * @return 宽高
     */
    public static int[] size(File file) {
        if (null == file) {
            return new int[2];
        }
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(file);
        } catch (IOException e) {
            return new int[2];
        }
        return size(bufferedImage);
    }

    /**
     * 获取宽高
     *
     * @param bufferedImage 图片
     * @return 宽高
     */
    public static int[] size(BufferedImage bufferedImage) {
        if (null == bufferedImage) {
            return new int[2];
        }
        return new int[]{bufferedImage.getWidth(), bufferedImage.getHeight()};
    }

    /**
     * BufferedImage转成 base64
     *
     * @param bufferedImage   图片
     * @param imageFormatName 图片格式
     * @return base64
     * @throws IOException 转化异常
     */
    public static String getBufferedImageToBase64(BufferedImage bufferedImage, String imageFormatName) throws IOException {
        if (StringUtils.isNullOrEmpty(imageFormatName)) {
            imageFormatName = "png";
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, imageFormatName, stream);
        return Base64.getEncoder().encodeToString(stream.toByteArray());
    }

    /**
     * 旋转
     *
     * @param src   源图片
     * @param angel 角度
     * @return BufferedImage
     */
    public static BufferedImage rotate(BufferedImage src, int angel) {
        int width = src.getWidth(null);
        int height = src.getHeight(null);
        int type = src.getColorModel().getTransparency();
        Rectangle rectangle = calcRotatedSize(new Rectangle(new Dimension(width, height)), angel);
        BufferedImage bi = new BufferedImage(rectangle.width, rectangle.height, type);
        Graphics2D g2 = bi.createGraphics();
        g2.translate((rectangle.width - width) / 2, (rectangle.height - height) / 2);
        g2.rotate(Math.toRadians(angel), width / 2, height / 2);
        g2.drawImage(src, 0, 0, null);
        g2.dispose();
        return bi;
    }

    /**
     * 旋转信息
     *
     * @param src   矩形
     * @param angel 角度
     * @return 矩形
     */
    private static Rectangle calcRotatedSize(Rectangle src, int angel) {
        if (angel >= NumberConstant.NIGHT_TEEN) {
            if (angel / NumberConstant.NIGHT_TEEN % NumberConstant.INTEGER_TWO == 1) {
                int temp = src.height;
                src.height = src.width;
                src.width = temp;
            }
            angel = angel % 90;
        }
        double r = Math.sqrt(src.height * src.height + src.width * src.width) / 2;
        double len = 2 * Math.sin(Math.toRadians(angel) / 2) * r;
        double angelAlpha = (Math.PI - Math.toRadians(angel)) / 2;
        double angelDaltaWidth = Math.atan((double) src.height / src.width);
        double angelDaltaHeight = Math.atan((double) src.width / src.height);
        int lenDaltaWidth = (int) (len * Math.cos(Math.PI - angelAlpha - angelDaltaWidth));
        int lenDaltaHeight = (int) (len * Math.cos(Math.PI - angelAlpha - angelDaltaHeight));
        int desWidth = src.width + lenDaltaWidth * 2;
        int desHeight = src.height + lenDaltaHeight * 2;
        return new Rectangle(new Dimension(desWidth, desHeight));
    }

    /**
     * 图片
     *
     * @param url 图片地址
     * @return BufferedImage
     */
    public static BufferedImage toBufferedImage(String url) {
        try {
            return ImageIO.read(new URL(url));
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 写文件
     *
     * @param bufferedImages 图片
     * @param output         输出文件
     * @return 文件
     */
    public static void writeToFile(List<BufferedImage> bufferedImages, String output) {
        if (null == bufferedImages || StringUtils.isNullOrEmpty(output)) {
            return;
        }

        try {
            FileUtils.forceMkdirParent(new File(output));
        } catch (IOException ignored) {
        }
        int index = 0;
        for (BufferedImage bufferedImage : bufferedImages) {
            writeToFile(bufferedImage, new File(output, index++ + ".jpg").getAbsolutePath());
        }

    }

    /**
     * 写文件
     *
     * @param bufferedImage 图片
     * @param output        输出文件
     * @return 文件
     */
    public static File writeToFile(BufferedImage bufferedImage, String output) {
        if (null == bufferedImage || StringUtils.isNullOrEmpty(output)) {
            return null;
        }
        File file = new File(output);

        String formatName = FileUtils.getExtension(output);
        if (CommonConstant.JPG.equalsIgnoreCase(formatName) || CommonConstant.JPEG.equalsIgnoreCase(formatName)) {
            BufferedImage tag;
            tag = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_BGR);
            Graphics g = tag.getGraphics();
            g.drawImage(bufferedImage, 0, 0, null);
            g.dispose();
            bufferedImage = tag;
        }
        try {
            ImageIO.write(bufferedImage, FileUtils.getExtension(output), file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return file;
    }

    /**
     * 图片缩放,w，h为缩放的目标宽度和高度
     * src为源文件目录，dest为缩放后保存目录
     */
    public static BufferedImage zoomImage(BufferedImage src, ImagePoint point) throws Exception {
        if (point.getRate() > 0) {
            AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance(point.getRate(), point.getRate()), null);
            return ato.filter(src, null);
        }

        BufferedImage dest = createBufferedImage(src, point);
        Graphics graphics = dest.getGraphics();
        graphics.drawImage(src, (int) point.getX(), (int) point.getY(), point.getWidth(), point.getHeight(), null);
        graphics.dispose();
        return dest;
    }

    /**
     * 图片缩放,w，h为缩放的目标宽度和高度
     * src为源文件目录，dest为缩放后保存目录
     */
    public static void zoomImage(String src, String dest, int w, int h) throws Exception {

        double wr = 0, hr = 0;
        File srcFile = new File(src);
        File destFile = new File(dest);

        BufferedImage bufImg = ImageIO.read(srcFile);
        Image itemp = bufImg.getScaledInstance(w, h, Image.SCALE_SMOOTH);

        wr = w * 1.0 / bufImg.getWidth();
        hr = h * 1.0 / bufImg.getHeight();

        AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance(wr, hr), null);
        itemp = ato.filter(bufImg, null);
        try {
            ImageIO.write((BufferedImage) itemp, dest.substring(dest.lastIndexOf(".") + 1), destFile);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 图片按比率缩放
     * rate 缩放比率
     */
    public static void zoomImage(String src, String dest, double rate) throws Exception {
        File srcFile = new File(src);
        File destFile = new File(dest);

        long fileSize = srcFile.length();
        //缩放比例小于1时，才进行缩放
        if (rate >= NumberConstant.ONE) {
            return;
        }

        BufferedImage bufImg = ImageIO.read(srcFile);
        Image itemp = bufImg.getScaledInstance(bufImg.getWidth(), bufImg.getHeight(), Image.SCALE_SMOOTH);

        AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance(rate, rate), null);
        itemp = ato.filter(bufImg, null);
        try {
            ImageIO.write((BufferedImage) itemp, dest.substring(dest.lastIndexOf(".") + 1), destFile);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 创建空对象
     *
     * @param bufferedImage 原始数据
     */
    public static BufferedImage createBufferedImage(BufferedImage bufferedImage) {
        return createBufferedImage(bufferedImage, null);
    }

    /**
     * 创建空对象
     *
     * @param bufferedImage 原始数据
     * @param point         point
     * @return 结果
     */
    public static BufferedImage createBufferedImage(BufferedImage bufferedImage, ImagePoint point) {
        if (null == point) {
            return new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getType());
        }
        return new BufferedImage(point.getWidth(), point.getHeight(), bufferedImage.getType());
    }

    /**
     * image -> byte[]
     *
     * @param bufferedImage image
     * @param contentType   类型
     * @return byte[]
     */
    public static byte[] createByteArray(BufferedImage bufferedImage, String contentType) {
        if (null == bufferedImage || StringUtils.isNullOrEmpty(contentType)) {
            return new byte[0];
        }

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(bufferedImage, contentType.replace("image/", ""), byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            return new byte[0];
        }
    }

    /**
     * A convenience method for getting ARGB pixels from an image. This tries to avoid the performance
     * penalty of BufferedImage.getRGB unmanaging the image.
     */
    public static int[] getRgb(BufferedImage image, int x, int y, int width, int height, int[] pixels) {
        int type = image.getType();
        if (type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB) {
            return (int[]) image.getRaster().getDataElements(x, y, width, height, pixels);
        }
        return image.getRGB(x, y, width, height, pixels, 0, width);
    }

    public static int clamp(float c) {
        float p = (c > 255 ? 255 : ((c < 0) ? 0 : c));
        return (int) p;
    }

    /**
     * 写入流
     * @param bufferedImage 图片
     * @param stream 流
     * @param imageType 类型
     */
    public static void writeToStream(BufferedImage bufferedImage, OutputStream stream, String imageType) {
        try {
            ImageIO.write(bufferedImage, imageType, stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
