package com.chua.ffmpeg.support;

import com.chua.common.support.binary.ByteSource;
import com.chua.common.support.binary.ByteSourceArray;
import com.chua.common.support.file.*;
import com.chua.common.support.resource.ResourceConfiguration;
import com.chua.common.support.utils.NumberUtils;
import lombok.SneakyThrows;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BiFunction;

/**
 * 视频文件
 *
 * @author CH
 */
public class FfmpegVideoFile extends AbstractResourceFile implements VideoFile, StreamFile {
    protected final ResourceFileConfiguration resourceConfiguration;

    private final ByteSource byteSource;
    final Java2DFrameConverter converter = new Java2DFrameConverter();

    public FfmpegVideoFile(ResourceFileConfiguration resourceConfiguration) {
        super(resourceConfiguration);
        this.resourceConfiguration = resourceConfiguration;
        try {
            this.byteSource = new ByteSourceArray(openInputStream());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取流
     *
     * @return 流
     */
    @SneakyThrows
    private InputStream getInputStream() {
        return byteSource.getInputStream();
    }


    @Override
    public void record(File folder, OutputStream outputStream, VideoConfig videoConfig) {
        if (null == folder || folder.isFile()) {
            return;
        }

        File[] files = folder.listFiles();
        if (null == files) {
            return;
        }

        FFmpegFrameRecorder recorder = null;

        try {
            //获取高度
            int height = NumberUtils.isZero(videoConfig.height(), 100);
            //获取宽度
            int width = NumberUtils.isZero(videoConfig.width(), 100);

            recorder = new FFmpegFrameRecorder(outputStream, width, height, 3);
            recorder.setFrameRate(NumberUtils.isZero(videoConfig.frameRate(), 0));
            if (null == videoConfig.option()) {
                recorder.setVideoOption("preset", "veryfast");
            } else {
                recorder.setVideoOptions(videoConfig.option());
            }
            // yuv420p,像素
            recorder.setFormat("mp4");
            recorder.setCloseOutputStream(true);
            recorder.start();

            for (File file : files) {
                BufferedImage bufferedImage = null;
                try {
                    bufferedImage = ImageIO.read(file);
                } catch (IOException ignored) {
                }
                if (null == bufferedImage) {
                    continue;
                }
                Frame frame = converter.convert(bufferedImage);
                if (null == frame) {
                    continue;
                }
                recorder.record(frame);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != recorder) {
                try {
                    recorder.stop();
                    recorder.release();
                } catch (FFmpegFrameRecorder.Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void crop(long start, long end, OutputStream outputStream, BiFunction<LongAdder, BufferedImage, Boolean> function) {
        //抓帧器
        FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(getInputStream());
        //帧记录器
        FFmpegFrameRecorder recorder = null;
        //帧
        Frame frame = null;
        try {
            frameGrabber.start();
            //获取高度
            int height = frameGrabber.getImageHeight();
            //获取宽度
            int width = frameGrabber.getImageWidth();

            if (null != outputStream) {
                recorder = new FFmpegFrameRecorder(outputStream, width, height, frameGrabber.getAudioChannels());

                recorder.setFrameRate(frameGrabber.getFrameRate());
                recorder.setSampleRate(frameGrabber.getSampleRate());
                recorder.setFormat(frameGrabber.getFormat());
                recorder.setAudioCodec(frameGrabber.getAudioCodec());
                recorder.setAudioCodecName(frameGrabber.getAudioCodecName());
                recorder.setVideoCodecName(frameGrabber.getVideoCodecName());
                recorder.setVideoCodec(frameGrabber.getVideoCodec());
                recorder.setVideoBitrate(frameGrabber.getVideoBitrate());
                recorder.setCloseOutputStream(true);
                recorder.start();
            }

            double frameRate = frameGrabber.getFrameRate();
            int lengthInFrames = frameGrabber.getLengthInFrames();

            double startRate = Math.max(frameRate * start, 0);
            double endRate = end == -1 ? lengthInFrames : Math.min(frameRate * end, lengthInFrames);

            LongAdder longAdder = new LongAdder();
            while (true) {
                try {
                    frame = frameGrabber.grabFrame();
                    if (null == frame) {
                        break;
                    }

                    double doubleValue = longAdder.doubleValue();
                    if (startRate >= doubleValue && doubleValue <= endRate) {
                        if (null != outputStream) {
                            recorder.setTimestamp(frameGrabber.getTimestamp());
                            recorder.record(frame);
                        }
                        if (null != function) {
                            Boolean aBoolean = function.apply(longAdder, converter.convert(frame));
                            if (null != aBoolean && aBoolean) {
                                break;
                            }
                        }
                    }
                    longAdder.increment();

                } catch (Exception ignored) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != recorder) {
                try {
                    recorder.stop();
                    recorder.release();
                } catch (FFmpegFrameRecorder.Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                frameGrabber.stop();
                frameGrabber.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void transfer(String videoType, OutputStream outputStream, VideoConfig videoConfig) {
        //抓帧器
        FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(getInputStream());
        //帧记录器
        FFmpegFrameRecorder recorder = null;
        //帧
        Frame frame = null;
        try {
            frameGrabber.start();
            //获取高度
            int height = NumberUtils.isZero(videoConfig.height(), frameGrabber.getImageHeight());
            //获取宽度
            int width = NumberUtils.isZero(videoConfig.width(), frameGrabber.getImageWidth());

            recorder = new FFmpegFrameRecorder(outputStream, width, height, frameGrabber.getAudioChannels());
            recorder.setFrameRate(NumberUtils.isZero(videoConfig.frameRate(), frameGrabber.getFrameRate()));
            recorder.setSampleRate(frameGrabber.getSampleRate());
            if (null == videoConfig.option()) {
                recorder.setVideoOption("preset", "veryfast");
            } else {
                recorder.setVideoOptions(videoConfig.option());
            }
            // yuv420p,像素
            recorder.setPixelFormat(NumberUtils.isZero(videoConfig.pixelFormat(), avutil.AV_PIX_FMT_YUV420P));
            recorder.setVideoCodec(NumberUtils.isZero(videoConfig.videoCodec(), avcodec.AV_CODEC_ID_H264));
            recorder.setAudioCodec(NumberUtils.isZero(videoConfig.audioCodec(), avcodec.AV_CODEC_ID_AAC));
            recorder.setFormat(videoType);
            recorder.setCloseOutputStream(true);
            recorder.setVideoBitrate(NumberUtils.isZero(videoConfig.videoBitrate(), frameGrabber.getVideoBitrate()));
            recorder.start();

            while (true) {
                try {
                    frame = frameGrabber.grabFrame();
                    if (null == frame) {
                        break;
                    }
                    recorder.setTimestamp(frameGrabber.getTimestamp());
                    recorder.record(frame);
                } catch (Exception ignored) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != recorder) {
                try {
                    recorder.stop();
                    recorder.release();
                } catch (FFmpegFrameRecorder.Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                frameGrabber.stop();
                frameGrabber.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
