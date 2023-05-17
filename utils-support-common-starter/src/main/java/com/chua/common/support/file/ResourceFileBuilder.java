package com.chua.common.support.file;

import com.chua.common.support.binary.ByteSource;
import com.chua.common.support.binary.ByteSourceArray;
import com.chua.common.support.binary.ByteSourceFile;
import com.chua.common.support.binary.ByteSourceUrl;
import com.chua.common.support.file.folder.FolderResourceFile;
import com.chua.common.support.media.MediaType;
import com.chua.common.support.media.MediaTypeFactory;
import com.chua.common.support.resource.ResourceConfiguration;
import com.chua.common.support.resource.ResourceProvider;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.ContentTypeUtils;
import com.chua.common.support.utils.FileUtils;
import com.chua.common.support.utils.IoUtils;
import com.chua.common.support.utils.StringUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Optional;

import static com.chua.common.support.constant.CommonConstant.UNKNOWN;

/**
 * 文件构造器
 *
 * @author CH
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE, staticName = "of")
public class ResourceFileBuilder {

    private final ResourceFileConfiguration configuration = ResourceFileConfiguration.builder().build();
    public static final String BASE64 = ";base64,";

    /**
     * 初始化
     *
     * @return 构造器
     */
    public static ResourceFileBuilder builder() {
        return new ResourceFileBuilder();
    }

    /**
     * 块大小
     *
     * @param buffer 块大小
     * @return this
     */
    public ResourceFileBuilder buffer(int buffer) {
        this.configuration.setBuffer(buffer);
        return this;
    }


    /**
     * 文件类型(后缀)
     *
     * @param type 文件类型(后缀)
     * @return this
     */
    public ResourceFileBuilder type(String type) {
        this.configuration.setType(type);
        return this;
    }

    /**
     * 编码
     *
     * @param charset 编码
     * @return this
     */
    public ResourceFileBuilder charset(Charset charset) {
        this.configuration.setCharset(charset);
        return this;
    }

    /**
     * 获取资源文件
     *
     * @param file 文件
     * @return 结果
     */
    public ResourceFile open(File file) {
        return open(file.getAbsolutePath());
    }

    private ResourceFile openFile(File file) {
        configuration.setByteSource(new ByteSourceFile(file));
        String mimeType = UNKNOWN;
        Optional<MediaType> mediaType = MediaTypeFactory.getMediaType(file.getName());
        if (mediaType.isPresent()) {
            MediaType mediaType1 = mediaType.get();
            mimeType = mediaType1.toString();
            configuration.setSubtype(mediaType1.subtype());
        } else {
            try (InputStream inputStream = configuration.getByteSource().getInputStream()) {
                mimeType = IoUtils.getMimeType(inputStream);
            } catch (IOException ignored) {
            }
        }

        configuration.setContentType(mimeType);

        String type = configuration.getType();
        if (StringUtils.isNullOrEmpty(type) || UNKNOWN.equals(type)) {
            configuration.setType(FileUtils.getSimpleExtension(file));
        }

        configuration.setSourceUrl(file.getAbsolutePath());
        configuration.setSource(file);

        configuration.setType(check(configuration.getType()));

        String suffix = configuration.getType();
        ResourceFile resourceFile = ServiceProvider.of(ResourceFile.class).getNewExtension(suffix, configuration);
        if (null != resourceFile) {
            return resourceFile;
        }

        while (!StringUtils.isNullOrEmpty(suffix = FileUtils.getSimpleExtension(suffix))) {
            resourceFile = ServiceProvider.of(ResourceFile.class).getNewExtension(suffix, configuration);
            return resourceFile;
        }

        return ServiceProvider.of(ResourceFile.class).getNewExtension("url", configuration);
    }

    /**
     * 获取资源文件
     *
     * @param inputStream 文件
     * @param name        文件名
     * @return 结果
     */
    public ResourceFile open(InputStream inputStream, String name) {
        MediaType mediaType = MediaTypeFactory.getMediaType(name).get();
        String subtype = check(mediaType.subtype());
        try {
            configuration.setByteSource(new ByteSourceArray(IoUtils.toByteArray(inputStream)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ServiceProvider.of(ResourceFile.class).getNewExtension(subtype, configuration);
    }

    /**
     * 获取资源文件
     *
     * @param file 文件
     * @return 结果
     */
    public ResourceFile open(String file) {
        File temp = new File(file);
        if (temp.exists() && temp.isDirectory()) {
            configuration.setSource(temp);
            return new FolderResourceFile(configuration);
        }

        if (temp.exists()) {
            return openFile(temp);
        }


        String name = createResourceFile(file);
        ByteSource byteSource = configuration.getByteSource();

        if (byteSource instanceof ByteSourceUrl) {
            if (((ByteSourceUrl) byteSource).isFile()) {
                return open(new File(((ByteSourceUrl) byteSource).getFile()));
            }
        }
        name = check(name);

        return ServiceProvider.of(ResourceFile.class).getNewExtension(name, configuration);
    }

    /**
     * 检测数据
     *
     * @param name 名称
     * @return 名称
     */
    private String check(String name) {
        String contentType = configuration.getContentType();
        return null != contentType && contentType.startsWith("image/") ? "image" : name;
    }

    /**
     * 创建资源文件
     *
     * @param file 文件
     * @return 资源文件
     */
    private String createResourceFile(String file) {
        if (file.contains(BASE64)) {
            String contentType = file.substring(5, file.indexOf(BASE64));
            byte[] bytes = Base64.getDecoder().decode(file.substring(file.indexOf(BASE64) + BASE64.length()));
            configuration.setContentType(contentType);
            configuration.setType(ContentTypeUtils.getType(contentType));
            configuration.setByteSource(new ByteSourceArray(bytes));
            return "base64";
        }

        File temp = new File(file);

        if (temp.exists() && temp.isDirectory()) {
            return "folder";
        }

        ByteSourceUrl byteSourceUrl = null;
        try {
            byteSourceUrl = new ByteSourceUrl(new URL(file));
        } catch (IOException ignored) {
            try {
                byteSourceUrl = new ByteSourceUrl(ResourceProvider.of(file).getResource().getUrl());
            } catch (Throwable ignored1) {
            }
        }
        configuration.setByteSource(byteSourceUrl);

        configuration.setSourceUrl(file);
        String contentType = UNKNOWN;
        try {
            try (InputStream inputStream = configuration.getByteSource().getInputStream()) {
                contentType = IoUtils.getMimeType(inputStream);
            } catch (IOException ignored) {
            }
        } catch (Exception ignored) {
        }

        contentType = null == contentType ? UNKNOWN : contentType;

        configuration.setContentType(contentType);
        configuration.setType(ContentTypeUtils.getType(contentType));
        return "url";
    }

}
