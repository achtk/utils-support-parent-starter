package com.chua.common.support.view;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.function.strategy.name.NamedStrategy;
import com.chua.common.support.media.MediaType;
import com.chua.common.support.media.MediaTypeFactory;
import com.chua.common.support.utils.FileUtils;
import com.chua.common.support.utils.StringUtils;
import com.chua.common.support.value.ContentTypeValue;
import com.chua.common.support.value.Value;
import com.chua.common.support.function.strategy.name.RejectStrategy;
import com.chua.common.support.view.viewer.Viewer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

/**
 * 本地文件视图解析器
 *
 * @author CH
 */
@Spi("local")
public class LocalViewResolver extends AbstractViewResolver {

    @Override
    public ViewPreview beforePreview(String bucket, String path, String mode, OutputStream os) {
        String filePath = config.getPath();
        File file = new File(filePath, path);

        if (!file.exists()) {
            RejectStrategy rejectStrategy = getRejectStrategy(config.getRejectStrategy());
            if (null != os) {
                rejectStrategy.reject(path, mode, os);
            }
            return ViewPreview.of(mode);
        }

        ViewPreview viewPreview = ViewPreview.of(mode);
        Optional<MediaType> mediaType = MediaTypeFactory.getMediaType(path);
        mediaType.ifPresent(type -> viewPreview.setContentType(type.toString()));

        if (null == os) {
            return viewPreview;
        }

        String suffix = FileUtils.getExtension(file.getName());
        Viewer viewer = super.getViewer(viewPreview.getContentType(), mode, suffix);
        try (FileInputStream fis = new FileInputStream(file)) {
            ContentTypeValue<byte[]> contentTypeValue = viewer.resolve(fis, os, mode, bucket + getSourcePath());
            viewPreview.setContentType(contentTypeValue.getContentType(viewPreview.getContentType()));
        } catch (Exception ignored) {
        }

        return viewPreview;
    }

    @Override
    public Value<String> storage(InputStream is, String bucket, String name) {
        NamedStrategy namedStrategy = getNamedStrategy(config.getNameStrategy());
        name = namedStrategy.named(name);
        String real = StringUtils.defaultString(bucket, "") + "/" + name;
        File file = new File(config.getPath(), real);
        FileUtils.mkParentDirs(file);

        StandardCopyOption[] copyOption = new StandardCopyOption[1];
        if (config.isCovering()) {
            copyOption[0] = StandardCopyOption.REPLACE_EXISTING;
        } else {
            copyOption = new StandardCopyOption[0];
        }

        try {
            Files.copy(is, file.toPath(), copyOption);
            return Value.of(real);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Value.of(null);
    }

    @Override
    public void destroy() {

    }
}
