package com.chua.common.support.file.export;

import com.chua.common.support.file.xz.LZMA2Options;
import com.chua.common.support.file.xz.XZOutputStream;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.IoUtils;
import com.chua.common.support.value.Pair;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.GZIPOutputStream;

import static com.chua.common.support.constant.CommonConstant.GZ;
import static com.chua.common.support.constant.CommonConstant.XZ;

/**
 * 导出表
 *
 * @author CH
 */
@RequiredArgsConstructor(staticName = "read")
public class ExportFileBuilder implements AutoCloseable {

    private final ExportConfiguration exportConfiguration = new ExportConfiguration();

    @NonNull
    private OutputStream outputStream;
    private String type;

    private final AtomicBoolean state = new AtomicBoolean(false);
    private ExportFile exportFile;

    /**
     * 表头
     *
     * @param headers 表头
     * @return this
     */
    public ExportFileBuilder header(Pair... headers) {
        exportConfiguration.header(headers);
        return this;
    }

    /**
     * 表头
     *
     * @param headerType 表头
     * @return this
     */
    public ExportFileBuilder header(Class<?> headerType) {
        List<Pair> tpl = new LinkedList<>();
        ClassUtils.doWithFields(headerType, field -> {
            Pair name = createName(field);
            if (null == name) {
                return;
            }
            tpl.add(name);
        });
        return header(tpl.toArray(new Pair[0]));
    }

    /**
     * 编码
     *
     * @param charset 编码
     * @return this
     */
    public ExportFileBuilder charset(Charset charset) {
        return charset(charset.name());
    }

    /**
     * 编码
     *
     * @param charset 编码
     * @return this
     */
    public ExportFileBuilder charset(String charset) {
        exportConfiguration.charset(charset);
        return this;
    }

    /**
     * 文件类型
     *
     * @param type 文件类型
     * @return this
     */
    public ExportFileBuilder type(String type) {
        this.type = type;
        return this;
    }

    /**
     * 文件类型
     *
     * @param type 文件类型
     * @return this
     */
    public ExportFileBuilder type(ExportType type) {
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
    @SneakyThrows
    public <T> void doRead(List<T> data) {
        state.set(true);
        if (type.endsWith(GZ)) {
            type = type.replace(GZ, "");
            outputStream = new GZIPOutputStream(outputStream);
        }

        if (type.endsWith(XZ)) {
            type = type.replace(XZ, "");
            outputStream = new XZOutputStream(outputStream, new LZMA2Options());
        }


        this.exportFile = ServiceProvider.of(ExportFile.class).getNewExtension(type, exportConfiguration);
        exportFile.export(outputStream, data);
    }

    /**
     * 是否已经初始化
     *
     * @return 是否已经初始化
     */
    public boolean isLoad() {
        return state.get();
    }

    /**
     * 追加
     *
     * @param records 数据
     */
    public <T> void doAppend(List<T> records) {
        exportFile.append(records);
    }

    @Override
    public void close() throws Exception {
        exportFile.close();
        IoUtils.closeQuietly(outputStream);
    }
}
