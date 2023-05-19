package com.chua.ffmpeg.support.subject;

import com.chua.common.support.file.transfer.AbstractFileConverter;
import com.chua.common.support.lang.exception.NotSupportedException;
import com.chua.common.support.utils.FileUtils;
import lombok.SneakyThrows;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import static com.chua.common.support.constant.CommonConstant.FILE_URL_PREFIX;

/**
 * video -> pic
 *
 * @author CH
 * @since 2022-01-19
 */
public class VideoToFolderConverter extends AbstractFileConverter {

    private static final Java2DFrameConverter CONVERTER = new Java2DFrameConverter();

    @Override
    public void convert(String type, InputStream inputStream, String suffix, OutputStream outputStream) {
        throw new NotSupportedException();
    }

    @SneakyThrows
    public void convert(InputStream sourcePath, File targetPath) {
        String type = getString("type", "png");

        FFmpegFrameGrabber fFmpegFrameGrabber = new FFmpegFrameGrabber(sourcePath);
        fFmpegFrameGrabber.start();

        FileUtils.forceMkdir(targetPath);
        while (true) {
            Frame frame = fFmpegFrameGrabber.grabImage();
            if (null == frame) {
                break;
            }

            BufferedImage bufferedImage = CONVERTER.convert(frame);
            if (null != bufferedImage) {
                ImageIO.write(bufferedImage, type, new File(targetPath, frame.timestamp + "." + type));
            }
        }
    }

    @Override
    public String target() {
        return FILE_URL_PREFIX;
    }

    @Override
    public String source() {
        return DEFAULT_VIDEO;
    }

}
