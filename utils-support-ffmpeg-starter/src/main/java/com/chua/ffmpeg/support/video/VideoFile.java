package com.chua.ffmpeg.support.video;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BiFunction;

/**
 * 视频
 *
 * @author CH
 */
public interface VideoFile {
    /**
     * 輸出每一帧
     *
     * @param output   输出目录
     * @param fileType 类型
     * @throws IOException ex
     */
    void transTo(File output, String fileType) throws IOException;


    /**
     * 裁剪
     *
     * @param start    开始位置(s)
     * @param end      结束位置(s)
     * @param file     输出
     * @param function 回调
     */
    void crop(long start, long end, File file, BiFunction<LongAdder, BufferedImage, Boolean> function) throws IOException;
}
