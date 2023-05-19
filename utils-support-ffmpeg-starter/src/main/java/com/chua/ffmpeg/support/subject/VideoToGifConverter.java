package com.chua.ffmpeg.support.subject;

import com.chua.common.support.file.transfer.AbstractFileConverter;
import com.chua.common.support.lang.process.ProgressBar;
import lombok.SneakyThrows;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import java.awt.*;
import java.awt.image.BufferedImage;
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
public class VideoToGifConverter extends AbstractFileConverter {

    private static final Java2DFrameConverter CONVERTER = new Java2DFrameConverter();

    @SneakyThrows
    @Override
    public void convert(String type, InputStream sourcePath, String suffix, OutputStream targetPath) {
        FFmpegFrameGrabber fFmpegFrameGrabber = new FFmpegFrameGrabber(sourcePath);
        fFmpegFrameGrabber.start();
        List<BufferedImage> bufferedImageList = new LinkedList<>();
        int interval = getIntValue("interval", 1);
        int cnt = 0;
        while (true) {
            Frame frame = fFmpegFrameGrabber.grabImage();
            if (null == frame) {
                break;
            }

            if (cnt++ % interval == 0) {
                BufferedImage bufferedImage = CONVERTER.convert(frame);
                if (null != bufferedImage) {
                    bufferedImageList.add(bufferedImage);
                }
            }
        }

        try (ProgressBar progressBar = new ProgressBar(bufferedImageList.size())) {

            com.chua.common.support.protocol.image.gif.GifEncoder encoder = new com.chua.common.support.protocol.image.gif.GifEncoder();
            encoder.start(targetPath);
            encoder.setQuality(getIntValue("quality", 15));
            encoder.setRepeat(getIntValue("repeat", 0));
            encoder.setDelay(getIntValue("delay", 500));
            for (BufferedImage bufferedImage : bufferedImageList) {
                int height = bufferedImage.getHeight();
                int width = bufferedImage.getWidth();
                BufferedImage zoomImage = new BufferedImage(width, height, 3);
                Image image = bufferedImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                Graphics gc = zoomImage.getGraphics();
                gc.setColor(Color.WHITE);
                gc.drawImage(image, 0, 0, null);
                encoder.addFrame(zoomImage);
                progressBar.step();
            }
            encoder.finish();
        }

    }


    protected IIOMetadata getMetaData(ImageWriter imageWriter, ImageWriteParam writeParam) {
        return imageWriter.getDefaultStreamMetadata(writeParam);
    }

    @Override
    public String target() {
        return "gif";
    }

    @Override
    public String source() {
        return DEFAULT_VIDEO;
    }
}
