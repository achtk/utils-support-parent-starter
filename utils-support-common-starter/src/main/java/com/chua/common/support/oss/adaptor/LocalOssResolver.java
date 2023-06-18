package com.chua.common.support.oss.adaptor;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.annotations.SpiOption;
import com.chua.common.support.media.MediaType;
import com.chua.common.support.media.MediaTypeFactory;
import com.chua.common.support.pojo.Mode;
import com.chua.common.support.pojo.OssSystem;
import com.chua.common.support.range.Range;
import com.chua.common.support.utils.FileUtils;
import com.chua.common.support.utils.IoUtils;
import com.chua.common.support.utils.StringUtils;
import com.chua.common.support.value.Value;

import java.io.*;
import java.util.Optional;

/**
 * 本地
 */
@Spi("local")
@SpiOption("本地")
public class LocalOssResolver extends AbstractOssResolver {

    @Override
    public Value<String> storage(String parentPath, byte[] is, OssSystem ossSystem, String name) {
        String real = StringUtils.defaultString(ossSystem.getOssBucket(), "") + "/" + name;
        File file = new File(ossSystem.getOssPath() + "/" + parentPath, real);
        FileUtils.mkParentDirs(file);

        if (ossSystem.getOssCovering()) {
            if (file.exists()) {
                try {
                    FileUtils.delete(file);
                } catch (IOException ignored) {
                }
            }
        }

        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            IoUtils.write(is, outputStream);
            return Value.of(real);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Value.of(null);
    }

    @Override
    public void preview(OssSystem ossSystem, String path, Mode mode, Range<Long> range, OutputStream os) {
        File file = findFile(ossSystem, path);
        if (isValidFile(file)) {
            writeToReject(reject(ossSystem), os);
            return;
        }

        byte[] ra = null;
        byte[] bytes = new byte[null == ossSystem.getOssBuffer() ? 4096 : ossSystem.getOssBuffer()];
        int read = -1;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            if (mode == Mode.DOWNLOAD && null != range) {
                try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
                    rangeRead(randomAccessFile, ossSystem, range, byteArrayOutputStream);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                try (FileInputStream fis = new FileInputStream(file)) {
                    while ((read = fis.read(bytes)) != -1) {
                        byteArrayOutputStream.write(bytes, 0, read);
                    }
                } catch (Exception ignored) {
                }
            }
            Optional<MediaType> mediaType = MediaTypeFactory.getMediaType(path);
            writeTo(mediaType.get(), mode, range, byteArrayOutputStream.toByteArray(), os, ossSystem);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 是否为无效文件
     *
     * @param file 文件
     * @return 是否为无效文件
     */
    private boolean isValidFile(File file) {
        return null == file || !file.exists();
    }

    private File findFile(OssSystem ossSystem, String path) {
        String ossPath = ossSystem.getOssPath() + "/" + ossSystem.getOssBucket();
        if (StringUtils.isBlank(ossPath)) {
            return null;
        }
        return new File(ossPath, path);
    }
}
