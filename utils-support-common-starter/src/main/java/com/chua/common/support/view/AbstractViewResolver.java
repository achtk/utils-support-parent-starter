package com.chua.common.support.view;

import com.chua.common.support.function.strategy.name.NamedStrategy;
import com.chua.common.support.image.filter.ImageFilter;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.StringUtils;
import com.chua.common.support.function.strategy.name.RejectStrategy;
import com.chua.common.support.view.viewer.SourceViewer;
import com.chua.common.support.view.viewer.Viewer;
import lombok.Getter;

import java.io.*;
import java.net.URLDecoder;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.chua.common.support.constant.CommonConstant.DOWNLOAD;
import static com.chua.common.support.constant.CommonConstant.JAR_URL_SEPARATOR;

/**
 * 视图解析器
 *
 * @author CH
 */
public abstract class AbstractViewResolver implements ViewResolver {
    protected ViewConfig config;

    protected Map<String, ImageFilter> imageFilterList = new ConcurrentHashMap<>();
    @Getter
    private String path;
    @Getter
    private String compressPath;
    @Getter
    private String sourcePath;

    @Override
    public ViewResolver setConfig(ViewConfig config) {
        this.config = config;
        return this;
    }

    @Override
    public ViewResolver addPlugin(String name, ImageFilter imageFilter) {
        imageFilterList.put(name, imageFilter);
        return this;
    }

    @Override
    public ViewResolver setPlugin(Map<String, ImageFilter> imageFilter) {
        this.imageFilterList = imageFilter;
        return this;
    }

    @Override
    public ViewPreview preview(String bucket, String path, String mode, OutputStream os, Set<String> pluginList) {
        try {
            path = URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException ignored) {
        }

        this.sourcePath = path;
        path = render(path);

        if (DOWNLOAD.equals(mode)) {
            if (null == os) {
                return ViewPreview.emptyDownloader();
            }
            ViewPreview viewPreview = beforePreview(bucket, path, mode, os);
            viewPreview.setContentType(ViewPreview.OCTET_STREAM);
            return viewPreview;
        }

        ViewPreview viewPreview;
        byte[] stream2;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            viewPreview = beforePreview(bucket, path, mode, outputStream);
            stream2 = outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (viewPreview.isImage() && !imageFilterList.isEmpty()) {
            renderImage(os, stream2, viewPreview, pluginList);
        } else {
            renderArray(os, stream2);
        }
        return viewPreview;
    }

    private String render(String path) {
        if (!path.contains(JAR_URL_SEPARATOR)) {
            return path;
        }
        int index = path.indexOf(JAR_URL_SEPARATOR);
        this.compressPath = path.substring(index + 2);
        return path.substring(0, index);
    }

    private void renderArray(OutputStream os, byte[] stream2) {
        try {
            os.write(stream2);
        } catch (IOException ignored) {
        }
    }

    /**
     * 渲染图片
     *
     * @param os          os
     * @param stream2     stream
     * @param viewPreview preview
     * @param pluginList  plugin
     */
    private void renderImage(OutputStream os, byte[] stream2, ViewPreview viewPreview, Set<String> pluginList) {
        String format = viewPreview.getContentType()
                .replace(";charset=UTF-8", "")
                .replace("image/", "");

        if (pluginList.isEmpty()) {
            pluginList.addAll(this.imageFilterList.keySet());
        }

        for (String name : pluginList) {
            ImageFilter imageFilter = this.imageFilterList.get(name);
            if (null == imageFilter) {
                continue;
            }

            imageFilter.getImageFormat(format);
            byte[] stream1 = converter(imageFilter, stream2);
            if (null == stream1) {
                break;
            }

            stream2 = stream1;
        }
        try {
            os.write(stream2);
        } catch (IOException ignored) {
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
     * 预览
     *
     * @param bucket bucket
     * @param path   预览的文件路径
     * @param mode   模式. download/preview
     * @param os     输出
     * @return ViewPreview
     */
    abstract protected ViewPreview beforePreview(String bucket, String path, String mode, OutputStream os);

    /**
     * 拒絕策略
     *
     * @param rejectStrategy 策略
     * @return 拒絕策略
     */
    protected RejectStrategy getRejectStrategy(String rejectStrategy) {
        return ServiceProvider.of(RejectStrategy.class).getExtension(rejectStrategy);
    }

    /**
     * 命名策略
     *
     * @param namedStrategy 策略
     * @return 拒絕策略
     */
    protected NamedStrategy getNamedStrategy(String namedStrategy) {
        return ServiceProvider.of(NamedStrategy.class).getExtension(namedStrategy);
    }

    /**
     * 获取解析器
     *
     * @param contentType 类型
     * @param mode        mode
     * @param suffix      文件后缀
     * @return Viewer
     */
    protected Viewer getViewer(String contentType, String mode, String suffix) {
        if (DOWNLOAD.equals(mode)) {
            return new SourceViewer();
        }

        Viewer extension = ServiceProvider.of(Viewer.class).getNewExtension(StringUtils.removeSuffix(contentType, ";charset=UTF-8"), new SourceViewer());
        if (null != extension && !(extension instanceof SourceViewer)) {
            return extension;
        }

        return new SourceViewer();

    }
}
