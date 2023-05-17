package com.chua.common.support.file;

import com.chua.common.support.spi.ServiceProvider;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BiFunction;

/**
 * 视频文件
 *
 * @author CH
 */
public interface VideoFile {
    /**
     * 初始化
     *
     * @param inputStream 流
     * @return VideoFile
     */
    static VideoFile create(InputStream inputStream) {
        return create("ffmpeg", inputStream);
    }

    /**
     * 初始化
     *
     * @param inputStream 流
     * @return VideoFile
     */
    static VideoFile create(String name, InputStream inputStream) {
        return ServiceProvider.of(VideoFile.class).getNewExtension(name, inputStream);
    }

    /**
     * 初始化
     *
     * @param file 文件名
     * @return VideoFile
     */
    static VideoFile create(File file) {
        return create("ffmpeg", file);
    }

    /**
     * 初始化
     *
     * @param file 文件名
     * @return VideoFile
     */
    static VideoFile create(String name, File file) {
        return ServiceProvider.of(VideoFile.class).getNewExtension(name, file);
    }

    /**
     * 录制视频
     *
     * @param folder       文件夹
     * @param outputStream 输出
     * @param videoConfig  配置
     */
    void record(File folder, OutputStream outputStream, VideoConfig videoConfig);

    /**
     * 录制视频
     *
     * @param folder       文件夹
     * @param outputStream 输出
     */
    default void record(File folder, OutputStream outputStream) {
        record(folder, outputStream, new VideoConfig());
    }

    /**
     * 录制视频
     *
     * @param folder 文件夹
     */
    default OutputStream record(File folder) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        record(folder, byteArrayOutputStream, new VideoConfig());
        return byteArrayOutputStream;
    }

    /**
     * 录制视频
     *
     * @param folder      文件夹
     * @param videoConfig 配置
     */
    default OutputStream record(File folder, VideoConfig videoConfig) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        record(folder, byteArrayOutputStream, videoConfig);
        return byteArrayOutputStream;
    }

    /**
     * 裁剪
     *
     * @param start        开始位置(s)
     * @param end          结束位置(s)
     * @param function     回调
     * @param outputStream 输出
     */
    void crop(long start, long end, OutputStream outputStream, BiFunction<LongAdder, BufferedImage, Boolean> function);

    /**
     * 截图
     *
     * @param function 回调
     */
    default void screenshot(BiFunction<LongAdder, BufferedImage, Boolean> function) {
        crop(0, -1, null, function);
    }

    /**
     * 裁剪
     *
     * @param start 开始位置(s)
     * @param end   结束位置(s)
     * @return outputStream 输出
     */
    default OutputStream crop(long start, long end) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        crop(start, end, byteArrayOutputStream, null);
        return byteArrayOutputStream;
    }

    /**
     * 转码
     *
     * @param videoType    视频类型
     * @param outputStream 输出
     * @param videoConfig  配置
     */
    void transfer(String videoType, OutputStream outputStream, VideoConfig videoConfig);


    /**
     * 转码
     *
     * @param videoType   视频类型
     * @param videoConfig 配置
     * @return outputStream 输出
     */
    default OutputStream transfer(String videoType, VideoConfig videoConfig) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        transfer(videoType, byteArrayOutputStream, videoConfig);
        return byteArrayOutputStream;
    }

    /**
     * 转码
     *
     * @param videoType 视频类型
     * @return outputStream 输出
     */
    default OutputStream transfer(String videoType) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        transfer(videoType, byteArrayOutputStream, new VideoConfig());
        return byteArrayOutputStream;
    }
}
