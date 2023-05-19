package com.chua.ffmpeg.support.subject;

import com.chua.common.support.file.transfer.AbstractFileConverter;
import com.chua.common.support.lang.exception.NotSupportedException;
import com.chua.common.support.lang.process.ProgressBar;
import com.chua.common.support.reflection.FieldStation;
import com.chua.common.support.utils.BufferedImageUtils;
import com.chua.common.support.utils.FileUtils;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FFmpegLogCallback;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;

import static com.chua.common.support.constant.CommonConstant.FILE_URL_PREFIX;

/**
 * video -> png
 *
 * @author CH
 * @since 2022-01-19
 */
public class FolderToVideoConverter extends AbstractFileConverter {

    private static final Java2DFrameConverter CONVERTER = new Java2DFrameConverter();

    @Override
    public void convert(File sourcePath, String suffix, OutputStream targetPath) {
        //录制器
        FFmpegFrameRecorder fFmpegFrameRecorder = null;
        //帧
        Frame frame = null;
        String type = getString("type");
        double frameRate = getDoubleValue("frameRate", 25);
        int repeat = getIntValue("repeat", 1);
        int width = getIntValue("width", 1600);
        int height = getIntValue("height", 900);
        String string = Optional.ofNullable(FieldStation.of(targetPath).getValue("path")).orElse("mp4").toString();
        suffix = FileUtils.getExtension(string);
        try {
            avutil.av_log_set_level(avutil.AV_LOG_ERROR);
            FFmpegLogCallback.set();
            fFmpegFrameRecorder = new FFmpegFrameRecorder(targetPath, width, height);
            fFmpegFrameRecorder.setFormat(suffix);
            //设置视频编码层模式
            fFmpegFrameRecorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
            //设置视频为25帧每秒
            fFmpegFrameRecorder.setFrameRate(frameRate);
            //设置视频图像数据格式
            fFmpegFrameRecorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);


            fFmpegFrameRecorder.start();
            File[] files = sourcePath.listFiles();
            if (null == files) {
                return;
            }

            try (ProgressBar progressBar = new ProgressBar(files.length)) {
                for (File file1 : files) {
                    progressBar.step();
                    if (null != type) {
                        String extension = FileUtils.getExtension(file1);
                        if (!type.contains(extension)) {
                            continue;
                        }
                    }
                    try {
                        frame = CONVERTER.getFrame(BufferedImageUtils.getBufferedImage(file1));
                        for (int i = 0; i < repeat; i++) {
                            fFmpegFrameRecorder.record(frame);
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != fFmpegFrameRecorder) {
                try {
                    fFmpegFrameRecorder.stop();
                    fFmpegFrameRecorder.release();
                    fFmpegFrameRecorder.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void convert(String type,InputStream inputStream, String suffix, OutputStream outputStream) {
        throw new NotSupportedException();
    }

    @Override
    public String target() {
        return DEFAULT_VIDEO;
    }

    @Override
    public String source() {
        return FILE_URL_PREFIX;
    }

}
