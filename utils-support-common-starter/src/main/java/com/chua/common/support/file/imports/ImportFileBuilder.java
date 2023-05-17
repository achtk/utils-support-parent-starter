package com.chua.common.support.file.imports;

import com.chua.common.support.file.export.ExportConfiguration;
import com.chua.common.support.file.export.ExportType;
import com.chua.common.support.file.xz.XZInputStream;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.value.Pair;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.List;
import java.util.zip.GZIPInputStream;

import static com.chua.common.support.constant.CommonConstant.GZ;
import static com.chua.common.support.constant.CommonConstant.XZ;

/**
 * 导出表
 *
 * @author CH
 */
@RequiredArgsConstructor(staticName = "read")
public class ImportFileBuilder {

    private final ExportConfiguration exportConfiguration = new ExportConfiguration();

    @NonNull
    private InputStream inputStream;
    private String type;

    /**
     * 编码
     *
     * @param charset 编码
     * @return this
     */
    public ImportFileBuilder charset(Charset charset) {
        return charset(charset.name());
    }

    /**
     * 编码
     *
     * @param charset 编码
     * @return this
     */
    public ImportFileBuilder charset(String charset) {
        exportConfiguration.charset(charset);
        return this;
    }

    /**
     * 文件类型
     *
     * @param type 文件类型
     * @return this
     */
    public ImportFileBuilder type(String type) {
        this.type = type;
        return this;
    }

    /**
     * 文件类型
     *
     * @param type 文件类型
     * @return this
     */
    public ImportFileBuilder type(ExportType type) {
        this.type = type.name().toLowerCase();
        return this;
    }

    /**
     * 获取名称
     *
     * @param field 字段
     * @return 名称
     */
    private Pair createName(Field field) {
        return exportConfiguration.namedResolver().name(field);
    }

    /**
     * 输出
     *
     * @param data 数据
     */
    public <T> List<T> doRead(Class<T> data) {
        ImportFile importFile = ServiceProvider.of(ImportFile.class).getNewExtension(type, exportConfiguration);
        return importFile.imports(inputStream, data);
    }

    /**
     * 输出
     *
     * @param data 数据
     */
    @SneakyThrows
    public <T> void doRead(Class<T> data, ImportListener<T> listener) {
        if (type.endsWith(GZ)) {
            type = type.replace(GZ, "");
            inputStream = new GZIPInputStream(inputStream);
        }

        if (type.endsWith(XZ)) {
            type = type.replace(XZ, "");
            inputStream = new XZInputStream(inputStream);
        }

        ImportFile importFile = ServiceProvider.of(ImportFile.class).getNewExtension(type, exportConfiguration);
        importFile.imports(inputStream, data, listener);
    }

}