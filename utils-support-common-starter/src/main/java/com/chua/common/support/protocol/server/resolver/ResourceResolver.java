package com.chua.common.support.protocol.server.resolver;

import com.chua.common.support.annotations.SpiIgnore;
import com.chua.common.support.image.filter.ImageFilter;
import com.chua.common.support.media.MediaType;
import com.chua.common.support.media.MediaTypeFactory;
import com.chua.common.support.objects.ConfigureObjectContext;
import com.chua.common.support.resource.repository.Metadata;
import com.chua.common.support.resource.repository.Repository;
import com.chua.common.support.utils.BufferedImageUtils;
import com.chua.common.support.utils.IoUtils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 解析器
 *
 * @author CH
 */
@SpiIgnore
public class ResourceResolver extends AbstractResolver {

    static Repository repository = Repository.current().add(Repository.classpath(true));
    private String name;

    public ResourceResolver(ConfigureObjectContext configureObjectContext) {
        super(configureObjectContext);
    }

    @Override
    public byte[] resolve(Object obj) {
        Repository resolve = repository.resolve(name);
        List<Metadata> metadata = resolve.getMetadata();
        if (metadata.isEmpty()) {
            return new byte[0];
        }

        Metadata metadata1 = metadata.get(0);
        try {
            byte[] bytes = IoUtils.toByteArray(metadata1.toUrl());
            if (metadata1.isImage()) {
                bytes = filter(bytes, metadata1);
            }
            return bytes;
        } catch (IOException ignored) {
        }
        return new byte[0];
    }

    /**
     * 过滤
     *
     * @param bytes     图片
     * @param metadata1 媒体
     * @return 过滤图片
     */
    private byte[] filter(byte[] bytes, Metadata metadata1) {
        Map<String, ImageFilter> beanMap = beanFactory.getBeanOfType(ImageFilter.class);
        BufferedImage bufferedImage = BufferedImageUtils.getBufferedImage(bytes);
        for (ImageFilter imageFilter : beanMap.values()) {
            try {
                bufferedImage = imageFilter.converter(bufferedImage);
            } catch (IOException e) {
                return bytes;
            }
        }


        return BufferedImageUtils.createByteArray(bufferedImage, metadata1.getContentType());
    }


    @Override
    public boolean hasResolve(Object obj) {
        if (!(obj instanceof String)) {
            return false;
        }

        this.name = obj.toString();

        Repository resolve = repository.resolve(name);
        List<Metadata> metadata = resolve.getMetadata();
        return !metadata.isEmpty();
    }

    @Override
    public String getContentType() {
        Optional<MediaType> mediaType = MediaTypeFactory.getMediaType(name);
        return mediaType.map(MediaType::toString).orElse("text/pain");
    }
}
