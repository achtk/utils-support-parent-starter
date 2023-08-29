package com.chua.common.support.utils;

import com.chua.common.support.converter.Converter;
import com.chua.common.support.image.BackgroundRemoval;
import com.chua.common.support.image.Img;
import com.chua.common.support.resource.resource.Resource;

import javax.imageio.*;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.*;
import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.*;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_HASH;
import static com.chua.common.support.constant.CommonConstant.SYMBOL_HASH_CHAR;
import static java.awt.image.BufferedImage.SCALE_SMOOTH;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;

/**
 * 图片处理工具类：<br>
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/5/5
 */
public class ImageUtils {
    /**
     * 图形交换格式
     */
    public static final String IMAGE_TYPE_GIF = "gif";
    /**
     * 联合照片专家组
     */
    public static final String IMAGE_TYPE_JPG = "jpg";
    /**
     * 联合照片专家组
     */
    public static final String IMAGE_TYPE_JPEG = "jpeg";
    /**
     * 英文Bitmap（位图）的简写，它是Windows操作系统中的标准图像文件格式
     */
    public static final String IMAGE_TYPE_BMP = "bmp";
    /**
     * 可移植网络图形
     */
    public static final String IMAGE_TYPE_PNG = "png";
    /**
     * Photoshop的专用格式Photoshop
     */
    public static final String IMAGE_TYPE_PSD = "psd";

    /**
     * RGB颜色范围上限
     */
    private static final int RGB_COLOR_BOUND = 256;

    /**
     * 缩放图像（按比例缩放），目标文件的扩展名决定目标文件类型
     *
     * @param srcImageFile  源图像文件
     * @param destImageFile 缩放后的图像文件，扩展名决定目标类型
     * @param scale         缩放比例。比例大于1时为放大，小于1大于0为缩小
     */
    public static void scale(File srcImageFile, File destImageFile, float scale) throws IOException {
        scale(read(srcImageFile), destImageFile, scale);
    }

    /**
     * 缩放图像（按比例缩放）<br>
     * 缩放后默认为jpeg格式，此方法并不关闭流
     *
     * @param srcStream  源图像来源流
     * @param destStream 缩放后的图像写出到的流
     * @param scale      缩放比例。比例大于1时为放大，小于1大于0为缩小
     * @since 3.0.9
     */
    public static void scale(InputStream srcStream, OutputStream destStream, float scale) throws IOException {
        scale(read(srcStream), destStream, scale);
    }

    /**
     * 缩放图像（按比例缩放）<br>
     * 缩放后默认为jpeg格式，此方法并不关闭流
     *
     * @param srcStream  源图像来源流
     * @param destStream 缩放后的图像写出到的流
     * @param scale      缩放比例。比例大于1时为放大，小于1大于0为缩小
     * @since 3.1.0
     */
    public static void scale(ImageInputStream srcStream, ImageOutputStream destStream, float scale) throws IOException {
        scale(read(srcStream), destStream, scale);
    }

    /**
     * 缩放图像（按比例缩放）<br>
     * 缩放后默认为jpeg格式，此方法并不关闭流
     *
     * @param srcImg   源图像来源流
     * @param destFile 缩放后的图像写出到的流
     * @param scale    缩放比例。比例大于1时为放大，小于1大于0为缩小
     * @throws IOException IO异常
     * @since 3.2.2
     */
    public static void scale(Image srcImg, File destFile, float scale) throws IOException {
        Img.from(srcImg).setTargetImageType(FileUtils.getExtension(destFile)).scale(scale).write(destFile);
    }

    /**
     * 缩放图像（按比例缩放）<br>
     * 缩放后默认为jpeg格式，此方法并不关闭流
     *
     * @param srcImg 源图像来源流
     * @param out    缩放后的图像写出到的流
     * @param scale  缩放比例。比例大于1时为放大，小于1大于0为缩小
     * @throws IOException IO异常
     * @since 3.2.2
     */
    public static void scale(Image srcImg, OutputStream out, float scale) throws IOException {
        scale(srcImg, getImageOutputStream(out), scale);
    }

    /**
     * 缩放图像（按比例缩放）<br>
     * 缩放后默认为jpeg格式，此方法并不关闭流
     *
     * @param srcImg          源图像来源流
     * @param destImageStream 缩放后的图像写出到的流
     * @param scale           缩放比例。比例大于1时为放大，小于1大于0为缩小
     * @throws IOException IO异常
     * @since 3.1.0
     */
    public static void scale(Image srcImg, ImageOutputStream destImageStream, float scale) throws IOException {
        writeJpg(scale(srcImg, scale), destImageStream);
    }

    /**
     * 缩放图像（按比例缩放）
     *
     * @param srcImg 源图像来源流
     * @param scale  缩放比例。比例大于1时为放大，小于1大于0为缩小
     * @return {@link Image}
     * @since 3.1.0
     */
    public static Image scale(Image srcImg, float scale) {
        return Img.from(srcImg).scale(scale).getImg();
    }

    /**
     * 缩放图像（按长宽缩放）<br>
     * 注意：目标长宽与原图不成比例会变形
     *
     * @param srcImg 源图像来源流
     * @param width  目标宽度
     * @param height 目标高度
     * @return {@link Image}
     * @since 3.1.0
     */
    public static Image scale(Image srcImg, int width, int height) {
        return Img.from(srcImg).scale(width, height).getImg();
    }

    /**
     * 缩放图像（按高度和宽度缩放）<br>
     * 缩放后默认格式与源图片相同，无法识别原图片默认JPG
     *
     * @param srcImageFile  源图像文件地址
     * @param destImageFile 缩放后的图像地址
     * @param width         缩放后的宽度
     * @param height        缩放后的高度
     * @param fixedColor    补充的颜色，不补充为{@code null}
     * @throws IOException IO异常
     */
    public static void scale(File srcImageFile, File destImageFile, int width, int height, Color fixedColor) throws IOException {
        Img.from(srcImageFile)
                .setTargetImageType(FileUtils.getExtension(destImageFile))
                .scale(width, height, fixedColor)
                .write(destImageFile);
    }

    /**
     * 缩放图像（按高度和宽度缩放）<br>
     * 缩放后默认为jpeg格式，此方法并不关闭流
     *
     * @param srcStream  源图像流
     * @param destStream 缩放后的图像目标流
     * @param width      缩放后的宽度
     * @param height     缩放后的高度
     * @param fixedColor 比例不对时补充的颜色，不补充为{@code null}
     * @throws IOException IO异常
     */
    public static void scale(InputStream srcStream, OutputStream destStream, int width, int height, Color fixedColor) throws IOException {
        scale(read(srcStream), getImageOutputStream(destStream), width, height, fixedColor);
    }

    /**
     * 缩放图像（按高度和宽度缩放）<br>
     * 缩放后默认为jpeg格式，此方法并不关闭流
     *
     * @param srcStream  源图像流
     * @param destStream 缩放后的图像目标流
     * @param width      缩放后的宽度
     * @param height     缩放后的高度
     * @param fixedColor 比例不对时补充的颜色，不补充为{@code null}
     * @throws IOException IO异常
     */
    public static void scale(ImageInputStream srcStream, ImageOutputStream destStream, int width, int height, Color fixedColor) throws IOException {
        scale(read(srcStream), destStream, width, height, fixedColor);
    }

    /**
     * 缩放图像（按高度和宽度缩放）<br>
     * 缩放后默认为jpeg格式，此方法并不关闭流
     *
     * @param srcImage        源图像
     * @param destImageStream 缩放后的图像目标流
     * @param width           缩放后的宽度
     * @param height          缩放后的高度
     * @param fixedColor      比例不对时补充的颜色，不补充为{@code null}
     * @throws IOException IO异常
     */
    public static void scale(Image srcImage, ImageOutputStream destImageStream, int width, int height, Color fixedColor) throws IOException {
        writeJpg(scale(srcImage, width, height, fixedColor), destImageStream);
    }

    /**
     * 缩放图像（按高度和宽度缩放）<br>
     * 缩放后默认为jpeg格式
     *
     * @param srcImage   源图像
     * @param width      缩放后的宽度
     * @param height     缩放后的高度
     * @param fixedColor 比例不对时补充的颜色，不补充为{@code null}
     * @return {@link Image}
     */
    public static Image scale(Image srcImage, int width, int height, Color fixedColor) {
        return Img.from(srcImage).scale(width, height, fixedColor).getImg();
    }

    // ---------------------------------------------------------------------------------------------------------------------- cut

    /**
     * 图像切割(按指定起点坐标和宽高切割)
     *
     * @param srcImgFile  源图像文件
     * @param destImgFile 切片后的图像文件
     * @param rectangle   矩形对象，表示矩形区域的x，y，width，height
     * @since 3.1.0
     */
    public static void cut(File srcImgFile, File destImgFile, Rectangle rectangle) throws IOException {
        cut(read(srcImgFile), destImgFile, rectangle);
    }

    /**
     * 图像切割(按指定起点坐标和宽高切割)，此方法并不关闭流
     *
     * @param srcStream  源图像流
     * @param destStream 切片后的图像输出流
     * @param rectangle  矩形对象，表示矩形区域的x，y，width，height
     * @since 3.1.0
     */
    public static void cut(InputStream srcStream, OutputStream destStream, Rectangle rectangle) throws IOException {
        cut(read(srcStream), destStream, rectangle);
    }

    /**
     * 图像切割(按指定起点坐标和宽高切割)，此方法并不关闭流
     *
     * @param srcStream  源图像流
     * @param destStream 切片后的图像输出流
     * @param rectangle  矩形对象，表示矩形区域的x，y，width，height
     * @since 3.1.0
     */
    public static void cut(ImageInputStream srcStream, ImageOutputStream destStream, Rectangle rectangle) throws IOException {
        cut(read(srcStream), destStream, rectangle);
    }

    /**
     * 图像切割(按指定起点坐标和宽高切割)，此方法并不关闭流
     *
     * @param srcImage  源图像
     * @param destFile  输出的文件
     * @param rectangle 矩形对象，表示矩形区域的x，y，width，height
     * @throws IOException IO异常
     * @since 3.2.2
     */
    public static void cut(Image srcImage, File destFile, Rectangle rectangle) throws IOException {
        write(cut(srcImage, rectangle), destFile);
    }

    /**
     * 图像切割(按指定起点坐标和宽高切割)，此方法并不关闭流
     *
     * @param srcImage  源图像
     * @param out       切片后的图像输出流
     * @param rectangle 矩形对象，表示矩形区域的x，y，width，height
     * @throws IOException IO异常
     * @since 3.1.0
     */
    public static void cut(Image srcImage, OutputStream out, Rectangle rectangle) throws IOException {
        cut(srcImage, getImageOutputStream(out), rectangle);
    }

    /**
     * 图像切割(按指定起点坐标和宽高切割)，此方法并不关闭流
     *
     * @param srcImage        源图像
     * @param destImageStream 切片后的图像输出流
     * @param rectangle       矩形对象，表示矩形区域的x，y，width，height
     * @throws IOException IO异常
     * @since 3.1.0
     */
    public static void cut(Image srcImage, ImageOutputStream destImageStream, Rectangle rectangle) throws IOException {
        writeJpg(cut(srcImage, rectangle), destImageStream);
    }

    /**
     * 图像切割(按指定起点坐标和宽高切割)
     *
     * @param srcImage  源图像
     * @param rectangle 矩形对象，表示矩形区域的x，y，width，height
     * @return {@link BufferedImage}
     * @since 3.1.0
     */
    public static Image cut(Image srcImage, Rectangle rectangle) {
        return Img.from(srcImage).setPositionBaseCentre(false).cut(rectangle).getImg();
    }

    /**
     * 图像切割(按指定起点坐标和宽高切割)，填充满整个图片（直径取长宽最小值）
     *
     * @param srcImage 源图像
     * @param x        原图的x坐标起始位置
     * @param y        原图的y坐标起始位置
     * @return {@link Image}
     * @since 4.1.15
     */
    public static Image cut(Image srcImage, int x, int y) {
        return cut(srcImage, x, y, -1);
    }

    /**
     * 图像切割(按指定起点坐标和宽高切割)
     *
     * @param srcImage 源图像
     * @param x        原图的x坐标起始位置
     * @param y        原图的y坐标起始位置
     * @param radius   半径，小于0表示填充满整个图片（直径取长宽最小值）
     * @return {@link Image}
     * @since 4.1.15
     */
    public static Image cut(Image srcImage, int x, int y, int radius) {
        return Img.from(srcImage).cut(x, y, radius).getImg();
    }

    /**
     * 图像切片（指定切片的宽度和高度）
     *
     * @param srcImageFile 源图像
     * @param descDir      切片目标文件夹
     * @param destWidth    目标切片宽度。默认200
     * @param destHeight   目标切片高度。默认150
     */
    public static void slice(File srcImageFile, File descDir, int destWidth, int destHeight) throws IOException {
        slice(read(srcImageFile), descDir, destWidth, destHeight);
    }

    /**
     * 图像切片（指定切片的宽度和高度）
     *
     * @param srcImage   源图像
     * @param descDir    切片目标文件夹
     * @param destWidth  目标切片宽度。默认200
     * @param destHeight 目标切片高度。默认150
     */
    public static void slice(Image srcImage, File descDir, int destWidth, int destHeight) throws IOException {
        if (destWidth <= 0) {
            destWidth = 200;
        }
        if (destHeight <= 0) {
            destHeight = 150;
        }
        int srcWidth = srcImage.getWidth(null);
        int srcHeight = srcImage.getHeight(null);

        if (srcWidth < destWidth) {
            destWidth = srcWidth;
        }
        if (srcHeight < destHeight) {
            destHeight = srcHeight;
        }

        int cols;
        int rows;
        // 计算切片的横向和纵向数量
        if (srcWidth % destWidth == 0) {
            cols = srcWidth / destWidth;
        } else {
            cols = (int) Math.floor((double) srcWidth / destWidth) + 1;
        }
        if (srcHeight % destHeight == 0) {
            rows = srcHeight / destHeight;
        } else {
            rows = (int) Math.floor((double) srcHeight / destHeight) + 1;
        }
        // 循环建立切片
        Image tag;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // 四个参数分别为图像起点坐标和宽高
                // 即: CropImageFilter(int x,int y,int width,int height)
                tag = cut(srcImage, new Rectangle(j * destWidth, i * destHeight, destWidth, destHeight));
                write(tag, FileUtils.file(descDir, "_r" + i + "_c" + j + ".jpg"));
            }
        }
    }

    /**
     * 图像切割（指定切片的行数和列数）
     *
     * @param srcImageFile 源图像文件
     * @param destDir      切片目标文件夹
     * @param rows         目标切片行数。默认2，必须是范围 [1, 20] 之内
     * @param cols         目标切片列数。默认2，必须是范围 [1, 20] 之内
     */
    public static void sliceByRowsAndCols(File srcImageFile, File destDir, int rows, int cols) throws IOException {
        try {
            sliceByRowsAndCols(ImageIO.read(srcImageFile), destDir, rows, cols);
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    /**
     * 图像切割（指定切片的行数和列数），默认RGB模式
     *
     * @param srcImage 源图像，如果非{@link BufferedImage}，则默认使用RGB模式
     * @param destDir  切片目标文件夹
     * @param rows     目标切片行数。默认2，必须是范围 [1, 20] 之内
     * @param cols     目标切片列数。默认2，必须是范围 [1, 20] 之内
     */
    public static void sliceByRowsAndCols(Image srcImage, File destDir, int rows, int cols) throws IOException {
        if (!destDir.exists()) {
            FileUtils.mkdir(destDir);
        } else if (!destDir.isDirectory()) {
            throw new IllegalArgumentException("Destination Dir must be a Directory !");
        }

        try {
            if (rows <= 0 || rows > 20) {
                rows = 2;
            }
            if (cols <= 0 || cols > 20) {
                cols = 2;
            }
            // 读取源图像
            final BufferedImage bi = toBufferedImage(srcImage);
            int srcWidth = bi.getWidth();
            int srcHeight = bi.getHeight();

            int destWidth = NumberUtils.partValue(srcWidth, cols);
            int destHeight = NumberUtils.partValue(srcHeight, rows);

            // 循环建立切片
            Image tag;
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    tag = cut(bi, new Rectangle(j * destWidth, i * destHeight, destWidth, destHeight));
                    ImageIO.write(toRenderedImage(tag), IMAGE_TYPE_JPEG, new File(destDir, "_r" + i + "_c" + j + ".jpg"));
                }
            }
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    // ---------------------------------------------------------------------------------------------------------------------- convert

    /**
     * 图像类型转换：GIF=》JPG、GIF=》PNG、PNG=》JPG、PNG=》GIF(X)、BMP=》PNG
     *
     * @param srcImageFile  源图像文件
     * @param destImageFile 目标图像文件
     */
    public static void convert(File srcImageFile, File destImageFile) throws IOException {
        final String srcExtName = FileUtils.getExtension(srcImageFile);
        final String destExtName = FileUtils.getExtension(destImageFile);
        if (StringUtils.equalsIgnoreCase(srcExtName, destExtName)) {
            // 扩展名相同直接复制文件
            FileUtils.copy(srcImageFile, destImageFile, true);
        }

        ImageOutputStream imageOutputStream = null;
        try {
            imageOutputStream = getImageOutputStream(destImageFile);
            convert(read(srcImageFile), destExtName, imageOutputStream, StringUtils.equalsIgnoreCase(IMAGE_TYPE_PNG, srcExtName));
        } finally {
            IoUtils.closeQuietly(imageOutputStream);
        }
    }

    /**
     * 图像类型转换：GIF=》JPG、GIF=》PNG、PNG=》JPG、PNG=》GIF(X)、BMP=》PNG<br>
     * 此方法并不关闭流
     *
     * @param srcStream  源图像流
     * @param formatName 包含格式非正式名称的 String：如JPG、JPEG、GIF等
     * @param destStream 目标图像输出流
     * @since 3.0.9
     */
    public static void convert(InputStream srcStream, String formatName, OutputStream destStream) throws IOException {
        write(read(srcStream), formatName, getImageOutputStream(destStream));
    }

    /**
     * 图像类型转换：GIF=》JPG、GIF=》PNG、PNG=》JPG、PNG=》GIF(X)、BMP=》PNG<br>
     * 此方法并不关闭流
     *
     * @param srcImage        源图像流
     * @param formatName      包含格式非正式名称的 String：如JPG、JPEG、GIF等
     * @param destImageStream 目标图像输出流
     * @param isSrcPng        源图片是否为PNG格式
     * @since 4.1.14
     */
    public static void convert(Image srcImage, String formatName, ImageOutputStream destImageStream, boolean isSrcPng) {
        try {
            ImageIO.write(isSrcPng ? copyImage(srcImage, BufferedImage.TYPE_INT_RGB) : toBufferedImage(srcImage), formatName, destImageStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ---------------------------------------------------------------------------------------------------------------------- grey

    /**
     * 彩色转为黑白
     *
     * @param srcImageFile  源图像地址
     * @param destImageFile 目标图像地址
     */
    public static void gray(File srcImageFile, File destImageFile) throws IOException {
        gray(read(srcImageFile), destImageFile);
    }

    /**
     * 彩色转为黑白<br>
     * 此方法并不关闭流
     *
     * @param srcStream  源图像流
     * @param destStream 目标图像流
     * @since 3.0.9
     */
    public static void gray(InputStream srcStream, OutputStream destStream) throws IOException {
        gray(read(srcStream), getImageOutputStream(destStream));
    }

    /**
     * 彩色转为黑白<br>
     * 此方法并不关闭流
     *
     * @param srcStream  源图像流
     * @param destStream 目标图像流
     * @since 3.0.9
     */
    public static void gray(ImageInputStream srcStream, ImageOutputStream destStream) throws IOException {
        gray(read(srcStream), destStream);
    }

    /**
     * 彩色转为黑白
     *
     * @param srcImage 源图像流
     * @param outFile  目标文件
     * @since 3.2.2
     */
    public static void gray(Image srcImage, File outFile) throws IOException {
        write(gray(srcImage), outFile);
    }

    /**
     * 彩色转为黑白<br>
     * 此方法并不关闭流
     *
     * @param srcImage 源图像流
     * @param out      目标图像流
     * @since 3.2.2
     */
    public static void gray(Image srcImage, OutputStream out) throws IOException {
        gray(srcImage, getImageOutputStream(out));
    }

    /**
     * 彩色转为黑白<br>
     * 此方法并不关闭流
     *
     * @param srcImage        源图像流
     * @param destImageStream 目标图像流
     * @throws IOException IO异常
     * @since 3.0.9
     */
    public static void gray(Image srcImage, ImageOutputStream destImageStream) throws IOException {
        writeJpg(gray(srcImage), destImageStream);
    }

    /**
     * 彩色转为黑白
     *
     * @param srcImage 源图像流
     * @return {@link Image}灰度后的图片
     * @since 3.1.0
     */
    public static Image gray(Image srcImage) {
        return Img.from(srcImage).gray().getImg();
    }

    // ---------------------------------------------------------------------------------------------------------------------- binary

    /**
     * 彩色转为黑白二值化图片，根据目标文件扩展名确定转换后的格式
     *
     * @param srcImageFile  源图像地址
     * @param destImageFile 目标图像地址
     */
    public static void binary(File srcImageFile, File destImageFile) throws IOException {
        binary(read(srcImageFile), destImageFile);
    }

    /**
     * 彩色转为黑白二值化图片<br>
     * 此方法并不关闭流
     *
     * @param srcStream  源图像流
     * @param destStream 目标图像流
     * @param imageType  图片格式(扩展名)
     * @since 4.0.5
     */
    public static void binary(InputStream srcStream, OutputStream destStream, String imageType) throws IOException {
        binary(read(srcStream), getImageOutputStream(destStream), imageType);
    }

    /**
     * 彩色转为黑白黑白二值化图片<br>
     * 此方法并不关闭流
     *
     * @param srcStream  源图像流
     * @param destStream 目标图像流
     * @param imageType  图片格式(扩展名)
     * @since 4.0.5
     */
    public static void binary(ImageInputStream srcStream, ImageOutputStream destStream, String imageType) throws IOException {
        binary(read(srcStream), destStream, imageType);
    }

    /**
     * 彩色转为黑白二值化图片，根据目标文件扩展名确定转换后的格式
     *
     * @param srcImage 源图像流
     * @param outFile  目标文件
     * @since 4.0.5
     */
    public static void binary(Image srcImage, File outFile) throws IOException {
        write(binary(srcImage), outFile);
    }

    /**
     * 彩色转为黑白二值化图片<br>
     * 此方法并不关闭流，输出JPG格式
     *
     * @param srcImage  源图像流
     * @param out       目标图像流
     * @param imageType 图片格式(扩展名)
     * @since 4.0.5
     */
    public static void binary(Image srcImage, OutputStream out, String imageType) throws IOException {
        binary(srcImage, getImageOutputStream(out), imageType);
    }

    /**
     * 彩色转为黑白二值化图片<br>
     * 此方法并不关闭流，输出JPG格式
     *
     * @param srcImage        源图像流
     * @param destImageStream 目标图像流
     * @param imageType       图片格式(扩展名)
     * @throws IOException IO异常
     * @since 4.0.5
     */
    public static void binary(Image srcImage, ImageOutputStream destImageStream, String imageType) throws IOException {
        write(binary(srcImage), imageType, destImageStream);
    }

    /**
     * 彩色转为黑白二值化图片
     *
     * @param srcImage 源图像流
     * @return {@link Image}二值化后的图片
     * @since 4.0.5
     */
    public static Image binary(Image srcImage) {
        return Img.from(srcImage).binary().getImg();
    }

    // ---------------------------------------------------------------------------------------------------------------------- press

    /**
     * 给图片添加文字水印
     *
     * @param imageFile 源图像文件
     * @param destFile  目标图像文件
     * @param pressText 水印文字
     * @param color     水印的字体颜色
     * @param font      {@link Font} 字体相关信息，如果默认则为{@code null}
     * @param x         修正值。 默认在中间，偏移量相对于中间偏移
     * @param y         修正值。 默认在中间，偏移量相对于中间偏移
     * @param alpha     透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     */
    public static void pressText(File imageFile, File destFile, String pressText, Color color, Font font, int x, int y, float alpha) throws IOException {
        pressText(read(imageFile), destFile, pressText, color, font, x, y, alpha);
    }

    /**
     * 给图片添加文字水印<br>
     * 此方法并不关闭流
     *
     * @param srcStream  源图像流
     * @param destStream 目标图像流
     * @param pressText  水印文字
     * @param color      水印的字体颜色
     * @param font       {@link Font} 字体相关信息，如果默认则为{@code null}
     * @param x          修正值。 默认在中间，偏移量相对于中间偏移
     * @param y          修正值。 默认在中间，偏移量相对于中间偏移
     * @param alpha      透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     */
    public static void pressText(InputStream srcStream, OutputStream destStream, String pressText, Color color, Font font, int x, int y, float alpha) throws IOException {
        pressText(read(srcStream), getImageOutputStream(destStream), pressText, color, font, x, y, alpha);
    }

    /**
     * 给图片添加文字水印<br>
     * 此方法并不关闭流
     *
     * @param srcStream  源图像流
     * @param destStream 目标图像流
     * @param pressText  水印文字
     * @param color      水印的字体颜色
     * @param font       {@link Font} 字体相关信息，如果默认则为{@code null}
     * @param x          修正值。 默认在中间，偏移量相对于中间偏移
     * @param y          修正值。 默认在中间，偏移量相对于中间偏移
     * @param alpha      透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     */
    public static void pressText(ImageInputStream srcStream, ImageOutputStream destStream, String pressText, Color color, Font font, int x, int y, float alpha) throws IOException {
        pressText(read(srcStream), destStream, pressText, color, font, x, y, alpha);
    }

    /**
     * 给图片添加文字水印<br>
     * 此方法并不关闭流
     *
     * @param srcImage  源图像
     * @param destFile  目标流
     * @param pressText 水印文字
     * @param color     水印的字体颜色
     * @param font      {@link Font} 字体相关信息，如果默认则为{@code null}
     * @param x         修正值。 默认在中间，偏移量相对于中间偏移
     * @param y         修正值。 默认在中间，偏移量相对于中间偏移
     * @param alpha     透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     * @throws IOException IO异常
     * @since 3.2.2
     */
    public static void pressText(Image srcImage, File destFile, String pressText, Color color, Font font, int x, int y, float alpha) throws IOException {
        write(pressText(srcImage, pressText, color, font, x, y, alpha), destFile);
    }

    /**
     * 给图片添加文字水印<br>
     * 此方法并不关闭流
     *
     * @param srcImage  源图像
     * @param to        目标流
     * @param pressText 水印文字
     * @param color     水印的字体颜色
     * @param font      {@link Font} 字体相关信息，如果默认则为{@code null}
     * @param x         修正值。 默认在中间，偏移量相对于中间偏移
     * @param y         修正值。 默认在中间，偏移量相对于中间偏移
     * @param alpha     透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     * @throws IOException IO异常
     * @since 3.2.2
     */
    public static void pressText(Image srcImage, OutputStream to, String pressText, Color color, Font font, int x, int y, float alpha) throws IOException {
        pressText(srcImage, getImageOutputStream(to), pressText, color, font, x, y, alpha);
    }

    /**
     * 给图片添加文字水印<br>
     * 此方法并不关闭流
     *
     * @param srcImage        源图像
     * @param destImageStream 目标图像流
     * @param pressText       水印文字
     * @param color           水印的字体颜色
     * @param font            {@link Font} 字体相关信息，如果默认则为{@code null}
     * @param x               修正值。 默认在中间，偏移量相对于中间偏移
     * @param y               修正值。 默认在中间，偏移量相对于中间偏移
     * @param alpha           透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     * @throws IOException IO异常
     */
    public static void pressText(Image srcImage, ImageOutputStream destImageStream, String pressText, Color color, Font font, int x, int y, float alpha) throws IOException {
        writeJpg(pressText(srcImage, pressText, color, font, x, y, alpha), destImageStream);
    }

    /**
     * 给图片添加文字水印<br>
     * 此方法并不关闭流
     *
     * @param srcImage  源图像
     * @param pressText 水印文字
     * @param color     水印的字体颜色
     * @param font      {@link Font} 字体相关信息，如果默认则为{@code null}
     * @param x         修正值。 默认在中间，偏移量相对于中间偏移
     * @param y         修正值。 默认在中间，偏移量相对于中间偏移
     * @param alpha     透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     * @return 处理后的图像
     * @since 3.2.2
     */
    public static Image pressText(Image srcImage, String pressText, Color color, Font font, int x, int y, float alpha) {
        return Img.from(srcImage).pressText(pressText, color, font, x, y, alpha).getImg();
    }

    /**
     * 给图片添加图片水印
     *
     * @param srcImageFile  源图像文件
     * @param destImageFile 目标图像文件
     * @param pressImg      水印图片
     * @param x             修正值。 默认在中间，偏移量相对于中间偏移
     * @param y             修正值。 默认在中间，偏移量相对于中间偏移
     * @param alpha         透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     */
    public static void pressImage(File srcImageFile, File destImageFile, Image pressImg, int x, int y, float alpha) throws IOException {
        pressImage(read(srcImageFile), destImageFile, pressImg, x, y, alpha);
    }

    /**
     * 给图片添加图片水印<br>
     * 此方法并不关闭流
     *
     * @param srcStream  源图像流
     * @param destStream 目标图像流
     * @param pressImg   水印图片，可以使用{@link ImageIO#read(File)}方法读取文件
     * @param x          修正值。 默认在中间，偏移量相对于中间偏移
     * @param y          修正值。 默认在中间，偏移量相对于中间偏移
     * @param alpha      透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     */
    public static void pressImage(InputStream srcStream, OutputStream destStream, Image pressImg, int x, int y, float alpha) throws IOException {
        pressImage(read(srcStream), getImageOutputStream(destStream), pressImg, x, y, alpha);
    }

    /**
     * 给图片添加图片水印<br>
     * 此方法并不关闭流
     *
     * @param srcStream  源图像流
     * @param destStream 目标图像流
     * @param pressImg   水印图片，可以使用{@link ImageIO#read(File)}方法读取文件
     * @param x          修正值。 默认在中间，偏移量相对于中间偏移
     * @param y          修正值。 默认在中间，偏移量相对于中间偏移
     * @param alpha      透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     * @throws IOException IO异常
     */
    public static void pressImage(ImageInputStream srcStream, ImageOutputStream destStream, Image pressImg, int x, int y, float alpha) throws IOException {
        pressImage(read(srcStream), destStream, pressImg, x, y, alpha);
    }

    /**
     * 给图片添加图片水印<br>
     * 此方法并不关闭流
     *
     * @param srcImage 源图像流
     * @param outFile  写出文件
     * @param pressImg 水印图片，可以使用{@link ImageIO#read(File)}方法读取文件
     * @param x        修正值。 默认在中间，偏移量相对于中间偏移
     * @param y        修正值。 默认在中间，偏移量相对于中间偏移
     * @param alpha    透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     * @throws IOException IO异常
     * @since 3.2.2
     */
    public static void pressImage(Image srcImage, File outFile, Image pressImg, int x, int y, float alpha) throws IOException {
        write(pressImage(srcImage, pressImg, x, y, alpha), outFile);
    }

    /**
     * 给图片添加图片水印<br>
     * 此方法并不关闭流
     *
     * @param srcImage 源图像流
     * @param out      目标图像流
     * @param pressImg 水印图片，可以使用{@link ImageIO#read(File)}方法读取文件
     * @param x        修正值。 默认在中间，偏移量相对于中间偏移
     * @param y        修正值。 默认在中间，偏移量相对于中间偏移
     * @param alpha    透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     * @throws IOException IO异常
     * @since 3.2.2
     */
    public static void pressImage(Image srcImage, OutputStream out, Image pressImg, int x, int y, float alpha) throws IOException {
        pressImage(srcImage, getImageOutputStream(out), pressImg, x, y, alpha);
    }

    /**
     * 给图片添加图片水印<br>
     * 此方法并不关闭流
     *
     * @param srcImage        源图像流
     * @param destImageStream 目标图像流
     * @param pressImg        水印图片，可以使用{@link ImageIO#read(File)}方法读取文件
     * @param x               修正值。 默认在中间，偏移量相对于中间偏移
     * @param y               修正值。 默认在中间，偏移量相对于中间偏移
     * @param alpha           透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     * @throws IOException IO异常
     */
    public static void pressImage(Image srcImage, ImageOutputStream destImageStream, Image pressImg, int x, int y, float alpha) throws IOException {
        writeJpg(pressImage(srcImage, pressImg, x, y, alpha), destImageStream);
    }

    /**
     * 给图片添加图片水印<br>
     * 此方法并不关闭流
     *
     * @param srcImage 源图像流
     * @param pressImg 水印图片，可以使用{@link ImageIO#read(File)}方法读取文件
     * @param x        修正值。 默认在中间，偏移量相对于中间偏移
     * @param y        修正值。 默认在中间，偏移量相对于中间偏移
     * @param alpha    透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     * @return 结果图片
     */
    public static Image pressImage(Image srcImage, Image pressImg, int x, int y, float alpha) {
        return Img.from(srcImage).pressImage(pressImg, x, y, alpha).getImg();
    }

    /**
     * 给图片添加图片水印<br>
     * 此方法并不关闭流
     *
     * @param srcImage  源图像流
     * @param pressImg  水印图片，可以使用{@link ImageIO#read(File)}方法读取文件
     * @param rectangle 矩形对象，表示矩形区域的x，y，width，height，x,y从背景图片中心计算
     * @param alpha     透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     * @return 结果图片
     * @since 4.1.14
     */
    public static Image pressImage(Image srcImage, Image pressImg, Rectangle rectangle, float alpha) {
        return Img.from(srcImage).pressImage(pressImg, rectangle, alpha).getImg();
    }

    // ---------------------------------------------------------------------------------------------------------------------- rotate

    /**
     * 旋转图片为指定角度<br>
     * 此方法不会关闭输出流
     *
     * @param imageFile 被旋转图像文件
     * @param degree    旋转角度
     * @param outFile   输出文件
     * @throws IOException IO异常
     * @since 3.2.2
     */
    public static void rotate(File imageFile, int degree, File outFile) throws IOException {
        rotate(read(imageFile), degree, outFile);
    }

    /**
     * 旋转图片为指定角度<br>
     * 此方法不会关闭输出流
     *
     * @param image   目标图像
     * @param degree  旋转角度
     * @param outFile 输出文件
     * @throws IOException IO异常
     * @since 3.2.2
     */
    public static void rotate(Image image, int degree, File outFile) throws IOException {
        write(rotate(image, degree), outFile);
    }

    /**
     * 旋转图片为指定角度<br>
     * 此方法不会关闭输出流
     *
     * @param image  目标图像
     * @param degree 旋转角度
     * @param out    输出流
     * @throws IOException IO异常
     * @since 3.2.2
     */
    public static void rotate(Image image, int degree, OutputStream out) throws IOException {
        writeJpg(rotate(image, degree), getImageOutputStream(out));
    }

    /**
     * 旋转图片为指定角度<br>
     * 此方法不会关闭输出流，输出格式为JPG
     *
     * @param image  目标图像
     * @param degree 旋转角度
     * @param out    输出图像流
     * @throws IOException IO异常
     * @since 3.2.2
     */
    public static void rotate(Image image, int degree, ImageOutputStream out) throws IOException {
        writeJpg(rotate(image, degree), out);
    }

    /**
     * 旋转图片为指定角度<br>
     * 来自：http://blog.51cto.com/cping1982/130066
     *
     * @param image  目标图像
     * @param degree 旋转角度
     * @return 旋转后的图片
     * @since 3.2.2
     */
    public static Image rotate(Image image, int degree) {
        return Img.from(image).rotate(degree).getImg();
    }

    // ---------------------------------------------------------------------------------------------------------------------- flip

    /**
     * 水平翻转图像
     *
     * @param imageFile 图像文件
     * @param outFile   输出文件
     * @throws IOException IO异常
     * @since 3.2.2
     */
    public static void flip(File imageFile, File outFile) throws IOException {
        flip(read(imageFile), outFile);
    }

    /**
     * 水平翻转图像
     *
     * @param image   图像
     * @param outFile 输出文件
     * @throws IOException IO异常
     * @since 3.2.2
     */
    public static void flip(Image image, File outFile) throws IOException {
        write(flip(image), outFile);
    }

    /**
     * 水平翻转图像
     *
     * @param image 图像
     * @param out   输出
     * @throws IOException IO异常
     * @since 3.2.2
     */
    public static void flip(Image image, OutputStream out) throws IOException {
        flip(image, getImageOutputStream(out));
    }

    /**
     * 水平翻转图像，写出格式为JPG
     *
     * @param image 图像
     * @param out   输出
     * @throws IOException IO异常
     * @since 3.2.2
     */
    public static void flip(Image image, ImageOutputStream out) throws IOException {
        writeJpg(flip(image), out);
    }

    /**
     * 水平翻转图像
     *
     * @param image 图像
     * @return 翻转后的图片
     * @since 3.2.2
     */
    public static Image flip(Image image) {
        return Img.from(image).flip().getImg();
    }

    // ---------------------------------------------------------------------------------------------------------------------- compress

    /**
     * 压缩图像，输出图像只支持jpg文件
     *
     * @param imageFile 图像文件
     * @param outFile   输出文件，只支持jpg文件
     * @param quality   压缩比例，必须为0~1
     * @throws IOException IO异常
     * @since 4.3.2
     */
    public static void compress(File imageFile, File outFile, float quality) throws IOException {
        Img.from(imageFile).setQuality(quality).write(outFile);
    }

    // ---------------------------------------------------------------------------------------------------------------------- other

    /**
     * {@link Image} 转 {@link RenderedImage}<br>
     * 首先尝试强转，否则新建一个{@link BufferedImage}后重新绘制，使用 {@link BufferedImage#TYPE_INT_RGB} 模式。
     *
     * @param img {@link Image}
     * @return {@link BufferedImage}
     * @since 4.3.2
     */
    public static RenderedImage toRenderedImage(Image img) {
        if (img instanceof RenderedImage) {
            return (RenderedImage) img;
        }

        return copyImage(img, BufferedImage.TYPE_INT_RGB);
    }

    /**
     * {@link Image} 转 {@link BufferedImage}<br>
     * 首先尝试强转，否则新建一个{@link BufferedImage}后重新绘制，使用 {@link BufferedImage#TYPE_INT_RGB} 模式
     *
     * @param img {@link Image}
     * @return {@link BufferedImage}
     */
    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        return copyImage(img, BufferedImage.TYPE_INT_RGB);
    }

    /**
     * {@link Image} 转 {@link BufferedImage}<br>
     * 如果源图片的RGB模式与目标模式一致，则直接转换，否则重新绘制<br>
     * 默认的，png图片使用 {@link BufferedImage#TYPE_INT_ARGB}模式，其它使用 {@link BufferedImage#TYPE_INT_RGB} 模式
     *
     * @param image     {@link Image}
     * @param imageType 目标图片类型，例如jpg或png等
     * @return {@link BufferedImage}
     * @since 4.3.2
     */
    public static BufferedImage toBufferedImage(Image image, String imageType) {
        final int type = IMAGE_TYPE_PNG.equalsIgnoreCase(imageType)
                ? BufferedImage.TYPE_INT_ARGB
                : BufferedImage.TYPE_INT_RGB;
        return toBufferedImage(image, type);
    }

    /**
     * {@link Image} 转 {@link BufferedImage}<br>
     * 如果源图片的RGB模式与目标模式一致，则直接转换，否则重新绘制
     *
     * @param image     {@link Image}
     * @param imageType 目标图片类型，{@link BufferedImage}中的常量，例如黑白等
     * @return {@link BufferedImage}
     * @since 5.4.7
     */
    public static BufferedImage toBufferedImage(Image image, int imageType) {
        BufferedImage bufferedImage;
        if (image instanceof BufferedImage) {
            bufferedImage = (BufferedImage) image;
            if (imageType != bufferedImage.getType()) {
                bufferedImage = copyImage(image, imageType);
            }
            return bufferedImage;
        }

        bufferedImage = copyImage(image, imageType);
        return bufferedImage;
    }

    /**
     * 将已有Image复制新的一份出来
     *
     * @param img       {@link Image}
     * @param imageType 目标图片类型，{@link BufferedImage}中的常量，例如黑白等
     * @return {@link BufferedImage}
     * @see BufferedImage#TYPE_INT_RGB
     * @see BufferedImage#TYPE_INT_ARGB
     * @see BufferedImage#TYPE_INT_ARGB_PRE
     * @see BufferedImage#TYPE_INT_BGR
     * @see BufferedImage#TYPE_3BYTE_BGR
     * @see BufferedImage#TYPE_4BYTE_ABGR
     * @see BufferedImage#TYPE_4BYTE_ABGR_PRE
     * @see BufferedImage#TYPE_BYTE_GRAY
     * @see BufferedImage#TYPE_USHORT_GRAY
     * @see BufferedImage#TYPE_BYTE_BINARY
     * @see BufferedImage#TYPE_BYTE_INDEXED
     * @see BufferedImage#TYPE_USHORT_565_RGB
     * @see BufferedImage#TYPE_USHORT_555_RGB
     */
    public static BufferedImage copyImage(Image img, int imageType) {
        return copyImage(img, imageType, null);
    }

    /**
     * 将已有Image复制新的一份出来
     *
     * @param img             {@link Image}
     * @param imageType       目标图片类型，{@link BufferedImage}中的常量，例如黑白等
     * @param backgroundColor 背景色，{@code null} 表示默认背景色（黑色或者透明）
     * @return {@link BufferedImage}
     * @see BufferedImage#TYPE_INT_RGB
     * @see BufferedImage#TYPE_INT_ARGB
     * @see BufferedImage#TYPE_INT_ARGB_PRE
     * @see BufferedImage#TYPE_INT_BGR
     * @see BufferedImage#TYPE_3BYTE_BGR
     * @see BufferedImage#TYPE_4BYTE_ABGR
     * @see BufferedImage#TYPE_4BYTE_ABGR_PRE
     * @see BufferedImage#TYPE_BYTE_GRAY
     * @see BufferedImage#TYPE_USHORT_GRAY
     * @see BufferedImage#TYPE_BYTE_BINARY
     * @see BufferedImage#TYPE_BYTE_INDEXED
     * @see BufferedImage#TYPE_USHORT_565_RGB
     * @see BufferedImage#TYPE_USHORT_555_RGB
     * @since 4.5.17
     */
    public static BufferedImage copyImage(Image img, int imageType, Color backgroundColor) {
        img = new ImageIcon(img).getImage();

        final BufferedImage bimage = new BufferedImage(
                img.getWidth(null), img.getHeight(null), imageType);
        final Graphics2D bGr = GraphicsUtils.createGraphics(bimage, backgroundColor);
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        return bimage;
    }

    /**
     * 创建与当前设备颜色模式兼容的 {@link BufferedImage}
     *
     * @param width        宽度
     * @param height       高度
     * @param transparency 透明模式，见 {@link Transparency}
     * @return {@link BufferedImage}
     * @since 5.7.13
     */
    public static BufferedImage createCompatibleImage(int width, int height, int transparency) throws HeadlessException {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gs = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gs.getDefaultConfiguration();
        return gc.createCompatibleImage(width, height, transparency);
    }

    /**
     * 将Base64编码的图像信息转为 {@link BufferedImage}
     *
     * @param base64 图像的Base64表示
     * @return {@link BufferedImage}
     * @throws IOException IO异常
     */
    public static BufferedImage toImage(String base64) throws IOException {
        return toImage(CodecUtils.decodeBase64(base64));
    }

    /**
     * 将的图像bytes转为 {@link BufferedImage}
     *
     * @param imageBytes 图像bytes
     * @return {@link BufferedImage}
     * @throws IOException IO异常
     */
    public static BufferedImage toImage(byte[] imageBytes) throws IOException {
        return read(new ByteArrayInputStream(imageBytes));
    }

    /**
     * 将图片对象转换为InputStream形式
     *
     * @param image     图片对象
     * @param imageType 图片类型
     * @return Base64的字符串表现形式
     * @since 4.2.4
     */
    public static ByteArrayInputStream toStream(Image image, String imageType) throws IOException {
        return IoUtils.toStream(toBytes(image, imageType));
    }

    /**
     * 将图片对象转换为Base64的Data URI形式，格式为：data:image/[imageType];base64,[data]
     *
     * @param image     图片对象
     * @param imageType 图片类型
     * @return Base64的字符串表现形式
     * @since 5.3.6
     */
    public static String toBase64DataUri(Image image, String imageType) throws IOException {
        return UrlUtils.getDataUri(
                "image/" + imageType, "base64",
                toBase64(image, imageType));
    }

    /**
     * 将图片对象转换为Base64形式
     *
     * @param image     图片对象
     * @param imageType 图片类型
     * @return Base64的字符串表现形式
     * @since 4.1.8
     */
    public static String toBase64(Image image, String imageType) throws IOException {
        return CodecUtils.encodeBase64(toBytes(image, imageType));
    }

    /**
     * 将图片对象转换为bytes形式
     *
     * @param image     图片对象
     * @param imageType 图片类型
     * @return Base64的字符串表现形式
     * @since 5.2.4
     */
    public static byte[] toBytes(Image image, String imageType) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        write(image, imageType, out);
        return out.toByteArray();
    }

    /**
     * 根据文字创建PNG图片
     *
     * @param str             文字
     * @param font            字体{@link Font}
     * @param backgroundColor 背景颜色，默认透明
     * @param fontColor       字体颜色，默认黑色
     * @param out             图片输出地
     * @throws IOException IO异常
     */
    public static void createImage(String str, Font font, Color backgroundColor, Color fontColor, ImageOutputStream out) throws IOException {
        writePng(createImage(str, font, backgroundColor, fontColor, BufferedImage.TYPE_INT_ARGB), out);
    }

    /**
     * Cretae a BufferedImage from an ImageProducer.
     *
     * @param producer the ImageProducer
     * @return a new TYPE_INT_ARGB BufferedImage
     */
    public static BufferedImage createImage(ImageProducer producer) {
        PixelGrabber pg = new PixelGrabber(producer, 0, 0, -1, -1, null, 0, 0);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
            throw new RuntimeException("Image fetch interrupted");
        }
        if ((pg.status() & ImageObserver.ABORT) != 0) {
            throw new RuntimeException("Image fetch aborted");
        }
        if ((pg.status() & ImageObserver.ERROR) != 0) {
            throw new RuntimeException("Image fetch error");
        }
        BufferedImage p = new BufferedImage(pg.getWidth(), pg.getHeight(), BufferedImage.TYPE_INT_ARGB);
        p.setRGB(0, 0, pg.getWidth(), pg.getHeight(), (int[]) pg.getPixels(), 0, pg.getWidth());
        return p;
    }

    /**
     * 根据文字创建图片
     *
     * @param str             文字
     * @param font            字体{@link Font}
     * @param backgroundColor 背景颜色，默认透明
     * @param fontColor       字体颜色，默认黑色
     * @param imageType       图片类型，见：{@link BufferedImage}
     * @return 图片
     * @throws IOException IO异常
     */
    public static BufferedImage createImage(String str, Font font, Color backgroundColor, Color fontColor, int imageType) throws IOException {
        // 获取font的样式应用在str上的整个矩形
        final Rectangle2D r = getRectangle(str, font);
        int unitHeight = (int) Math.floor(r.getHeight());
        int width = (int) Math.round(r.getWidth()) + 1;
        int height = unitHeight + 3;
        final BufferedImage image = new BufferedImage(width, height, imageType);
        final Graphics g = image.getGraphics();
        if (null != backgroundColor) {
            // 先用背景色填充整张图片,也就是背景
            g.setColor(backgroundColor);
            g.fillRect(0, 0, width, height);
        }
        g.setColor(ObjectUtils.defaultIfNull(fontColor, Color.BLACK));
        g.setFont(font);
        g.drawString(str, 0, font.getSize());
        g.dispose();

        return image;
    }

    /**
     * 获取font的样式应用在str上的整个矩形
     *
     * @param str  字符串，必须非空
     * @param font 字体，必须非空
     * @return {@link Rectangle2D}
     * @since 5.3.3
     */
    public static Rectangle2D getRectangle(String str, Font font) {
        return font.getStringBounds(str,
                new FontRenderContext(AffineTransform.getScaleInstance(1, 1),
                        false,
                        false));
    }

    /**
     * 根据文件创建字体<br>
     * 首先尝试创建{@link Font#TRUETYPE_FONT}字体，此类字体无效则创建{@link Font#TYPE1_FONT}
     *
     * @param fontFile 字体文件
     * @return {@link Font}
     * @since 3.0.9
     */
    public static Font createFont(File fontFile) {
        return FontUtils.createFont(fontFile);
    }

    /**
     * 根据文件创建字体<br>
     * 首先尝试创建{@link Font#TRUETYPE_FONT}字体，此类字体无效则创建{@link Font#TYPE1_FONT}
     *
     * @param fontStream 字体流
     * @return {@link Font}
     * @since 3.0.9
     */
    public static Font createFont(InputStream fontStream) {
        return FontUtils.createFont(fontStream);
    }

    /**
     * 创建{@link Graphics2D}
     *
     * @param image {@link BufferedImage}
     * @param color {@link Color}背景颜色以及当前画笔颜色
     * @return {@link Graphics2D}
     * @see GraphicsUtils#createGraphics(BufferedImage, Color)
     * @since 3.2.3
     */
    public static Graphics2D createGraphics(BufferedImage image, Color color) {
        return GraphicsUtils.createGraphics(image, color);
    }

    /**
     * 写出图像为JPG格式
     *
     * @param image           {@link Image}
     * @param destImageStream 写出到的目标流
     * @throws IOException IO异常
     */
    public static void writeJpg(Image image, ImageOutputStream destImageStream) throws IOException {
        write(image, IMAGE_TYPE_JPG, destImageStream);
    }

    /**
     * 写出图像为PNG格式
     *
     * @param image           {@link Image}
     * @param destImageStream 写出到的目标流
     * @throws IOException IO异常
     */
    public static void writePng(Image image, ImageOutputStream destImageStream) throws IOException {
        write(image, IMAGE_TYPE_PNG, destImageStream);
    }

    /**
     * 写出图像为JPG格式
     *
     * @param image {@link Image}
     * @param out   写出到的目标流
     * @throws IOException IO异常
     * @since 4.0.10
     */
    public static void writeJpg(Image image, OutputStream out) throws IOException {
        write(image, IMAGE_TYPE_JPG, out);
    }

    /**
     * 写出图像为PNG格式
     *
     * @param image {@link Image}
     * @param out   写出到的目标流
     * @throws IOException IO异常
     * @since 4.0.10
     */
    public static void writePng(Image image, OutputStream out) throws IOException {
        write(image, IMAGE_TYPE_PNG, out);
    }

    /**
     * 按照目标格式写出图像：GIF=》JPG、GIF=》PNG、PNG=》JPG、PNG=》GIF(X)、BMP=》PNG<br>
     * 此方法并不关闭流
     *
     * @param srcStream  源图像流
     * @param formatName 包含格式非正式名称的 String：如JPG、JPEG、GIF等
     * @param destStream 目标图像输出流
     * @since 5.0.0
     */
    public static void write(ImageInputStream srcStream, String formatName, ImageOutputStream destStream) throws IOException {
        write(read(srcStream), formatName, destStream);
    }

    /**
     * 写出图像：GIF=》JPG、GIF=》PNG、PNG=》JPG、PNG=》GIF(X)、BMP=》PNG<br>
     * 此方法并不关闭流
     *
     * @param image     {@link Image}
     * @param imageType 图片类型（图片扩展名）
     * @param out       写出到的目标流
     * @throws IOException IO异常
     * @since 3.1.2
     */
    public static void write(Image image, String imageType, OutputStream out) throws IOException {
        write(image, imageType, getImageOutputStream(out));
    }

    /**
     * 写出图像为指定格式：GIF=》JPG、GIF=》PNG、PNG=》JPG、PNG=》GIF(X)、BMP=》PNG<br>
     * 此方法并不关闭流
     *
     * @param image           {@link Image}
     * @param imageType       图片类型（图片扩展名）
     * @param destImageStream 写出到的目标流
     * @return 是否成功写出，如果返回false表示未找到合适的Writer
     * @throws IOException IO异常
     * @since 3.1.2
     */
    public static boolean write(Image image, String imageType, ImageOutputStream destImageStream) throws IOException {
        return write(image, imageType, destImageStream, 1);
    }

    /**
     * 写出图像为指定格式
     *
     * @param image           {@link Image}
     * @param imageType       图片类型（图片扩展名）
     * @param destImageStream 写出到的目标流
     * @param quality         质量，数字为0~1（不包括0和1）表示质量压缩比，除此数字外设置表示不压缩
     * @return 是否成功写出，如果返回false表示未找到合适的Writer
     * @throws IOException IO异常
     * @since 4.3.2
     */
    public static boolean write(Image image, String imageType, ImageOutputStream destImageStream, float quality) throws IOException {
        if (StringUtils.isEmpty(imageType)) {
            imageType = IMAGE_TYPE_JPG;
        }

        final BufferedImage bufferedImage = toBufferedImage(image, imageType);
        final ImageWriter writer = getWriter(bufferedImage, imageType);
        return write(bufferedImage, writer, destImageStream, quality);
    }

    /**
     * 写出图像为目标文件扩展名对应的格式
     *
     * @param image      {@link Image}
     * @param targetFile 目标文件
     * @throws IOException IO异常
     * @since 3.1.0
     */
    public static void write(Image image, File targetFile) throws IOException {
        FileUtils.touch(targetFile);
        ImageOutputStream out = null;
        try {
            out = getImageOutputStream(targetFile);
            write(image, FileUtils.getExtension(targetFile), out);
        } finally {
            IoUtils.closeQuietly(out);
        }
    }

    /**
     * 通过{@link ImageWriter}写出图片到输出流
     *
     * @param image   图片
     * @param writer  {@link ImageWriter}
     * @param output  输出的Image流{@link ImageOutputStream}
     * @param quality 质量，数字为0~1（不包括0和1）表示质量压缩比，除此数字外设置表示不压缩
     * @return 是否成功写出
     * @since 4.3.2
     */
    public static boolean write(Image image, ImageWriter writer, ImageOutputStream output, float quality) throws IOException {
        if (writer == null) {
            return false;
        }

        writer.setOutput(output);
        final RenderedImage renderedImage = toRenderedImage(image);
        ImageWriteParam imgWriteParams = null;
        if (quality > 0 && quality < 1) {
            imgWriteParams = writer.getDefaultWriteParam();
            if (imgWriteParams.canWriteCompressed()) {
                imgWriteParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                imgWriteParams.setCompressionQuality(quality);
                final ColorModel colorModel = renderedImage.getColorModel();
                imgWriteParams.setDestinationType(new ImageTypeSpecifier(colorModel, colorModel.createCompatibleSampleModel(16, 16)));
            }
        }

        try {
            if (null != imgWriteParams) {
                writer.write(null, new IIOImage(renderedImage, null, null), imgWriteParams);
            } else {
                writer.write(renderedImage);
            }
            output.flush();
        } catch (IOException e) {
            throw new IOException(e);
        } finally {
            writer.dispose();
        }
        return true;
    }

    /**
     * 获得{@link ImageReader}
     *
     * @param type 图片文件类型，例如 "jpeg" 或 "tiff"
     * @return {@link ImageReader}
     */
    public static ImageReader getReader(String type) {
        final Iterator<ImageReader> iterator = ImageIO.getImageReadersByFormatName(type);
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }

    /**
     * 从文件中读取图片，请使用绝对路径，使用相对路径会相对于ClassPath
     *
     * @param imageFilePath 图片文件路径
     * @return 图片
     * @since 4.1.15
     */
    public static BufferedImage read(String imageFilePath) throws IOException {
        return read(FileUtils.file(imageFilePath));
    }

    /**
     * 从文件中读取图片
     *
     * @param imageFile 图片文件
     * @return 图片
     * @since 3.2.2
     */
    public static BufferedImage read(File imageFile) throws IOException {
        BufferedImage result;
        try {
            result = ImageIO.read(imageFile);
        } catch (IOException e) {
            throw new IOException(e);
        }

        if (null == result) {
            throw new IllegalArgumentException("Image type of file [" + imageFile.getName() + "] is not supported!");
        }

        return result;
    }

    /**
     * 从URL中获取或读取图片对象
     *
     * @param url URL
     * @return {@link Image}
     * @since 5.5.8
     */
    public static Image getImage(URL url) {
        return Toolkit.getDefaultToolkit().getImage(url);
    }

    /**
     * 从{@link Resource}中读取图片
     *
     * @param resource 图片资源
     * @return 图片
     * @since 4.4.1
     */
    public static BufferedImage read(Resource resource) throws IOException {
        return read(resource.getUrl());
    }

    /**
     * 从流中读取图片
     *
     * @param imageStream 图片文件
     * @return 图片
     * @since 3.2.2
     */
    public static BufferedImage read(InputStream imageStream) throws IOException {
        BufferedImage result;
        try {
            result = ImageIO.read(imageStream);
        } catch (IOException e) {
            throw new IOException(e);
        }

        if (null == result) {
            throw new IllegalArgumentException("Image type is not supported!");
        }

        return result;
    }

    /**
     * 从图片流中读取图片
     *
     * @param imageStream 图片文件
     * @return 图片
     * @since 3.2.2
     */
    public static BufferedImage read(ImageInputStream imageStream) throws IOException {
        BufferedImage result;
        try {
            result = ImageIO.read(imageStream);
        } catch (IOException e) {
            throw new IOException(e);
        }

        if (null == result) {
            throw new IllegalArgumentException("Image type is not supported!");
        }

        return result;
    }

    /**
     * 从URL中读取图片
     *
     * @param imageUrl 图片文件
     * @return 图片
     * @since 3.2.2
     */
    public static BufferedImage read(URL imageUrl) throws IOException {
        BufferedImage result;
        try {
            result = ImageIO.read(imageUrl);
        } catch (IOException e) {
            throw new IOException(e);
        }

        if (null == result) {
            throw new IllegalArgumentException("Image type of [" + imageUrl + "] is not supported!");
        }

        return result;
    }

    /**
     * 获取{@link ImageOutputStream}
     *
     * @param out {@link OutputStream}
     * @return {@link ImageOutputStream}
     * @throws IOException IO异常
     * @since 3.1.2
     */
    public static ImageOutputStream getImageOutputStream(OutputStream out) throws IOException {
        ImageOutputStream result;
        try {
            result = ImageIO.createImageOutputStream(out);
        } catch (IOException e) {
            throw new IOException(e);
        }

        if (null == result) {
            throw new IllegalArgumentException("Image type is not supported!");
        }

        return result;
    }

    /**
     * 获取{@link ImageOutputStream}
     *
     * @param outFile {@link File}
     * @return {@link ImageOutputStream}
     * @throws IOException IO异常
     * @since 3.2.2
     */
    public static ImageOutputStream getImageOutputStream(File outFile) throws IOException {
        ImageOutputStream result;
        try {
            result = ImageIO.createImageOutputStream(outFile);
        } catch (IOException e) {
            throw new IOException(e);
        }

        if (null == result) {
            throw new IllegalArgumentException("Image type of file [" + outFile.getName() + "] is not supported!");
        }

        return result;
    }

    /**
     * 获取{@link ImageInputStream}
     *
     * @param in {@link InputStream}
     * @return {@link ImageInputStream}
     * @throws IOException IO异常
     * @since 3.1.2
     */
    public static ImageInputStream getImageInputStream(InputStream in) throws IOException {
        ImageOutputStream result;
        try {
            result = ImageIO.createImageOutputStream(in);
        } catch (IOException e) {
            throw new IOException(e);
        }

        if (null == result) {
            throw new IllegalArgumentException("Image type is not supported!");
        }

        return result;
    }

    /**
     * 根据给定的Image对象和格式获取对应的{@link ImageWriter}，如果未找到合适的Writer，返回null
     *
     * @param img        {@link Image}
     * @param formatName 图片格式，例如"jpg"、"png"
     * @return {@link ImageWriter}
     * @since 4.3.2
     */
    public static ImageWriter getWriter(Image img, String formatName) {
        final ImageTypeSpecifier type = ImageTypeSpecifier.createFromRenderedImage(toBufferedImage(img, formatName));
        final Iterator<ImageWriter> iter = ImageIO.getImageWriters(type, formatName);
        return iter.hasNext() ? iter.next() : null;
    }

    /**
     * 根据给定的图片格式或者扩展名获取{@link ImageWriter}，如果未找到合适的Writer，返回null
     *
     * @param formatName 图片格式或扩展名，例如"jpg"、"png"
     * @return {@link ImageWriter}
     * @since 4.3.2
     */
    public static ImageWriter getWriter(String formatName) {
        ImageWriter writer = null;
        Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName(formatName);
        if (iter.hasNext()) {
            writer = iter.next();
        }
        if (null == writer) {
            // 尝试扩展名获取
            iter = ImageIO.getImageWritersBySuffix(formatName);
            if (iter.hasNext()) {
                writer = iter.next();
            }
        }
        return writer;
    }

    // -------------------------------------------------------------------------------------------------------------------- Color

    /**
     * Color对象转16进制表示，例如#fcf6d6
     *
     * @param color {@link Color}
     * @return 16进制的颜色值，例如#fcf6d6
     * @since 4.1.14
     */
    public static String toHex(Color color) {
        return toHex(color.getRed(), color.getGreen(), color.getBlue());
    }

    /**
     * RGB颜色值转换成十六进制颜色码
     *
     * @param r 红(R)
     * @param g 绿(G)
     * @param b 蓝(B)
     * @return 返回字符串形式的 十六进制颜色码 如
     */
    public static String toHex(int r, int g, int b) {
        // rgb 小于 255
        if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255) {
            throw new IllegalArgumentException("RGB must be 0~255!");
        }
        return String.format("#%02X%02X%02X", r, g, b);
    }

    /**
     * 16进制的颜色值转换为Color对象，例如#fcf6d6
     *
     * @param hex 16进制的颜色值，例如#fcf6d6
     * @return {@link Color}
     * @since 4.1.14
     */
    public static Color hexToColor(String hex) {
        return getColor(Integer.parseInt(StringUtils.removePrefix(hex, "#"), 16));
    }

    /**
     * 获取一个RGB值对应的颜色
     *
     * @param rgb RGB值
     * @return {@link Color}
     * @since 4.1.14
     */
    public static Color getColor(int rgb) {
        return new Color(rgb);
    }

    /**
     * 将颜色值转换成具体的颜色类型 汇集了常用的颜色集，支持以下几种形式：
     *
     * <pre>
     * 1. 颜色的英文名（大小写皆可）
     * 2. 16进制表示，例如：#fcf6d6或者$fcf6d6
     * 3. RGB形式，例如：13,148,252
     * </pre>
     * <p>
     * 方法来自：com.lnwazg.kit
     *
     * @param colorName 颜色的英文名，16进制表示或RGB表示
     * @return {@link Color}
     * @since 4.1.14
     */
    public static Color getColor(String colorName) {
        if (StringUtils.isEmpty(colorName)) {
            return null;
        }
        colorName = colorName.toUpperCase();

        if ("BLACK".equals(colorName)) {
            return Color.BLACK;
        } else if ("WHITE".equals(colorName)) {
            return Color.WHITE;
        } else if ("LIGHTGRAY".equals(colorName) || "LIGHT_GRAY".equals(colorName)) {
            return Color.LIGHT_GRAY;
        } else if ("GRAY".equals(colorName)) {
            return Color.GRAY;
        } else if ("DARKGRAY".equals(colorName) || "DARK_GRAY".equals(colorName)) {
            return Color.DARK_GRAY;
        } else if ("RED".equals(colorName)) {
            return Color.RED;
        } else if ("PINK".equals(colorName)) {
            return Color.PINK;
        } else if ("ORANGE".equals(colorName)) {
            return Color.ORANGE;
        } else if ("YELLOW".equals(colorName)) {
            return Color.YELLOW;
        } else if ("GREEN".equals(colorName)) {
            return Color.GREEN;
        } else if ("MAGENTA".equals(colorName)) {
            return Color.MAGENTA;
        } else if ("CYAN".equals(colorName)) {
            return Color.CYAN;
        } else if ("BLUE".equals(colorName)) {
            return Color.BLUE;
        } else if ("DARKGOLD".equals(colorName)) {
            // 暗金色
            return hexToColor("#9e7e67");
        } else if ("LIGHTGOLD".equals(colorName)) {
            // 亮金色
            return hexToColor("#ac9c85");
        } else if (StringUtils.startWith(colorName, SYMBOL_HASH_CHAR)) {
            return hexToColor(colorName);
        } else if (StringUtils.startWith(colorName, '$')) {
            // 由于#在URL传输中无法传输，因此用$代替#
            return hexToColor("#" + colorName.substring(1));
        } else {
            // rgb值
            final List<String> rgb = StringUtils.splitList(colorName, ',');
            if (3 == rgb.size()) {
                final Integer r = toInt(rgb.get(0));
                final Integer g = toInt(rgb.get(1));
                final Integer b = toInt(rgb.get(2));
                if (!ArrayUtils.hasNull(r, g, b)) {
                    return new Color(r, g, b);
                }
            } else {
                return null;
            }
        }
        return null;
    }

    /**
     * 转换为int<br>
     * 如果给定的值为{@code null}，或者转换失败，返回默认值{@code null}<br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @return 结果
     */
    public static Integer toInt(Object value) {
        return toInt(value, null);
    }

    /**
     * 转换为int<br>
     * 如果给定的值为空，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value        被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static Integer toInt(Object value, Integer defaultValue) {
        return Converter.convertIfNecessary(value, Integer.class);
    }

    /**
     * 生成随机颜色
     *
     * @return 随机颜色
     * @since 3.1.2
     */
    public static Color randomColor() {
        return randomColor(null);
    }

    /**
     * 生成随机颜色
     *
     * @param random 随机对象 {@link Random}
     * @return 随机颜色
     * @since 3.1.2
     */
    public static Color randomColor(Random random) {
        if (null == random) {
            random = RandomUtils.getRandom();
        }
        return new Color(random.nextInt(RGB_COLOR_BOUND), random.nextInt(RGB_COLOR_BOUND), random.nextInt(RGB_COLOR_BOUND));
    }

    /**
     * 获得修正后的矩形坐标位置，变为以背景中心为基准坐标（即x,y == 0,0时，处于背景正中）
     *
     * @param rectangle        矩形
     * @param backgroundWidth  参考宽（背景宽）
     * @param backgroundHeight 参考高（背景高）
     * @return 修正后的{@link Point}
     * @since 5.3.6
     */
    public static Point getPointBaseCentre(Rectangle rectangle, int backgroundWidth, int backgroundHeight) {
        return new Point(
                rectangle.x + (Math.abs(backgroundWidth - rectangle.width) / 2),
                rectangle.y + (Math.abs(backgroundHeight - rectangle.height) / 2)
        );
    }

    /**
     * 获取给定图片的主色调，背景填充用
     *
     * @param image      {@link BufferedImage}
     * @param rgbFilters 过滤多种颜色
     * @return {@link String} #ffffff
     * @since 5.6.7
     */
    public static String getMainColor(BufferedImage image, int[]... rgbFilters) {
        int r, g, b;
        int width = image.getWidth();
        int height = image.getHeight();
        Map<String, Long> countMap = new HashMap<>(1 << 4);
        int minx = image.getMinX();
        int miny = image.getMinY();
        for (int i = minx; i < width; i++) {
            for (int j = miny; j < height; j++) {
                int pixel = image.getRGB(i, j);
                r = (pixel & 0xff0000) >> 16;
                g = (pixel & 0xff00) >> 8;
                b = (pixel & 0xff);
                if (rgbFilters != null && rgbFilters.length > 0) {
                    for (int[] rgbFilter : rgbFilters) {
                        if (r == rgbFilter[0] && g == rgbFilter[1] && b == rgbFilter[2]) {
                            break;
                        }
                    }
                }
                countMap.merge(r + "-" + g + "-" + b, 1L, Long::sum);
            }
        }
        String maxColor = null;
        long maxCount = 0;
        for (Map.Entry<String, Long> entry : countMap.entrySet()) {
            String key = entry.getKey();
            Long count = entry.getValue();
            if (count > maxCount) {
                maxColor = key;
                maxCount = count;
            }
        }
        final String[] splitRgbStr = StringUtils.splitToArray(maxColor, '-');
        String rHex = Integer.toHexString(Integer.parseInt(splitRgbStr[0]));
        String gHex = Integer.toHexString(Integer.parseInt(splitRgbStr[1]));
        String bHex = Integer.toHexString(Integer.parseInt(splitRgbStr[2]));
        rHex = rHex.length() == 1 ? "0" + rHex : rHex;
        gHex = gHex.length() == 1 ? "0" + gHex : gHex;
        bHex = bHex.length() == 1 ? "0" + bHex : bHex;
        return "#" + rHex + gHex + bHex;
    }

    // ------------------------------------------------------------------------------------------------------ 背景图换算

    /**
     * 背景移除
     * 图片去底工具
     * 将 "纯色背景的图片" 还原成 "透明背景的图片"
     * 将纯色背景的图片转成矢量图
     * 取图片边缘的像素点和获取到的图片主题色作为要替换的背景色
     * 再加入一定的容差值,然后将所有像素点与该颜色进行比较
     * 发现相同则将颜色不透明度设置为0,使颜色完全透明.
     *
     * @param inputPath  要处理图片的路径
     * @param outputPath 输出图片的路径
     * @param tolerance  容差值[根据图片的主题色,加入容差值,值的范围在0~255之间]
     * @return 返回处理结果 true:图片处理完成 false:图片处理失败
     */
    public static boolean backgroundRemoval(String inputPath, String outputPath, int tolerance) throws IOException {
        return BackgroundRemoval.backgroundRemoval(inputPath, outputPath, tolerance);
    }

    /**
     * 背景移除
     * 图片去底工具
     * 将 "纯色背景的图片" 还原成 "透明背景的图片"
     * 将纯色背景的图片转成矢量图
     * 取图片边缘的像素点和获取到的图片主题色作为要替换的背景色
     * 再加入一定的容差值,然后将所有像素点与该颜色进行比较
     * 发现相同则将颜色不透明度设置为0,使颜色完全透明.
     *
     * @param input     需要进行操作的图片
     * @param output    最后输出的文件
     * @param tolerance 容差值[根据图片的主题色,加入容差值,值的取值范围在0~255之间]
     * @return 返回处理结果 true:图片处理完成 false:图片处理失败
     */
    public static boolean backgroundRemoval(File input, File output, int tolerance) throws IOException {
        return BackgroundRemoval.backgroundRemoval(input, output, tolerance);
    }

    /**
     * 背景移除
     * 图片去底工具
     * 将 "纯色背景的图片" 还原成 "透明背景的图片"
     * 将纯色背景的图片转成矢量图
     * 取图片边缘的像素点和获取到的图片主题色作为要替换的背景色
     * 再加入一定的容差值,然后将所有像素点与该颜色进行比较
     * 发现相同则将颜色不透明度设置为0,使颜色完全透明.
     *
     * @param input     需要进行操作的图片
     * @param output    最后输出的文件
     * @param override  指定替换成的背景颜色 为null时背景为透明
     * @param tolerance 容差值[根据图片的主题色,加入容差值,值的取值范围在0~255之间]
     * @return 返回处理结果 true:图片处理完成 false:图片处理失败
     */
    public static boolean backgroundRemoval(File input, File output, Color override, int tolerance) throws IOException {
        return BackgroundRemoval.backgroundRemoval(input, output, override, tolerance);
    }

    /**
     * 背景移除
     * 图片去底工具
     * 将 "纯色背景的图片" 还原成 "透明背景的图片"
     * 将纯色背景的图片转成矢量图
     * 取图片边缘的像素点和获取到的图片主题色作为要替换的背景色
     * 再加入一定的容差值,然后将所有像素点与该颜色进行比较
     * 发现相同则将颜色不透明度设置为0,使颜色完全透明.
     *
     * @param bufferedImage 需要进行处理的图片流
     * @param override      指定替换成的背景颜色 为null时背景为透明
     * @param tolerance     容差值[根据图片的主题色,加入容差值,值的取值范围在0~255之间]
     * @return 返回处理好的图片流
     */
    public static BufferedImage backgroundRemoval(BufferedImage bufferedImage, Color override, int tolerance) {
        return BackgroundRemoval.backgroundRemoval(bufferedImage, override, tolerance);
    }

    /**
     * 背景移除
     * 图片去底工具
     * 将 "纯色背景的图片" 还原成 "透明背景的图片"
     * 将纯色背景的图片转成矢量图
     * 取图片边缘的像素点和获取到的图片主题色作为要替换的背景色
     * 再加入一定的容差值,然后将所有像素点与该颜色进行比较
     * 发现相同则将颜色不透明度设置为0,使颜色完全透明.
     *
     * @param outputStream 需要进行处理的图片字节数组流
     * @param override     指定替换成的背景颜色 为null时背景为透明
     * @param tolerance    容差值[根据图片的主题色,加入容差值,值的取值范围在0~255之间]
     * @return 返回处理好的图片流
     */
    public static BufferedImage backgroundRemoval(ByteArrayOutputStream outputStream, Color override, int tolerance) {
        return BackgroundRemoval.backgroundRemoval(outputStream, override, tolerance);
    }

    /**
     * 图片颜色转换<br>
     * 可以使用灰度 (gray)等
     *
     * @param colorSpace 颜色模式，如灰度等
     * @param image      被转换的图片
     * @return 转换后的图片
     * @since 5.7.8
     */
    public static BufferedImage colorConvert(ColorSpace colorSpace, BufferedImage image) {
        return filter(new ColorConvertOp(colorSpace, null), image);
    }

    /**
     * 转换图片<br>
     * 可以使用一系列平移 (translation)、缩放 (scale)、翻转 (flip)、旋转 (rotation) 和错切 (shear) 来构造仿射变换。
     *
     * @param xform 2D仿射变换，它执行从 2D 坐标到其他 2D 坐标的线性映射，保留了线的“直线性”和“平行性”。
     * @param image 被转换的图片
     * @return 转换后的图片
     * @since 5.7.8
     */
    public static BufferedImage transform(AffineTransform xform, BufferedImage image) {
        return filter(new AffineTransformOp(xform, null), image);
    }

    /**
     * 图片过滤转换
     *
     * @param op    过滤操作实现，如二维转换可传入{@link AffineTransformOp}
     * @param image 原始图片
     * @return 过滤后的图片
     * @since 5.7.8
     */
    public static BufferedImage filter(BufferedImageOp op, BufferedImage image) {
        return op.filter(image, null);
    }

    /**
     * 图片滤镜，借助 {@link ImageFilter}实现，实现不同的图片滤镜
     *
     * @param filter 滤镜实现
     * @param image  图片
     * @return 滤镜后的图片
     * @since 5.7.8
     */
    public static Image filter(ImageFilter filter, Image image) {
        return Toolkit.getDefaultToolkit().createImage(
                new FilteredImageSource(image.getSource(), filter));
    }

    /**
     * 图像类型转换：GIF->JPG、GIF->PNG、PNG->JPG、PNG->GIF(X)、BMP->PNG
     *
     * @param srcImageFile  源图像地址
     * @param formatName    包含格式非正式名称的 String：如JPG、JPEG、GIF等
     * @param destImageFile 目标图像地址
     */
    public static void convert(String srcImageFile, String formatName, String destImageFile) {
        try {
            File f = new File(srcImageFile);
            f.canRead();
            f.canWrite();
            BufferedImage src = ImageIO.read(f);
            ImageIO.write(src, formatName, new File(destImageFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Convert an Image into a TYPE_INT_ARGB BufferedImage. If the image is already of this type, the original image is returned unchanged.
     *
     * @param image the image to convert
     * @return the converted image
     */
    public static BufferedImage convertimagetoArgb(Image image) {
        if (image instanceof BufferedImage && ((BufferedImage) image).getType() == BufferedImage.TYPE_INT_ARGB) {
            return (BufferedImage) image;
        }
        BufferedImage p = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = p.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return p;
    }

    /**
     * 图像切割(按指定起点坐标和宽高切割)
     *
     * @param srcImageFile 源图像地址
     * @param result       切片后的图像地址
     * @param x            目标切片起点坐标X
     * @param y            目标切片起点坐标Y
     * @param width        目标切片宽度
     * @param height       目标切片高度
     */
    public static void cut(String srcImageFile, String result,
                           int x, int y, int width, int height) {
        try {
            // 读取源图像
            BufferedImage bi = ImageIO.read(new File(srcImageFile));
            int srcWidth = bi.getHeight();
            int srcHeight = bi.getWidth();
            if (srcWidth > 0 && srcHeight > 0) {
                Image image = bi.getScaledInstance(srcWidth, srcHeight,
                        Image.SCALE_DEFAULT);
                ImageFilter cropFilter = new CropImageFilter(x, y, width, height);
                Image img = Toolkit.getDefaultToolkit().createImage(
                        new FilteredImageSource(image.getSource(),
                                cropFilter));
                BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                Graphics g = tag.getGraphics();
                g.drawImage(img, 0, 0, width, height, null);
                g.dispose();
                ImageIO.write(tag, "JPEG", new File(result));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 图像切割（指定切片的行数和列数）
     *
     * @param srcImageFile 源图像地址
     * @param descDir      切片目标文件夹
     * @param rows         目标切片行数。默认2，必须是范围 [1, 20] 之内
     * @param cols         目标切片列数。默认2，必须是范围 [1, 20] 之内
     */
    public static void cut2(String srcImageFile, String descDir,
                            int rows, int cols) {
        int step20 = 20;
        try {
            // 切片行数
            if (rows <= 0 || rows > step20) {
                rows = 2;
            }
            // 切片列数
            if (cols <= 0 || cols > step20) {
                cols = 2;
            }
            // 读取源图像
            BufferedImage bi = ImageIO.read(new File(srcImageFile));
            int srcWidth = bi.getHeight();
            int srcHeight = bi.getWidth();
            if (srcWidth > 0 && srcHeight > 0) {
                Image img;
                ImageFilter cropFilter;
                Image image = bi.getScaledInstance(srcWidth, srcHeight, Image.SCALE_DEFAULT);
                int destWidth = srcWidth;
                int destHeight = srcHeight;
                if (srcWidth % cols == 0) {
                    destWidth = srcWidth / cols;
                } else {
                    destWidth = (int) Math.floor(srcWidth / cols) + 1;
                }
                if (srcHeight % rows == 0) {
                    destHeight = srcHeight / rows;
                } else {
                    destHeight = (int) Math.floor(srcWidth / rows) + 1;
                }
                // 循环建立切片
                // 改进的想法:是否可用多线程加快切割速度
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        // 四个参数分别为图像起点坐标和宽高
                        // 即: CropImageFilter(int x,int y,int width,int height)
                        cropFilter = new CropImageFilter(j * destWidth, i * destHeight,
                                destWidth, destHeight);
                        img = Toolkit.getDefaultToolkit().createImage(
                                new FilteredImageSource(image.getSource(),
                                        cropFilter));
                        BufferedImage tag = new BufferedImage(destWidth,
                                destHeight, BufferedImage.TYPE_INT_RGB);
                        Graphics g = tag.getGraphics();
                        g.drawImage(img, 0, 0, null);
                        g.dispose();
                        ImageIO.write(tag, "JPEG", new File(descDir
                                + "_r" + i + "_c" + j + ".jpg"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 图像切割（指定切片的宽度和高度）
     *
     * @param srcImageFile 源图像地址
     * @param descDir      切片目标文件夹
     * @param destWidth    目标切片宽度。默认200
     * @param destHeight   目标切片高度。默认150
     */
    public static void cut3(String srcImageFile, String descDir,
                            int destWidth, int destHeight) {
        try {
            // 切片宽度
            if (destWidth <= 0) {
                destWidth = 200;
            }
            // 切片高度
            if (destHeight <= 0) {
                destHeight = 150;
            }
            // 读取源图像
            BufferedImage bi = ImageIO.read(new File(srcImageFile));
            int srcWidth = bi.getHeight();
            int srcHeight = bi.getWidth();
            if (srcWidth > destWidth && srcHeight > destHeight) {
                Image img;
                ImageFilter cropFilter;
                Image image = bi.getScaledInstance(srcWidth, srcHeight, Image.SCALE_DEFAULT);
                int cols = 0;
                int rows = 0;
                if (srcWidth % destWidth == 0) {
                    cols = srcWidth / destWidth;
                } else {
                    cols = (int) Math.floor(srcWidth / destWidth) + 1;
                }
                if (srcHeight % destHeight == 0) {
                    rows = srcHeight / destHeight;
                } else {
                    rows = (int) Math.floor(srcHeight / destHeight) + 1;
                }
                // 循环建立切片
                // 改进的想法:是否可用多线程加快切割速度
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        // 四个参数分别为图像起点坐标和宽高
                        // 即: CropImageFilter(int x,int y,int width,int height)
                        cropFilter = new CropImageFilter(j * destWidth, i * destHeight,
                                destWidth, destHeight);
                        img = Toolkit.getDefaultToolkit().createImage(
                                new FilteredImageSource(image.getSource(),
                                        cropFilter));
                        BufferedImage tag = new BufferedImage(destWidth,
                                destHeight, BufferedImage.TYPE_INT_RGB);
                        Graphics g = tag.getGraphics();
                        g.drawImage(img, 0, 0, null);
                        g.dispose();
                        ImageIO.write(tag, "JPEG", new File(descDir
                                + "_r" + i + "_c" + j + ".jpg"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 滤镜转化
     *
     * @param target      目标
     * @param outFile     输出目录
     * @param imageFilter 滤镜
     * @return 结果
     * @throws IOException IOException
     */
    public static File filter(File target, File outFile, com.chua.common.support.image.filter.ImageFilter imageFilter) throws IOException {
        BufferedImage bufferedImage = imageFilter.converter(ImageIO.read(target));
        ImageIO.write(bufferedImage, FileUtils.getExtension(outFile), outFile);
        return outFile;
    }

    /**
     * 滤镜转化
     *
     * @param target      目标
     * @param outFile     输出目录
     * @param imageFilter 滤镜
     * @return 结果
     * @throws IOException IOException
     */
    public static File filter(String target, String outFile, com.chua.common.support.image.filter.ImageFilter imageFilter) throws IOException {
        BufferedImage bufferedImage = imageFilter.converter(ImageIO.read(new File(target)));
        File file = new File(outFile);
        FileUtils.createIfNotExist(file);
        ImageIO.write(bufferedImage, FileUtils.getExtension(outFile), file);
        return file;
    }

    /**
     * 计算text的长度（一个中文算两个字符）
     *
     * @param text 文本
     * @return 长度
     */
    public static int getLength(String text) {
        int length = 0;
        for (int i = 0; i < text.length(); i++) {
            if ((text.charAt(i) + "").getBytes().length > 1) {
                length += 2;
            } else {
                length += 1;
            }
        }
        return length / 2;
    }


    /**
     * 彩色转为黑白
     *
     * @param srcImageFile  源图像地址
     * @param destImageFile 目标图像地址
     */
    public static void gray(String srcImageFile, String destImageFile) {
        try {
            BufferedImage src = ImageIO.read(new File(srcImageFile));
            ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
            ColorConvertOp op = new ColorConvertOp(cs, null);
            src = op.filter(src, null);
            ImageIO.write(src, "JPEG", new File(destImageFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 水平合并两张图片
     *
     * @param image1  图片1
     * @param image2  图片2
     * @param outFile 输出文件
     * @throws IOException IOException
     */
    public static void hMergeImages(String image1, String image2, File outFile) throws IOException {
        BufferedImage buffer1 = ImageIO.read(new File(image1));
        BufferedImage buffer2 = ImageIO.read(new File(image2));
        BufferedImage source = new BufferedImage(buffer1.getWidth() + buffer2.getWidth(), buffer1.getHeight(), TYPE_INT_RGB);
        Graphics2D graphics = source.createGraphics();
        graphics.drawImage(buffer1, 0, 0, buffer1.getWidth(), buffer1.getHeight(), null);
        graphics.drawImage(buffer2, buffer1.getWidth(), 0, buffer2.getWidth(), buffer2.getHeight(), null);
        graphics.dispose();

        ImageIO.write(source, FileUtils.getExtension(outFile), outFile);
    }


    /**
     * 给图片添加图片水印
     *
     * @param pressImg      水印图片
     * @param srcImageFile  源图像地址
     * @param destImageFile 目标图像地址
     * @param x             修正值。 默认在中间
     * @param y             修正值。 默认在中间
     * @param alpha         透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     */
    public static void pressImage(String pressImg, String srcImageFile, String destImageFile,
                                  int x, int y, float alpha) {
        try {
            File img = new File(srcImageFile);
            Image src = ImageIO.read(img);
            int wideth = src.getWidth(null);
            int height = src.getHeight(null);
            BufferedImage image = new BufferedImage(wideth, height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            g.drawImage(src, 0, 0, wideth, height, null);
            Image srcBiao = ImageIO.read(new File(pressImg));
            int srcBiaoWidth = srcBiao.getWidth(null);
            int srcBiaoHeight = srcBiao.getHeight(null);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,
                    alpha));
            g.drawImage(srcBiao, (wideth - srcBiaoWidth) / 2,
                    (height - srcBiaoHeight) / 2, srcBiaoWidth, srcBiaoHeight, null);
            g.dispose();
            ImageIO.write(image, "JPEG", new File(destImageFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 给图片添加文字水印
     *
     * @param pressText     水印文字
     * @param srcImageFile  源图像地址
     * @param destImageFile 目标图像地址
     * @param fontName      水印的字体名称
     * @param fontStyle     水印的字体样式
     * @param color         水印的字体颜色
     * @param fontSize      水印的字体大小
     * @param x             修正值
     * @param y             修正值
     * @param alpha         透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     */
    public static void pressText(String pressText,
                                 String srcImageFile, String destImageFile, String fontName,
                                 int fontStyle, Color color, int fontSize, int x,
                                 int y, float alpha) {
        try {
            File img = new File(srcImageFile);
            Image src = ImageIO.read(img);
            int width = src.getWidth(null);
            int height = src.getHeight(null);
            BufferedImage image = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            g.drawImage(src, 0, 0, width, height, null);
            g.setColor(color);
            g.setFont(new Font(fontName, fontStyle, fontSize));
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,
                    alpha));
            g.drawString(pressText, (width - (getLength(pressText) * fontSize))
                    / 2 + x, (height - fontSize) / 2 + y);
            g.dispose();
            ImageIO.write(image, "JPEG", new File(destImageFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 给图片添加文字水印
     *
     * @param pressText     水印文字
     * @param srcImageFile  源图像地址
     * @param destImageFile 目标图像地址
     * @param fontName      字体名称
     * @param fontStyle     字体样式
     * @param color         字体颜色
     * @param fontSize      字体大小
     * @param x             修正值
     * @param y             修正值
     * @param alpha         透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     */
    public static void pressText2(String pressText, String srcImageFile, String destImageFile,
                                  String fontName, int fontStyle, Color color, int fontSize, int x,
                                  int y, float alpha) {
        try {
            Image src = ImageIO.read(new File(srcImageFile));
            int width = src.getWidth(null);
            int height = src.getHeight(null);
            BufferedImage image = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            g.drawImage(src, 0, 0, width, height, null);
            g.setColor(color);
            g.setFont(new Font(fontName, fontStyle, fontSize));
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,
                    alpha));
            g.drawString(pressText, (width - (getLength(pressText) * fontSize))
                    / 2 + x, (height - fontSize) / 2 + y);
            g.dispose();
            ImageIO.write(image, "JPEG", new File(destImageFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 缩放图像（按比例缩放）
     *
     * @param srcImageFile 源图像文件地址
     * @param result       缩放后的图像地址
     * @param scale        缩放比例
     * @param flag         缩放选择:true 放大; false 缩小;
     */
    public static void scale(String srcImageFile, String result,
                             int scale, boolean flag) {
        try {
            // 读入文件
            BufferedImage src = ImageIO.read(new File(srcImageFile));
            int width = src.getWidth();
            int height = src.getHeight();
            if (flag) {
                width = width * scale;
                height = height * scale;
            } else {
                width = width / scale;
                height = height / scale;
            }
            Image image = src.getScaledInstance(width, height,
                    Image.SCALE_DEFAULT);
            BufferedImage tag = new BufferedImage(width, height,
                    TYPE_INT_RGB);
            Graphics g = tag.getGraphics();
            g.drawImage(image, 0, 0, null);
            g.dispose();
            ImageIO.write(tag, "JPEG", new File(result));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 缩放图像（按高度和宽度缩放）
     *
     * @param srcImageFile 源图像文件地址
     * @param result       缩放后的图像地址
     * @param height       缩放后的高度
     * @param width        缩放后的宽度
     * @param bb           比例不对时是否需要补白：true为补白; false为不补白;
     */
    public static void scale2(String srcImageFile, String result, int height, int width, boolean bb) {
        try {
            // 缩放比例
            double ratio = 0.0;
            File f = new File(srcImageFile);
            BufferedImage bi = ImageIO.read(f);
            Image itemp = bi.getScaledInstance(width, height, SCALE_SMOOTH);
            if ((bi.getHeight() > height) || (bi.getWidth() > width)) {
                if (bi.getHeight() > bi.getWidth()) {
                    ratio = (new Integer(height)).doubleValue()
                            / bi.getHeight();
                } else {
                    ratio = (new Integer(width)).doubleValue() / bi.getWidth();
                }
                AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(ratio, ratio), null);
                itemp = op.filter(bi, null);
            }
            //补白
            if (bb) {
                BufferedImage image = new BufferedImage(width, height,
                        TYPE_INT_RGB);
                Graphics2D g = image.createGraphics();
                g.setColor(Color.white);
                g.fillRect(0, 0, width, height);
                if (width == itemp.getWidth(null)) {
                    g.drawImage(itemp, 0, (height - itemp.getHeight(null)) / 2,
                            itemp.getWidth(null), itemp.getHeight(null),
                            Color.white, null);
                } else {
                    g.drawImage(itemp, (width - itemp.getWidth(null)) / 2, 0,
                            itemp.getWidth(null), itemp.getHeight(null),
                            Color.white, null);
                }
                g.dispose();
                itemp = image;
            }
            ImageIO.write((BufferedImage) itemp, "JPEG", new File(result));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 垂直合并两张图片
     *
     * @param image1  图片1
     * @param image2  图片2
     * @param outFile 输出文件
     * @throws IOException IOException
     */
    public static void vMergeImages(String image1, String image2, File outFile) throws IOException {
        BufferedImage buffer1 = ImageIO.read(new File(image1));
        BufferedImage buffer2 = ImageIO.read(new File(image2));
        BufferedImage source = new BufferedImage(buffer1.getWidth(), buffer1.getHeight() + buffer2.getHeight(), TYPE_INT_RGB);
        Graphics2D graphics = source.createGraphics();
        graphics.drawImage(buffer1, 0, 0, buffer1.getWidth(), buffer1.getHeight(), null);
        graphics.drawImage(buffer2, 0, buffer1.getHeight(), buffer2.getWidth(), buffer2.getHeight(), null);
        graphics.dispose();

        ImageIO.write(source, FileUtils.getExtension(outFile), outFile);
    }

    /**
     * 去除黑色
     *
     * @param file 文件
     * @return image
     */
    public static BufferedImage extractRed(File file) throws IOException {
        BufferedImage image = ImageIO.read(file);
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(), image.getHeight(), TYPE_INT_RGB);
        for (int x = bufferedImage.getMinX(); x < bufferedImage.getWidth(); x++) {
            // 内层遍历是X轴的像素
            for (int y = bufferedImage.getMinX(); y < bufferedImage.getHeight(); y++) {
                int rgb = image.getRGB(x, y);
                bufferedImage.setRGB(x, y, colorToWhite(rgb));
            }
        }

        for (int x = bufferedImage.getMinX(); x < bufferedImage.getWidth(); x++) {
            // 内层遍历是X轴的像素
            for (int y = bufferedImage.getMinX(); y < bufferedImage.getHeight(); y++) {
                int rgb = bufferedImage.getRGB(x, y);
                bufferedImage.setRGB(x, y, colorToRedAndWhite(rgb));
            }
        }


        for (int x = bufferedImage.getMinX(); x < bufferedImage.getWidth(); x++) {
            // 内层遍历是X轴的像素
            for (int y = bufferedImage.getMinX(); y < bufferedImage.getHeight(); y++) {
                int rgb = bufferedImage.getRGB(x, y);
                bufferedImage.setRGB(x, y, repairLoss(rgb));
            }
        }

        return bufferedImage;
    }

    /**
     * 修复红色缺失部分
     *
     * @param rgb rgb
     * @return
     */
    private static int repairLoss(int rgb) {
        return 0;
    }

    private static int colorToRedAndWhite(int color) {
        Color color1 = hsbColor(new Color(color));
        if (color1 == Color.RED || color1 == Color.MAGENTA) {
            return Color.RED.getRGB();
        }

        return Color.WHITE.getRGB();
    }

    private static int colorToWhite(int color) {
        Color color1 = hsbColor(new Color(color));
        if (color1 == Color.BLACK) {
            return Color.WHITE.getRGB();
        }

        if (color1 == Color.WHITE || color1 == Color.CYAN) {
            return Color.WHITE.getRGB();
        }
        return color;
    }

    /**
     * hsb
     *
     * @param color color
     * @return hsb
     */
    public static Color hsbColor(Color color) {
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();

        float[] hsb = new float[3];
        Color.RGBtoHSB(red, green, blue, hsb);
        double h = hsb[0] * 180;
        double s = hsb[1] * 255;
        double v = hsb[2] * 255;


        boolean b1 = (h >= 0 && h <= 10) || (h >= 156 && h <= 180);
        if (b1) {
            if (s >= 43 && s <= 255 && v >= 46 && v <= 255) {
                return Color.RED;
            }
        }


        if (h >= 11 && h <= 25 && s >= 43 && s <= 255 && v >= 46 && v <= 255) {
            return Color.ORANGE;
        }


        if (h >= 26 && h <= 34 && s >= 43 && s <= 255 && v >= 46 && v <= 255) {
            return Color.YELLOW;
        }

        if (h >= 35 && h <= 77 && s >= 43 && s <= 255 && v >= 46 && v <= 255) {
            return Color.GREEN;
        }

        if (h >= 78 && h <= 99 && s >= 43 && s <= 255 && v >= 46 && v <= 255) {
            return Color.CYAN;
        }

        if (h >= 100 && h <= 124 && s >= 43 && s <= 255 && v >= 46 && v <= 255) {
            return Color.BLUE;
        }

        if (h >= 125 && h <= 155 && s >= 43 && s <= 255 && v >= 46 && v <= 255) {
            return Color.MAGENTA;
        }

        boolean b = Double.isNaN(h) || (h >= 0 && h <= 180);
        if (b) {
            if (s >= 0 && s <= 255 && v >= 0 && v <= 46) {
                return Color.BLACK;
            }

            if (s >= 0 && s <= 88 && v >= 46 && v <= 220) {
                return Color.GRAY;
            }

            if (s >= 0 && s <= 43 && v >= 221 && v <= 255) {
                return Color.WHITE;
            }
        }

        return Color.PINK;
    }


}
