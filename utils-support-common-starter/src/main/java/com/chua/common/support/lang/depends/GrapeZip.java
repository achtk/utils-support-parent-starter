package com.chua.common.support.lang.depends;

import com.chua.common.support.file.FileMedia;
import com.chua.common.support.file.zip.Zip;
import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.function.SafeFunction;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.chua.common.support.constant.CommonConstant.POM;

/**
 * grape
 *
 * @author CH
 */
public class GrapeZip implements InitializingAware {

    private final File file;
    private String dependencyPath;
    private ClassLoader parent;
    private InputStream stream;

    public GrapeZip(File file, String dependencyPath, ClassLoader parent) {
        this.file = file;
        this.dependencyPath = dependencyPath;
        this.parent = parent;
        afterPropertiesSet();
    }

    /**
     * 执行
     */
    public Surroundings execute() {
        return ServiceProvider.of(GrapeFileResolver.class).getNewExtension("default").resolve(stream, dependencyPath, parent, file);
    }

    @Override
    public void afterPropertiesSet() {
        if(FileUtils.isZip(file.getName())) {
            Zip zip = new Zip();
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                zip.unFile(fileInputStream, (SafeFunction<FileMedia, Boolean>) fileMedia -> {
                    if (POM.equals(fileMedia.getName())) {
                        GrapeZip.this.stream = fileMedia.getStream();
                        return true;
                    }

                    return false;
                }, true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return;
        }

        throw new UnsupportedOperationException();
    }
}
