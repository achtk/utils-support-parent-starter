package com.chua.ffmpeg.support.video;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.binary.ByteSource;
import com.chua.common.support.binary.ByteSourceArray;
import com.chua.common.support.binary.ByteSourceFile;
import com.chua.common.support.lang.process.ProgressBar;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BiFunction;

import static org.bytedeco.ffmpeg.global.avutil.AV_LOG_ERROR;

/**
 * 视频
 *
 * @author CH
 */
@Spi("ffmpeg")
public class FfmpegVideoFile implements VideoFile {

    private ByteSource byteSource;

    public FfmpegVideoFile(ByteSource byteSource) {
        this.byteSource = byteSource;
    }

    public FfmpegVideoFile(InputStream inputStream) throws IOException {
        this(new ByteSourceArray(IOUtils.toByteArray(inputStream)));
    }

    public FfmpegVideoFile(File file) {
        this(new ByteSourceFile(file));
    }


    @Override
    public void transTo(File output, String fileType) throws IOException {
        FileUtils.forceMkdir(output);
        avutil.av_log_set_level(AV_LOG_ERROR);
        try (
                Java2DFrameConverter converter = new Java2DFrameConverter();
                FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(byteSource.getInputStream());
        ) {
            grabber.start();
            //
            // 帧总数
            int frameNumber = grabber.getLengthInFrames();
            try (ProgressBar progressBar = new ProgressBar("视频转图片: ", frameNumber)) {
                for (int i = 0; i < frameNumber; i++) {
                    progressBar.step();
                    Frame frame = grabber.grab();

                    if (frame == null) {
                        continue;
                    }
                    BufferedImage bImg = null;
                    Buffer[] img = frame.image;

                    if (img != null) {
                        if ((bImg = converter.convert(frame)) != null) {
                            ImageIO.write(bImg, fileType, new File(output, i + "." + fileType));
                        }
                    }
                }
            }
            grabber.release();
        }
    }

    @Override
    public void crop(long start, long end, File file, BiFunction<LongAdder, BufferedImage, Boolean> function) throws IOException {
        FFmpegLogCallback.setLevel(1);
        //抓帧器
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(byteSource.getInputStream());
             Java2DFrameConverter converter = new Java2DFrameConverter();
             FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(file, grabber.getImageWidth(), grabber.getImageHeight(), grabber.getAudioChannels());
        ) {
            grabber.setCloseInputStream(true);
            grabber.start();

            // 重新设置 宽高
            recorder.setImageWidth(grabber.getImageWidth());
            recorder.setImageHeight(grabber.getImageHeight());

            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
            recorder.setFormat("mp4");
            recorder.setFrameRate(grabber.getFrameRate());
            recorder.setSampleRate(grabber.getSampleRate());
            recorder.setFrameRate(grabber.getFrameRate());

            recorder.setAudioCodec(grabber.getAudioCodec());
            recorder.setAudioChannels(grabber.getAudioChannels());
            recorder.setCloseOutputStream(true);

            recorder.start();

            double frameRate = grabber.getFrameRate();
            int lengthInFrames = grabber.getLengthInFrames();

            double startRate = Math.max(frameRate * start, 0);
            double endRate = end == -1 ? lengthInFrames : Math.min(frameRate * end, lengthInFrames);
            LongAdder longAdder = new LongAdder();

            Frame frame;
            int count = 0;
            while ((frame = grabber.grab()) != null) {
                double doubleValue = longAdder.doubleValue();
                longAdder.increment();
                if (startRate >= doubleValue && doubleValue <= endRate) {
                    recorder.setTimestamp(grabber.getTimestamp());
                    recorder.record(frame);

                    if (null != function) {
                        Boolean aBoolean = function.apply(longAdder, converter.convert(frame));
                        if (null != aBoolean && aBoolean) {
                            break;
                        }
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
