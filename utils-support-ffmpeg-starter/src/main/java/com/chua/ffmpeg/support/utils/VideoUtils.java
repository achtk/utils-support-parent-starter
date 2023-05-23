package com.chua.ffmpeg.support.utils;

import com.chua.common.support.lang.process.ProgressBar;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_AAC;
import static org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_H264;
import static org.bytedeco.ffmpeg.global.avutil.AV_LOG_ERROR;
import static org.bytedeco.ffmpeg.global.avutil.AV_PIX_FMT_YUV420P;

/**
 * 视频工具
 *
 * @author CH
 */
@Slf4j
public class VideoUtils {

    /**
     * mp4转gif动态图
     *
     * @param inputStream 輸入
     * @param width       寬度
     * @param height      高度
     * @param frameRate   帧率
     * @param output      输出
     */
    public static void transToGif(InputStream inputStream, Integer width, Integer height, Integer frameRate, OutputStream output) throws FileNotFoundException, Exception, FrameRecorder.Exception {
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputStream)) {
            grabber.start();

            if (width == null || height == null) {
                width = grabber.getImageWidth();
                height = grabber.getImageHeight();
            }

            //gif录制器
            try (FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(output, width, height, 0)) {
                //设置像素格式
                recorder.setPixelFormat(avutil.AV_PIX_FMT_RGB4_BYTE);
                //设置编码
                recorder.setVideoCodec(avcodec.AV_CODEC_ID_GIF);
                //设置帧率
                if (frameRate != null) {
                    recorder.setFrameRate(frameRate);
                }
                recorder.start();

                CanvasFrame canvas = new CanvasFrame("转换gif中屏幕预览");
                canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                canvas.setAlwaysOnTop(true);
                Frame frame = null;

                // 只抓取图像画面
                for (; (frame = grabber.grabImage()) != null; ) {
                    try {
                        //录制
                        recorder.record(frame);
                        //显示画面
                        canvas.showImage(frame);
                    } catch (FrameRecorder.Exception e) {
                        e.printStackTrace();
                    }
                }
                canvas.dispose();
            }
        }
    }

    /**
     * 关键帧
     *
     * @param file 输入
     * @return 输出
     * @throws Exception ex
     */
    public static List<BufferedImage> getKeyFrame(File file) throws Exception {
        avutil.av_log_set_level(AV_LOG_ERROR);
        List<BufferedImage> images = new LinkedList<>();
        try (
                Java2DFrameConverter converter = new Java2DFrameConverter();
        ) {
            FFmpegFrameGrabber grabberI = FFmpegFrameGrabber.createDefault(file);
            grabberI.start();
            // 帧总数
            BufferedImage bImg = null;
            log.info("总时长:" + grabberI.getLengthInTime() / 1000 / 60);
            log.info("音频帧数:" + grabberI.getLengthInAudioFrames());
            log.info("视频帧数:" + grabberI.getLengthInVideoFrames());
            log.info("总帧数:" + grabberI.getLengthInFrames());
            int frameNumber = grabberI.getLengthInVideoFrames() >= Integer.MAX_VALUE
                    ? 0
                    : grabberI.getLengthInVideoFrames();
            Frame img = null;
            grabberI.flush();
            for (int i = 0; i < frameNumber; i++) {
                if ((img = grabberI.grab()) == null) {
                    continue;
                }
                if ((bImg = converter.convert(img)) == null) {
                    continue;
                }

                images.add(copyImg(bImg));
            }
            grabberI.release();
        }
        return images;
    }


    /**
     * 图片
     *
     * @param img 图片
     * @return BufferedImage
     */
    public static BufferedImage copyImg(BufferedImage img) {
        BufferedImage checkImg =
                new BufferedImage(img.getWidth(), img.getHeight(), img.getType() == 0 ? 5 : img.getType());
        checkImg.setData(img.getData());
        return checkImg;
    }

    /**
     * 图片转视频
     *
     * @param file      源
     * @param outputDir 目标
     * @param fileType  视频类型
     * @throws Exception ex
     */
    public static void transTo(File file, File outputDir, String fileType) throws Exception {
        avutil.av_log_set_level(AV_LOG_ERROR);
        FFmpegFrameRecorder recorder = null;
        try (
                Java2DFrameConverter converter = new Java2DFrameConverter();
                OpenCVFrameConverter.ToIplImage toIplImage = new OpenCVFrameConverter.ToIplImage();
        ) {
            FFmpegFrameGrabber grabber = FFmpegFrameGrabber.createDefault(file);
            grabber.start();
            //
            // 帧总数
            int frameNumber = grabber.getLengthInFrames();
            ProgressBar progressBar = new ProgressBar("视频转图片: ", frameNumber);
            for (int i = 0; i < frameNumber; i++) {
                Frame frame = grabber.grab();

                if (frame == null) {
                    continue;
                }
                BufferedImage bImg = null;
                Buffer[] img = frame.image;

                if (img != null) {
                    if ((bImg = converter.convert(frame)) != null) {
                        ImageIO.write(bImg, fileType, new File(outputDir, i + "." + file));
                        progressBar.step();
                    }
                }
            }
            grabber.release();
        }
    }

    /**
     * 图片转视频
     *
     * @param file           源
     * @param outputFile     目标
     * @param bufferedImages 图片
     * @param fileType       视频类型
     * @throws Exception ex
     */
    public static void save(File file, File outputFile, List<BufferedImage> bufferedImages, String fileType) throws Exception {
        avutil.av_log_set_level(AV_LOG_ERROR);
        FFmpegFrameRecorder recorder = null;
        try (
                Java2DFrameConverter converter = new Java2DFrameConverter();
                OpenCVFrameConverter.ToIplImage toIplImage = new OpenCVFrameConverter.ToIplImage();
        ) {
            FFmpegFrameGrabber grabberI = FFmpegFrameGrabber.createDefault(file);
            grabberI.start();
            recorder = new FFmpegFrameRecorder(outputFile, grabberI.getImageWidth(), grabberI.getImageHeight(), 2);
            recorder.setVideoCodec(AV_CODEC_ID_H264);
            // 音频编/解码器
            recorder.setAudioCodec(AV_CODEC_ID_AAC);
            // 类型
            recorder.setFormat(fileType);
            recorder.setPixelFormat(AV_PIX_FMT_YUV420P);
            recorder.start();
            //
            // 帧总数
            BufferedImage bImg = null;
            log.info("总时长:" + grabberI.getLengthInTime() / 1000 / 60);
            log.info("音频帧数:" + grabberI.getLengthInAudioFrames());
            log.info("视频帧数:" + grabberI.getLengthInVideoFrames());
            log.info("总帧数:" + grabberI.getLengthInFrames());
            int audios =
                    grabberI.getLengthInAudioFrames() >= Integer.MAX_VALUE
                            ? 0
                            : grabberI.getLengthInAudioFrames();
            int vidoes =
                    grabberI.getLengthInVideoFrames() >= Integer.MAX_VALUE
                            ? 0
                            : grabberI.getLengthInVideoFrames();
            int frameNumber = audios + vidoes;
            int width = grabberI.getImageWidth();
            int height = grabberI.getImageHeight();
            int depth = 0;
            int channels = 0;
            int stride = 0;
            int index = 0;

            grabberI.flush();
            for (int i = 0; i < frameNumber; i++) {
                log.info("总共：" + frameNumber + " 完成：" + i);
                Frame frame = grabberI.grab();

                if (frame == null) {
                    continue;
                }
                Buffer[] smples = frame.samples;
                if (smples != null) {
                    recorder.recordSamples(smples);
                }
                Buffer[] img = frame.image;

                if (img != null) {
                    if ((bImg = converter.convert(frame)) != null) {
                        log.info("放入图片");
                        if (index >= bufferedImages.size()) {
                            break;
                        }
                        Mat face = MatUtils.bufferToMat(bufferedImages.get(index), opencv_core.CV_8UC3);
                        opencv_imgproc.resize(face, face, new Size(width, height));
                        Frame frame3 = toIplImage.convert(face);
                        img = frame3.image;
                        depth = frame3.imageDepth;
                        channels = frame3.imageChannels;
                        stride = frame3.imageStride;
                        index++;
                        recorder.recordImage(width, height, depth, channels, stride, -1, img);
                    }
                }
            }
            grabberI.release();
        } finally {
            recorder.close();
        }
    }

    /**
     * 多个视频的合并
     *
     * @param videoList 地址集合
     * @param output    合并后的视频输出地址
     */
    public static void videoMerge(List<String> videoList, String output) throws FrameRecorder.Exception, FrameGrabber.Exception {
        // 帧抓取器 以第一个视频为蓝本开始抓取
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoList.get(0))) {
            // 载入
            grabber.start();
            // 配置帧解码器
            try (FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(output, grabber.getImageWidth(),
                    grabber.getImageHeight(), 0)) {
                // 视频编解码器
                recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
                // 单通道
                recorder.setInterleaved(true);
                recorder.setAudioCodec(grabber.getAudioCodec());
                recorder.setAudioChannels(grabber.getAudioChannels());
                recorder.setAudioBitrate(grabber.getAudioBitrate());
                recorder.setAudioMetadata(grabber.getAudioMetadata());
                recorder.setAudioOptions(grabber.getAudioOptions());
                // 输出流封装格式
                recorder.setFormat("mp4");
                recorder.setVideoCodec(grabber.getVideoCodec());
                recorder.setVideoMetadata(grabber.getVideoMetadata());
                recorder.setVideoOptions(grabber.getVideoOptions());
                // 视频帧率
                recorder.setFrameRate(grabber.getFrameRate());
                // 设置日志输出（error）
                avutil.av_log_set_level(AV_LOG_ERROR);
                // 设置分辨率格式 yuv420p
                recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
                // 视频比特率
                int bitrate = grabber.getVideoBitrate();
                if (bitrate == 0) {
                    // 音频比特率
                    bitrate = grabber.getAudioBitrate();
                }
                // 设置视频比特率
                recorder.setVideoBitrate(bitrate);
                // 开始编码
                recorder.start();
                // 设置帧
                Frame frame;
                // 循环设置
                for (String s : videoList) {
                    try (FFmpegFrameGrabber grabberTemp = new FFmpegFrameGrabber(s)) {
                        grabberTemp.start();
                        while ((frame = grabberTemp.grab()) != null) {
                            // 添加并编码帧
                            recorder.record(frame);
                        }
                        // 关闭当前视频帧抓取器
                    }
                }
                // 关闭帧编解码器
            }
        }
    }

    public static void main(String[] args) throws FrameRecorder.Exception, FrameGrabber.Exception {
        List<String> file = new ArrayList<>();
        file.add("Z://1/dcb21aef-cf56-4f4b-9238-f52ac3e292e4.mp4");
        file.add("Z://1/f43a9065-eec1-4339-bf0f-267850e9c6ac.mp4");
        long start = System.currentTimeMillis();

        videoMerge(file, "Z://1/" + File.separator + UUID.randomUUID() + ".MP4");
        System.out.println("耗时" + (System.currentTimeMillis() - start) + "毫秒");


    }
}
