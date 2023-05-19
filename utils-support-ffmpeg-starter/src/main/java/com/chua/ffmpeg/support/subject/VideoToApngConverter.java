package com.chua.ffmpeg.support.subject;

import com.chua.common.support.converter.Converter;
import com.chua.common.support.file.transfer.AbstractFileConverter;
import lombok.SneakyThrows;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;

import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * video -> gif
 *
 * @author CH
 * @since 2022-01-19
 */
public class VideoToApngConverter extends AbstractFileConverter {

    @SneakyThrows
    @Override
    public void convert(InputStream inputStream, File output) throws Exception {
        FFmpegFrameGrabber fFmpegFrameGrabber = new FFmpegFrameGrabber(inputStream);
        fFmpegFrameGrabber.start();
        List<BufferedImage> bufferedImageList = new LinkedList<>();
        int interval = getIntValue("interval", 1);
        int cnt = 0;
        int delay = getIntValue("frameRate", 2);

        //apng录制器
        try (FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(output, fFmpegFrameGrabber.getImageWidth(), fFmpegFrameGrabber.getImageHeight(), 0)) {
            //设置像素格式
            recorder.setPixelFormat(avutil.AV_PIX_FMT_RGBA);
            //设置录制的视频/图片编码
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_APNG);
            if (delay > 0) {
                recorder.setFrameRate(delay);//设置帧率
            }
            recorder.start();

            while (true) {
                Frame frame = fFmpegFrameGrabber.grabImage();
                if (null == frame) {
                    break;
                }

                if (cnt++ % interval == 0) {
                    //录制
                    recorder.record(frame);
                }
            }
        }

    }


    protected IIOMetadata getMetaData(ImageWriter imageWriter, ImageWriteParam writeParam) {
        return imageWriter.getDefaultStreamMetadata(writeParam);
    }

    @Override
    public String target() {
        return "apng";
    }

    @Override
    public String source() {
        return DEFAULT_VIDEO;
    }

    @Override
    public void convert(InputStream inputStream, String suffix, OutputStream outputStream) throws Exception {
        if(outputStream instanceof FileOutputStream) {
            convert(inputStream, Converter.convertIfNecessary(outputStream, File.class));
        }
    }
}
