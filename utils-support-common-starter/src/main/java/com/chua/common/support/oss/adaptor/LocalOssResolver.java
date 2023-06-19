package com.chua.common.support.oss.adaptor;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.annotations.SpiOption;
import com.chua.common.support.lang.date.DateTime;
import com.chua.common.support.media.MediaType;
import com.chua.common.support.media.MediaTypeFactory;
import com.chua.common.support.oss.node.OssNode;
import com.chua.common.support.pojo.Mode;
import com.chua.common.support.pojo.OssSystem;
import com.chua.common.support.range.Range;
import com.chua.common.support.utils.FileUtils;
import com.chua.common.support.utils.IoUtils;
import com.chua.common.support.utils.StringUtils;
import com.chua.common.support.value.Value;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

/**
 * 本地
 */
@Spi("local")
@SpiOption("本地")
public class LocalOssResolver extends AbstractOssResolver {

    @Override
    public Value<String> storage(String parentPath, byte[] is, OssSystem ossSystem, String name) {
        File file = new File(ossSystem.getOssPath() + "/" + parentPath, name);
        FileUtils.mkParentDirs(file);

        if(file.exists()) {
            if(!ossSystem.getOssCovering()) {
                throw new RuntimeException("文件已存在");
            }
            try {
                FileUtils.delete(file);
            } catch (IOException ignored) {
            }
        }

        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            IoUtils.write(is, outputStream);
            return Value.of(name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Value.of(null);
    }

    @Override
    public List<OssNode> getChildren(OssSystem ossSystem, String id, String name) {
        String ossPath = ossSystem.getOssPath().replace("\\", "/");
        File file = new File(ossPath, name);
        if(!file.exists()) {
            return Collections.emptyList();
        }
        File[] files = file.listFiles();
        if(null == files || files.length == 0) {
            return Collections.emptyList();
        }

        return Arrays.stream(files).map(it -> {
            File[] files1 = it.listFiles();
            return new OssNode(
                    name + "/" + it.getName(),
                    name + "/" + it.getName(),
                    it.getName(),
                    DateTime.of(it.lastModified()).toLocalDateTime(),
                                it.isFile(),
                   null != files1 && files1.length != 0
            );
        }).collect(Collectors.toList());
    }

    @Override
    public Boolean deleteObject(OssSystem ossSystem, String id, String name) {
        String ossPath = ossSystem.getOssPath().replace("\\", "/");
        File file = new File(ossPath, name);
        if(!file.exists()) {
            throw new RuntimeException("文件/文件夹不存在");
        }
        try {
            if(file.isFile()) {
                file.delete();
            } else {
                FileUtils.forceDeleteDirectory(file);
            }
        } catch (Exception ignored) {
            throw new RuntimeException("删除文件/文件夹不存在");
        }
        return true;
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
                    writeRangeToOutStream(randomAccessFile, ossSystem, range, byteArrayOutputStream);
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
        String ossPath = ossSystem.getOssPath();
        if (StringUtils.isBlank(ossPath)) {
            return null;
        }
        return new File(ossPath, path);
    }
}
