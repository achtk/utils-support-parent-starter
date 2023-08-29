package com.chua.common.support.oss.adaptor;

import com.chua.common.support.function.Splitter;
import com.chua.common.support.function.strategy.name.OssNamedStrategy;
import com.chua.common.support.function.strategy.name.RejectStrategy;
import com.chua.common.support.image.filter.ImageFilter;
import com.chua.common.support.media.MediaType;
import com.chua.common.support.media.MediaTypeFactory;
import com.chua.common.support.oss.preview.Preview;
import com.chua.common.support.pojo.Mode;
import com.chua.common.support.pojo.OssSystem;
import com.chua.common.support.range.Range;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.StringUtils;

import java.io.*;
import java.util.Optional;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_COMMA;

/**
 * oss
 *
 * @author CH
 */
public abstract class AbstractOssResolver implements OssResolver {


    private static final String IMAGE = "image";

    /**
     * 命名策略
     *
     * @param ossSystem preview
     * @param name      name
     * @param bytes     bytes
     * @return 拒絕策略
     */
    public static String getNamedStrategy(OssSystem ossSystem, String name, byte[] bytes) {
        String ossNameStrategy = ossSystem.getOssNameStrategy();
        OssNamedStrategy ossNamedStrategy = ServiceProvider.of(OssNamedStrategy.class).getExtension(ossNameStrategy);
        return ossNamedStrategy.named(name, bytes);

    }

    /**
     * 拒绝策略
     *
     * @param ossSystem ossSystem
     * @return 拒绝策略
     */
    public byte[] reject(OssSystem ossSystem) {
        String ossRejectStrategy = ossSystem.getOssRejectStrategy();
        RejectStrategy rejectStrategy = ServiceProvider.of(RejectStrategy.class).getExtension(ossRejectStrategy);
        return rejectStrategy.reject(ossSystem);
    }

    /**
     * 区间寻址
     *
     * @param inputStream  输入
     * @param ossSystem    ossSystem
     * @param range        区间
     * @param outputStream 输出
     */
    protected void writeRangeToOutStream(Object inputStream, OssSystem ossSystem, Range<Long> range, ByteArrayOutputStream outputStream) throws Exception {
        if (inputStream instanceof RandomAccessFile) {
            rangeRandomAccessFile((RandomAccessFile) inputStream, ossSystem, range, outputStream);
            return;
        }

        if (inputStream instanceof InputStream) {
            rangeInputStream((InputStream) inputStream, ossSystem, range, outputStream);
        }
    }

    private void rangeInputStream(InputStream inputStream, OssSystem ossSystem, Range<Long> range, ByteArrayOutputStream outputStream) throws Exception {
        byte[] bytes = new byte[null == ossSystem.getOssBuffer() ? 4096 : ossSystem.getOssBuffer()];
        int read = -1;
        int cnt = 0;
        Long aLong = range.upperEndpoint();
        try (InputStream stream = inputStream) {
            stream.skip(range.lowerEndpoint());
            while ((read = stream.read(bytes)) != -1) {
                cnt += read;
                if (cnt <= aLong) {
                    outputStream.write(bytes, 0, read);
                } else {
                    long l = aLong - cnt;
                    if (l == 0) {
                        break;
                    }
                    outputStream.write(bytes, 0, (int) l);
                }
            }
        }
    }

    private void rangeRandomAccessFile(RandomAccessFile inputStream, OssSystem ossSystem, Range<Long> range, ByteArrayOutputStream outputStream) throws Exception {
        byte[] bytes = new byte[null == ossSystem.getOssBuffer() ? 4096 : ossSystem.getOssBuffer()];
        int read = -1;
        int cnt = 0;
        Long aLong = range.upperEndpoint();
        try (RandomAccessFile randomAccessFile = inputStream) {
            randomAccessFile.seek(range.lowerEndpoint());
            while ((read = randomAccessFile.read(bytes)) != -1) {
                cnt += read;
                if (cnt <= aLong) {
                    outputStream.write(bytes, 0, read);
                } else {
                    long l = aLong - cnt;
                    if (l == 0) {
                        break;
                    }
                    outputStream.write(bytes, 0, (int) l);
                }
            }
        }
    }

    /**
     * 成功
     *
     * @param mediaType mediaType
     * @param mode      模式
     * @param range     区间
     * @param bytes     输出
     * @param os        流
     * @param ossSystem 配置
     */
    protected void writeTo(MediaType mediaType, Mode mode, Range<Long> range, byte[] bytes, OutputStream os, OssSystem ossSystem) {

        try {
            if (mode == Mode.DOWNLOAD && null != range) {
                os.write(bytes);
                return;
            }

            String ossPlugins = ossSystem.getOssPlugins();
            if (null == mediaType || !mediaType.type().startsWith(IMAGE) || StringUtils.isBlank(ossPlugins)) {
                os.write(bytes);
                os.flush();
                return;
            }
            String format = mediaType.subtype();
            renderPlugin(format, ossPlugins, bytes, os);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 渲染插件
     *
     * @param format     图片格式
     * @param ossPlugins 插件
     * @param bytes      输出
     * @param os         流
     */
    private void renderPlugin(String format, String ossPlugins, byte[] bytes, OutputStream os) {
        for (String name : Splitter.on(SYMBOL_COMMA).trimResults().omitEmptyStrings().splitToList(ossPlugins)) {
            ImageFilter imageFilter = ServiceProvider.of(ImageFilter.class).getNewExtension(name);
            if (null == imageFilter) {
                imageFilter = ServiceProvider.of(ImageFilter.class).getNewExtension("com.chua.common.support.image.filter." + name + "Filter");
            }

            if (null == imageFilter) {
                continue;
            }

            imageFilter.getImageFormat(format);
            byte[] stream1 = converter(imageFilter, bytes);
            if (null == stream1) {
                break;
            }

            bytes = stream1;
        }

        try {
            os.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] converter(ImageFilter imageFilter, byte[] bufferedImage) {
        if (null == bufferedImage) {
            return null;
        }

        try (ByteArrayInputStream bis = new ByteArrayInputStream(bufferedImage);
             ByteArrayOutputStream converter = (ByteArrayOutputStream) imageFilter.converter(bis);
        ) {
            return converter.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 拒绝
     *
     * @param bytes 输出
     * @param os    流
     */
    protected void writeToReject(byte[] bytes, OutputStream os) {
        try {
            os.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected Preview contentType(Preview preview) {
        Optional<MediaType> mediaType = MediaTypeFactory.getMediaType(preview.getPath());
        mediaType.ifPresent(preview::setContentType);
        return preview;
    }

}
