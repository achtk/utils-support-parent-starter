package com.chua.ffmpeg.support.subject;

import com.chua.common.support.file.transfer.AbstractFileConverter;
import lombok.SneakyThrows;
import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * video -> video
 *
 * @author CH
 * @since 2022-01-19
 */
public class VideoToVideoConverter extends AbstractFileConverter {

    @SneakyThrows
    @Override
    public void convert(String type, InputStream sourcePath, String suffix, OutputStream targetPath) {
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(sourcePath);
        grabber.start();

        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(targetPath, grabber.getImageWidth(),
                grabber.getImageHeight(), grabber.getAudioChannels());

        recorder.setVideoCodec(grabber.getVideoCodec());
        recorder.setVideoBitrate(grabber.getVideoBitrate());
        recorder.setVideoMetadata(grabber.getVideoMetadata());
        recorder.setVideoOptions(grabber.getVideoOptions());

        recorder.setAudioOptions(grabber.getAudioOptions());
        recorder.setAudioMetadata(grabber.getAudioMetadata());
        recorder.setAudioBitrate(grabber.getAudioBitrate());
        recorder.setAudioChannels(grabber.getAudioChannels());

        recorder.setFormat(suffix);
        recorder.setPixelFormat(grabber.getPixelFormat());
        recorder.setFrameRate(grabber.getFrameRate());
        recorder.start(grabber.getFormatContext());

        AVPacket packet;
        long dts = 0;
        while ((packet = grabber.grabPacket()) != null) {
            long currentDts = packet.dts();
            if (currentDts >= dts) {
                recorder.recordPacket(packet);
            }
            dts = currentDts;
        }
        recorder.stop();
        recorder.release();
        grabber.stop();
        grabber.release();
    }

    @Override
    public String target() {
        return DEFAULT_VIDEO;
    }

    @Override
    public String source() {
        return DEFAULT_VIDEO;
    }

}
