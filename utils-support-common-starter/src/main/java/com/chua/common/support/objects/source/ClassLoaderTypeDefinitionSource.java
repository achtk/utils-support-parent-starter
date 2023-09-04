package com.chua.common.support.objects.source;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.annotations.SpiIgnore;
import com.chua.common.support.file.Decompress;
import com.chua.common.support.file.FileMedia;
import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.objects.ConfigureContextConfiguration;
import com.chua.common.support.objects.classloader.ZipClassLoader;
import com.chua.common.support.objects.definition.TypeDefinition;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static com.chua.common.support.constant.CommonConstant.SUFFIX_CLASS;

/**
 * 类型定义源
 *
 * @author CH
 * @since 2023/09/02
 */
@SuppressWarnings("ALL")
@Slf4j
@Spi("classloader")
@SpiIgnore
public class ClassLoaderTypeDefinitionSource extends AbstractTypeDefinitionSource implements TypeDefinitionSource, AutoCloseable, InitializingAware {

    private final String path;
    private final List<URL> urls;

    private ZipClassLoader classLoader;

    public ClassLoaderTypeDefinitionSource(String path, List<URL> urls, ZipClassLoader classLoader) {
        super(ConfigureContextConfiguration.builder().build());
        this.path = path;
        this.urls = urls;
        this.classLoader = Optional.ofNullable(classLoader).orElse(new ZipClassLoader());
        this.classLoader.addDepends(urls);
        this.register(path);
    }

    @Override
    public void afterPropertiesSet() {
        register(path);
    }

    @Override
    public boolean isMatch(TypeDefinition typeDefinition) {
        return typeDefinition instanceof ClassLoaderTypeDefinitionSource;
    }
    /**
     * 注册
     *
     * @param path 路径
     */
    public void register(String path) {
        Decompress decompress = ServiceProvider.of(Decompress.class).getNewExtension(FileUtils.getExtension(path));
        if (null == decompress) {
            log.error("{}不支持安装", path);
            return;
        }

        List<String> classNames = new LinkedList<>();

        try (FileInputStream fos = new FileInputStream(path)) {
            decompress.unFile(fos, new Function<FileMedia, Boolean>() {
                        @Override
                        public Boolean apply(FileMedia metadata) {
                            if (metadata.getName().endsWith(SUFFIX_CLASS)) {
                                String name = metadata.getName().replace("/", ".").replace(SUFFIX_CLASS, "");
                                classLoader.add(name, metadata.getStream());
                                classNames.add(name);
                            }
                            return false;
                        }
                    },
                    true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (String className : classNames) {
            super.register(ClassUtils.forName(className, classLoader));
        }
    }

    @Override
    public void close() {
        urls.clear();
        try {
            classLoader.close();
        } catch (Exception e) {
        }
    }
}
