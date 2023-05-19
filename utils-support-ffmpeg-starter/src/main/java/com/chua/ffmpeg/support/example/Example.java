package com.chua.ffmpeg.support.example;

import com.chua.common.support.spi.ServiceProvider;
import com.chua.ffmpeg.support.video.VideoFile;

import java.io.IOException;
import java.nio.file.Paths;

public class Example {

    public static void main(String[] args) throws IOException {
        VideoFile video = ServiceProvider.of(VideoFile.class).getNewExtension("ffmpeg", "ffmpeg", "Z:\\works\\resource\\1665669595.mp4");
//        video.transTo(new File("Z:\\gif"), "png");

        video.crop(2, 8, Paths.get("Z:\\gif\\crop.mp4").toFile(), null);
        System.out.println();
    }
}
