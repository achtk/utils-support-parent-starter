package com.chua.common.support.objects.source;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.annotations.SpiIgnore;
import com.chua.common.support.file.Decompress;
import com.chua.common.support.file.FileMedia;
import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.objects.ConfigureContextConfiguration;
import com.chua.common.support.objects.classloader.ZipClassLoader;
import com.chua.common.support.objects.definition.TypeDefinition;
import com.chua.common.support.objects.scanner.BaseAnnotationResourceScanner;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static com.chua.common.support.constant.CommonConstant.EMPTY_ARRAY;
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
    private final boolean outSideInAnnotation;

    private ZipClassLoader classLoader;

    public ClassLoaderTypeDefinitionSource(String path, List<URL> urls, ZipClassLoader classLoader, boolean outSideInAnnotation) {
        super(ConfigureContextConfiguration.builder().build());
        this.path = path;
        this.urls = urls;
        this.outSideInAnnotation = outSideInAnnotation;
        this.classLoader = Optional.ofNullable(classLoader).orElse(new ZipClassLoader());
        this.classLoader.addDepends(urls);
        this.print();
        this.register(path);
    }

    /**
     * 打印
     */
    private void print() {
        File tmp = new File(path);
        log.info("装载 >>>> {}", tmp.getName());
        for (java.net.URL url : urls) {
            log.info("\t装载依赖 >>>> {}", FileUtils.getName(url.getPath()));
        }
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
        List<String> rs = analysis(path);
        if(!outSideInAnnotation) {
            registerAllOutSide(rs);
            return;
        }

        registerByAnnotation(rs);
    }

    /**
     * 分析
     *
     * @param path 路径
     * @return {@link List}<{@link String}>
     */
    private List<String> analysis(String path) {
        Decompress decompress = ServiceProvider.of(Decompress.class).getNewExtension(FileUtils.getExtension(path));
        if (null == decompress) {
            log.error("{}不支持安装", path);
            return Collections.emptyList();
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

        return classNames;
    }

    /**
     * 按注释注册
     *
     * @param path 路径
     */
    private void registerByAnnotation(List<String> classNames) {
        List<BaseAnnotationResourceScanner> collect = ServiceProvider.of(BaseAnnotationResourceScanner.class).collect(new Object[]{EMPTY_ARRAY});

        for (String className : classNames) {
            for (BaseAnnotationResourceScanner baseAnnotationResourceScanner : collect) {
                Class<?> aClass = ClassUtils.forName(className, classLoader);
                if(baseAnnotationResourceScanner.isMatch(aClass)) {
                    super.register(aClass);
                }
            }
        }
    }

    /**
     * 寄存器全部输出
     *
     * @param path 路径
     */
    private void registerAllOutSide(List<String> classNames) {
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
